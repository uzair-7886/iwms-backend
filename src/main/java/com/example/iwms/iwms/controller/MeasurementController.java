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

    @Autowired private UserRepository userRepository;
    @Autowired private WeightMeasurementRepository weightRepo;
    @Autowired private TemperatureMeasurementRepository temperatureRepo;
    @Autowired private HeartRateMeasurementRepository heartRateRepo;
    @Autowired private GlucoseMeasurementRepository glucoseRepo;
    @Autowired private BloodPressureMeasurementRepository bpRepo;
    @Autowired private BloodOxygenMeasurementRepository oxygenRepo;
    @Autowired private LatestHealthMetricsRepository latestMetricsRepo;
    @Autowired private LogRepository logRepo;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/add/weight")
    public ResponseEntity<String> addWeightMeasurement(
            HttpServletRequest request,
            @RequestParam Float weight
    ) {
        return saveMeasurement(
            getUserIdFromToken(request),
            weight,      // weight
            null,        // heartRate
            null,        // systolic
            null,        // diastolic
            null,        // bloodOxygen
            null,        // temperature
            null,        // glucoseLevel
            "Added weight measurement"
        );
    }

    @PostMapping("/add/temperature")
    public ResponseEntity<String> addTemperatureMeasurement(
            HttpServletRequest request,
            @RequestParam Float temperature
    ) {
        return saveMeasurement(
            getUserIdFromToken(request),
            null,        // weight
            null,        // heartRate
            null,        // systolic
            null,        // diastolic
            null,        // bloodOxygen
            temperature, // temperature
            null,        // glucoseLevel
            "Added temperature measurement"
        );
    }

    @PostMapping("/add/heart-rate")
    public ResponseEntity<String> addHeartRateMeasurement(
            HttpServletRequest request,
            @RequestParam Integer heartRate
    ) {
        return saveMeasurement(
            getUserIdFromToken(request),
            null,        // weight
            heartRate,   // heartRate
            null,        // systolic
            null,        // diastolic
            null,        // bloodOxygen
            null,        // temperature
            null,        // glucoseLevel
            "Added heart rate measurement"
        );
    }

    @PostMapping("/add/blood-pressure")
    public ResponseEntity<String> addBloodPressureMeasurement(
            HttpServletRequest request,
            @RequestParam Float systolic,
            @RequestParam Float diastolic
    ) {
        return saveMeasurement(
            getUserIdFromToken(request),
            null,        // weight
            null,        // heartRate
            systolic,    // systolic
            diastolic,   // diastolic
            null,        // bloodOxygen
            null,        // temperature
            null,        // glucoseLevel
            "Added blood pressure measurement"
        );
    }

    @PostMapping("/add/blood-oxygen")
    public ResponseEntity<String> addBloodOxygenMeasurement(
            HttpServletRequest request,
            @RequestParam Float bloodOxygen
    ) {
        return saveMeasurement(
            getUserIdFromToken(request),
            null,        // weight
            null,        // heartRate
            null,        // systolic
            null,        // diastolic
            bloodOxygen, // bloodOxygen
            null,        // temperature
            null,        // glucoseLevel
            "Added blood oxygen measurement"
        );
    }

    @PostMapping("/add/glucose")
    public ResponseEntity<String> addGlucoseMeasurement(
            HttpServletRequest request,
            @RequestParam Float glucoseLevel
    ) {
        return saveMeasurement(
            getUserIdFromToken(request),
            null,          // weight
            null,          // heartRate
            null,          // systolic
            null,          // diastolic
            null,          // bloodOxygen
            null,          // temperature
            glucoseLevel,  // glucoseLevel
            "Added glucose measurement"
        );
    }

    private ResponseEntity<String> saveMeasurement(
            Long userId,
            Float weight,
            Integer heartRate,
            Float systolic,
            Float diastolic,
            Float bloodOxygen,
            Float temperature,
            Float glucoseLevel,
            String action
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Timestamp now = Timestamp.from(Instant.now());

        if (weight != null)
            weightRepo.save(new WeightMeasurement(null, user, weight, now));
        if (heartRate != null)
            heartRateRepo.save(new HeartRateMeasurement(null, user, heartRate, now));
        if (systolic != null && diastolic != null)
            bpRepo.save(new BloodPressureMeasurement(null, user, systolic, diastolic, now));
        if (bloodOxygen != null)
            oxygenRepo.save(new BloodOxygenMeasurement(null, user, bloodOxygen, now));
        if (temperature != null)
            temperatureRepo.save(new TemperatureMeasurement(null, user, temperature, now));
        if (glucoseLevel != null)
            glucoseRepo.save(new GlucoseMeasurement(null, user, glucoseLevel, now));

        LatestHealthMetrics latest = latestMetricsRepo.findByUser(user)
                .orElse(new LatestHealthMetrics());
        latest.setUser(user);
        if (weight != null)      latest.setWeight(weight);
        if (heartRate != null)   latest.setHeartRate(heartRate);
        if (systolic != null && diastolic != null) {
            latest.setSystolic(systolic.intValue());
            latest.setDiastolic(diastolic.intValue());
        }
        if (bloodOxygen != null) latest.setBloodOxygen(bloodOxygen);
        if (temperature != null) latest.setTemperature(temperature);
        if (glucoseLevel != null) latest.setGlucoseLevel(glucoseLevel);
        latest.setLastUpdated(now);
        latestMetricsRepo.save(latest);

        logRepo.save(new Log(null, user, action, now, "127.0.0.1"));
        return ResponseEntity.ok("Measurement added and latest metrics updated.");
    }

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
}
