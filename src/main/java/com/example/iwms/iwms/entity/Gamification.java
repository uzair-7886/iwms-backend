package com.example.iwms.iwms.entity;

import java.sql.Timestamp;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;
import jakarta.persistence.Lob;



@Entity
@Data
@Table(name = "gamification")
public class Gamification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gamificationId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    private Integer points;
    @Lob
    private String badges; // JSON stored as String
    private Integer challengesCompleted;
    private Integer leaderboardRank;

    @UpdateTimestamp
    private Timestamp lastUpdated;
}
