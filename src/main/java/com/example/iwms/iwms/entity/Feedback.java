package com.example.iwms.iwms.entity;

import java.sql.Timestamp;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

import jakarta.persistence.Lob;


@Entity
@Data
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @Lob
    private String feedbackText;

    private Integer rating;

    @CreationTimestamp
    private Timestamp submittedAt;
}
