package com.example.iwms.iwms.entity;

import java.sql.Timestamp;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Data
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deviceId;

    private String deviceName;
    private String deviceType;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private Timestamp lastConnected;
}
