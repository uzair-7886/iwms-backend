package com.example.iwms.iwms.controller;

import com.example.iwms.iwms.entity.User;
import com.example.iwms.iwms.repository.UserRepository;
import com.example.iwms.iwms.service.UserDetailsServiceImpl;
import com.example.iwms.iwms.utils.JwtUtil;
import com.example.iwms.iwms.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.iwms.iwms.repository.SecurityRepository;
import com.example.iwms.iwms.entity.Security;
import java.security.Timestamp;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository, SecurityRepository securityRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        // Check if a user with the same email or phone already exists
        if (userRepository.findByEmailOrPhone(user.getEmailOrPhone()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email or phone already exists!");
        }

        // Encode the user's password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        // Save the user to the database
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        try {
            // Authenticate user
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            // Load user details and generate JWT
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // Retrieve the User entity
            User user = userRepository.findByEmailOrPhone(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            // Create and save Security record
            Security security = new Security();
            security.setUser(user);
            security.setAuthToken(token);
            security.setExpiration(null);
            securityRepository.save(security);

            return ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password!");
        }
    }
}
