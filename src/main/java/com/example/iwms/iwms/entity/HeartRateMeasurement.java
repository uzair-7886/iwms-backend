package com.example.iwms.iwms.entity;

import java.sql.Timestamp;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "heart_rate_measurements")
public class HeartRateMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long heartRateId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private Integer heartRate;
    private Timestamp timestamp;
}
