package com.example.iwms.iwms.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;

import lombok.Data;

@Entity
@Data
@Table(name = "latest_health_metrics")
public class LatestHealthMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long metricId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private Float weight;
    private Integer systolic;
    private Integer diastolic;
    private Integer heartRate;
    private Float temperature;
    private Float bloodOxygen;
    private Float glucoseLevel;
    private Boolean abnormalFlag;

    @UpdateTimestamp
    private Timestamp lastUpdated;
}

