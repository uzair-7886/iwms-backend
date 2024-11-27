package com.example.iwms.iwms.entity;

import java.sql.Timestamp;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;


@Entity
@Data
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @OneToOne
    @JoinColumn(name = "metric_id", referencedColumnName = "metricId")
    private LatestHealthMetrics metric;

    private String alertText;
    private Boolean isRead;

    @CreationTimestamp
    private Timestamp createdAt;
}

