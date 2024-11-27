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

@Entity
@Data
@Table(name = "blood_pressure_measurements")
public class BloodPressureMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bpId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private Float systolic;
    private Float diastolic;
    private Timestamp timestamp;
}
