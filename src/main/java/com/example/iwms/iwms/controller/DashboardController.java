package com.example.iwms.iwms.controller;

import com.example.iwms.iwms.entity.User;
import com.example.iwms.iwms.repository.UserRepository;
import com.example.iwms.iwms.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dashboard")
// @CrossOrigin(origins = "http://localhost:5173")
public class DashboardController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public DashboardController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<Object> getDashboard(HttpServletRequest request) {
        try {
            // Extract token from Authorization header
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                     .body("Missing or invalid Authorization header");
            }
    
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
    
            // Try finding the user by email/phone first.
            Optional<User> userOptional = userRepository.findByEmailOrPhone(username);
            User user;
            if (userOptional.isPresent()) {
                user = userOptional.get();
            } else {
                // If not found, try to parse the username as a userId and look it up.
                try {
                    Long userId = Long.parseLong(username);
                    user = userRepository.findById(userId)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
                } catch (NumberFormatException ex) {
                    throw new UsernameNotFoundException("User not found: " + username);
                }
            }
    
            // Return user details
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body("Invalid token or user not found");
        }
    }
    
}
