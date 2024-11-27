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
@Table(name = "glucose_measurements")
public class GlucoseMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long glucoseId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private Float glucoseLevel;
    private Timestamp timestamp;
}
