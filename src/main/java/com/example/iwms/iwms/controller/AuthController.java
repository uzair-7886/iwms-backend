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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final LogRepository logRepository;

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

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user, HttpServletRequest request) {
        if (userRepository.findByEmailOrPhone(user.getEmailOrPhone()).isPresent()) {
            Log log = new Log();
            log.setAction("User Already Exists");
            log.setTimestamp(new Timestamp(System.currentTimeMillis()));
            log.setIpAddress(request.getRemoteAddr());
            logRepository.save(log);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User with email or phone already exists!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole("USER");
        user = userRepository.save(user);

        Log log = new Log();
        log.setUser(user);
        log.setAction("User Registered");
        log.setTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setIpAddress(request.getRemoteAddr());
        logRepository.save(log);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtUtil.generateToken(userDetails.getUsername());

            User user = userRepository.findByEmailOrPhone(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password!");
        }
    }

    // @GetMapping("/dashboard")
    // public ResponseEntity<Object> getDashboard(HttpServletRequest request) {
    //     try {
    //         // Extract token from Authorization header
    //         String authHeader = request.getHeader("Authorization");
    //         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
    //         }

    //         String token = authHeader.substring(7);
    //         String username = jwtUtil.extractUsername(token);

    //         // Retrieve user from the database
    //         User user = userRepository.findByEmailOrPhone(username)
    //                 .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

    //         // Return user details
    //         return ResponseEntity.ok(user);

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or user not found");
    //     }
    // }

}
