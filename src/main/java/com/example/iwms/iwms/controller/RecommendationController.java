package com.example.iwms.iwms.controller;

import com.example.iwms.iwms.dto.RecommendationDto;
import com.example.iwms.iwms.entity.LatestHealthMetrics;
import com.example.iwms.iwms.entity.Recommendation;
import com.example.iwms.iwms.entity.User;
import com.example.iwms.iwms.entity.RecommendationStatus;
import com.example.iwms.iwms.repository.RecommendationRepository;
import com.example.iwms.iwms.repository.LatestHealthMetricsRepository;
import com.example.iwms.iwms.repository.UserRepository;
import com.example.iwms.iwms.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired private RecommendationRepository recommendationRepo;
    @Autowired private LatestHealthMetricsRepository latestMetricsRepo;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByEmailOrPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user.getUserId();
    }

     @PostMapping("/add")
    public ResponseEntity<String> addRecommendations(
            HttpServletRequest request,
            @RequestBody List<RecommendationDto> recommendations
    ) {
        // 1) authenticate
        Long userId = getUserIdFromToken(request);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2) fetch the user’s latest metrics
        LatestHealthMetrics metric = latestMetricsRepo.findByUser(user)
            .orElseThrow(() -> new RuntimeException("No metrics found for user: " + userId));

        // 3) map each DTO → entity
        List<Recommendation> toSave = recommendations.stream().map(dto -> {
            Recommendation rec = new Recommendation();
            rec.setUser(user);
            rec.setMetric(metric);
            rec.setVital(dto.getVital());
            rec.setRecommendationText(dto.getMessage());
            rec.setStatus(RecommendationStatus.PENDING);
            return rec;
        }).collect(Collectors.toList());

        // 4) persist them all at once
        recommendationRepo.saveAll(toSave);
        return ResponseEntity.ok("Recommendations saved successfully");
    }

    @GetMapping
    public ResponseEntity<List<Map<String,String>>> getRecommendations(HttpServletRequest request) {
        // 1) authenticate
        Long userId = getUserIdFromToken(request);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 2) load just their Recommendation entities
        List<Recommendation> recs = recommendationRepo.findByUser(user);

        // 3) map to List<Map<"vital",...>,<"text",...>>
        List<Map<String,String>> out = recs.stream()
            .map(r -> Map.of(
                "vital", r.getVital(),
                "text",  r.getRecommendationText()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(out);
    }


}
