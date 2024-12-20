package com.example.iwms.iwms.controller;

import com.example.iwms.iwms.entity.*;
import com.example.iwms.iwms.repository.*;
import com.example.iwms.iwms.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeightMeasurementRepository weightRepo;

    @Autowired
    private TemperatureMeasurementRepository temperatureRepo;

    @Autowired
    private HeartRateMeasurementRepository heartRateRepo;

    @Autowired
    private GlucoseMeasurementRepository glucoseRepo;

    @Autowired
    private BloodPressureMeasurementRepository bpRepo;

    @Autowired
    private BloodOxygenMeasurementRepository oxygenRepo;

    @Autowired
    private LatestHealthMetricsRepository latestMetricsRepo;

    @Autowired
    private LogRepository logRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/add/weight")
    public ResponseEntity<String> addWeightMeasurement(HttpServletRequest request, @RequestParam Float weight) {
        return saveMeasurement(getUserIdFromToken(request), weight, null, null, null, null, null, "Added weight measurement");
        // return ResponseEntity.ok("Measurement added and latest metrics updated.");
    }

    @PostMapping("/add/temperature")
    public ResponseEntity<String> addTemperatureMeasurement(HttpServletRequest request, @RequestParam Float temperature) {
        return saveMeasurement(getUserIdFromToken(request), null, null, null, null, null, temperature, "Added temperature measurement");
    }

    @PostMapping("/add/heart-rate")
    public ResponseEntity<String> addHeartRateMeasurement(HttpServletRequest request, @RequestParam Integer heartRate) {
        return saveMeasurement(getUserIdFromToken(request), null, heartRate, null, null, null, null, "Added heart rate measurement");
    }

    @PostMapping("/add/blood-pressure")
    public ResponseEntity<String> addBloodPressureMeasurement(
            HttpServletRequest request,
            @RequestParam Float systolic,
            @RequestParam Float diastolic) {
        return saveMeasurement(getUserIdFromToken(request), null, null, systolic, diastolic, null, null, "Added blood pressure measurement");
    }

    @PostMapping("/add/blood-oxygen")
    public ResponseEntity<String> addBloodOxygenMeasurement(HttpServletRequest request, @RequestParam Float bloodOxygen) {
        return saveMeasurement(getUserIdFromToken(request), null, null, null, null, bloodOxygen, null, "Added blood oxygen measurement");
    }

    @PostMapping("/add/glucose")
    public ResponseEntity<String> addGlucoseMeasurement(HttpServletRequest request, @RequestParam Float glucoseLevel) {
        return saveMeasurement(getUserIdFromToken(request), null, null, null, null, null, glucoseLevel, "Added glucose measurement");
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        // Find user by username
        User user = userRepository.findByEmailOrPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return user.getUserId();
    }

    private ResponseEntity<String> saveMeasurement(Long userId, Float weight, Integer heartRate, Float systolic, Float diastolic, Float bloodOxygen, Float temperature, String action) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Timestamp now = Timestamp.from(Instant.now());

        // Update individual tables
        if (weight != null) weightRepo.save(new WeightMeasurement(null, user, weight, now));
        if (heartRate != null) heartRateRepo.save(new HeartRateMeasurement(null, user, heartRate, now));
        if (systolic != null && diastolic != null)
            bpRepo.save(new BloodPressureMeasurement(null, user, systolic, diastolic, now));
        if (bloodOxygen != null) oxygenRepo.save(new BloodOxygenMeasurement(null, user, bloodOxygen, now));
        if (temperature != null) temperatureRepo.save(new TemperatureMeasurement(null, user, temperature, now));
        // if (glucoseLevel != null) glucoseRepo.save(new GlucoseMeasurement(null, user, glucoseLevel, now));

        // Update LatestHealthMetrics
        LatestHealthMetrics latestMetrics = latestMetricsRepo.findByUser(user).orElse(new LatestHealthMetrics());
        latestMetrics.setUser(user);
        if (weight != null) latestMetrics.setWeight(weight);
        if (heartRate != null) latestMetrics.setHeartRate(heartRate);
        if (systolic != null && diastolic != null) {
            latestMetrics.setSystolic(systolic.intValue());
            latestMetrics.setDiastolic(diastolic.intValue());
        }
        if (bloodOxygen != null) latestMetrics.setBloodOxygen(bloodOxygen);
        if (temperature != null) latestMetrics.setTemperature(temperature);
        latestMetrics.setLastUpdated(now);
        latestMetricsRepo.save(latestMetrics);

        // Update logs
        logRepo.save(new Log(null, user, action, now, "127.0.0.1")); // IP address is hardcoded for simplicity

        return ResponseEntity.ok("Measurement added and latest metrics updated.");
    }
}
