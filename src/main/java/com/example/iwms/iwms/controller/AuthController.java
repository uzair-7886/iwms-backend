package com.example.iwms.iwms.controller;

import com.example.iwms.iwms.entity.Log;
import com.example.iwms.iwms.entity.Security;
import com.example.iwms.iwms.entity.User;
import com.example.iwms.iwms.repository.LogRepository;
import com.example.iwms.iwms.repository.SecurityRepository;
import com.example.iwms.iwms.repository.UserRepository;
import com.example.iwms.iwms.service.UserDetailsServiceImpl;
import com.example.iwms.iwms.utils.JwtUtil;
import com.example.iwms.iwms.dto.LoginRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder, UserRepository userRepository,
                          SecurityRepository securityRepository, LogRepository logRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.logRepository = logRepository;
    }

    // Registration request DTO.
    public static class RegistrationRequest {
        public String name;
        public String emailOrPhone;    // Optional
        public String password;        // Optional
        public String dob;             // Expected format: "YYYY-MM-DD" (Optional)
        public String gender;          // Optional
        public String languagePreference; // Optional
        public String role;            // Optional; defaults to "USER"
        public String faceImage;       // Base64 encoded image (Optional)
    }

    /**
     * Unified registration endpoint.
     * A user must register with at least one method:
     * - Credentials (emailOrPhone and password) and/or
     * - A faceImage.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest req, HttpServletRequest request) {
        // Validate that at least one authentication method is provided.
        if ((req.emailOrPhone == null || req.emailOrPhone.isEmpty() || req.password == null || req.password.isEmpty())
            && (req.faceImage == null || req.faceImage.isEmpty())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("At least one authentication method must be provided: either credentials (email/phone and password) or a face image.");
        }
        
        // If credentials are provided, check for duplicate emailOrPhone.
        if (req.emailOrPhone != null && !req.emailOrPhone.isEmpty() &&
            req.password != null && !req.password.isEmpty()) {
            if (userRepository.findByEmailOrPhone(req.emailOrPhone).isPresent()) {
                Log log = new Log();
                log.setAction("User Already Exists");
                log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                log.setIpAddress(request.getRemoteAddr());
                logRepository.save(log);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User with email or phone already exists!");
            }
        }
        
        // Create a new user.
        User user = new User();
        user.setName(req.name);
        user.setEmailOrPhone(req.emailOrPhone);
        if (req.password != null && !req.password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.password));
        }
        if (req.dob != null && !req.dob.isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(req.dob, DateTimeFormatter.ISO_LOCAL_DATE);
                user.setDob(dob);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid date format for dob. Use YYYY-MM-DD.");
            }
        }
        user.setGender(req.gender);
        user.setLanguagePreference(req.languagePreference);
        user.setRole((req.role != null && !req.role.isEmpty()) ? req.role : "USER");

        // If a face image is provided, obtain its embedding from the Flask service.
        if (req.faceImage != null && !req.faceImage.isEmpty()) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                Map<String, String> flaskRequest = new HashMap<>();
                flaskRequest.put("image", req.faceImage);
                ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:5000/embedding", flaskRequest, Map.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    String embeddingStr = objectMapper.writeValueAsString(response.getBody().get("embedding"));
                    user.setFaceEmbedding(embeddingStr);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Face embedding service error");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error processing face image: " + e.getMessage());
            }
        }
        user = userRepository.save(user);
        Log log = new Log();
        log.setUser(user);
        log.setAction("User Registered");
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setIpAddress(request.getRemoteAddr());
        logRepository.save(log);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully with ID: " + user.getUserId());
    }

    // Standard login endpoint using credentials.
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            // UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // String token = jwtUtil.generateToken(userDetails.getUsername());

            User user = userRepository.findByEmailOrPhone(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                    String subject = user.getEmailOrPhone();
                    if (subject == null || subject.isEmpty()) {
                        // face-only user: fall back to their DB id
                        subject = user.getUserId().toString();
                    }
                    
                    UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
                    String token = jwtUtil.generateToken(userDetails.getUsername());

            Security security = new Security();
            security.setUser(user);
            security.setAuthToken(token);
            security.setExpiration(new Timestamp(jwtUtil.getExpirationDateFromToken(token).getTime()));
            securityRepository.save(security);

            Log log = new Log();
            log.setUser(user);
            log.setAction("User Logged In");
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            log.setIpAddress(request.getRemoteAddr());
            logRepository.save(log);

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            Log log = new Log();
            log.setAction("Failed Login Attempt");
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            log.setIpAddress(request.getRemoteAddr());
            logRepository.save(log);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password!");
        }
    }

    /**
     * Face login endpoint.
     * Accepts a JSON payload: { "image": "<base64 image string>" }
     * The image is sent to the Flask service to compute its embedding, which is then compared to stored embeddings.
     */
    @PostMapping("/face/login")
    public ResponseEntity<String> faceLogin(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        if (!payload.containsKey("image")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing image data");
        }
        String imageData = payload.get("image");
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> flaskRequest = new HashMap<>();
            flaskRequest.put("image", imageData);
            ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:5000/embedding", flaskRequest, Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Face embedding service error");
            }
            List<Double> queryEmbedding = objectMapper.convertValue(response.getBody().get("embedding"), new TypeReference<List<Double>>() {});
            // Retrieve all users with a registered face embedding.
            List<User> usersWithFace = userRepository.findAll().stream()
                    .filter(u -> u.getFaceEmbedding() != null)
                    .collect(Collectors.toList());
            double threshold = 0.7;
            
            for (User user : usersWithFace) {
                List<Double> storedEmbedding = objectMapper.readValue(user.getFaceEmbedding(), new TypeReference<List<Double>>() {});
                double similarity = cosineSimilarity(queryEmbedding, storedEmbedding);
                System.out.println("Comparing user " + user.getUserId() + ": similarity = " + similarity);
                if (similarity > threshold) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(
                        user.getEmailOrPhone() != null ? user.getEmailOrPhone() : user.getUserId().toString());
                    String token = jwtUtil.generateToken(userDetails.getUsername());
                    
                    Security security = new Security();
                    security.setUser(user);
                    security.setAuthToken(token);
                    security.setExpiration(new Timestamp(jwtUtil.getExpirationDateFromToken(token).getTime()));
                    securityRepository.save(security);
                    
                    Log log = new Log();
                    log.setUser(user);
                    log.setAction("User Face Logged In");
                    log.setTimestamp(new Timestamp(System.currentTimeMillis()));
                    log.setIpAddress(request.getRemoteAddr());
                    logRepository.save(log);

                    // System.out.println(token);
                    
                    return ResponseEntity.ok(token);
                }
            }
            Log log = new Log();
            log.setAction("Failed Face Login Attempt");
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            log.setIpAddress(request.getRemoteAddr());
            logRepository.save(log);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Face authentication failed: no matching face found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Utility method to compute cosine similarity between two vectors.
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) return 0.0;
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < vec1.size(); i++) {
            double a = vec1.get(i);
            double b = vec2.get(i);
            dot += a * b;
            normA += a * a;
            normB += b * b;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
