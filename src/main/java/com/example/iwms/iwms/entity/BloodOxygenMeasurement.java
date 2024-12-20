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
@Table(name = "blood_oxygen_measurements")
public class BloodOxygenMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long oxygenId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private Float bloodOxygen;
    private Timestamp timestamp;
}
