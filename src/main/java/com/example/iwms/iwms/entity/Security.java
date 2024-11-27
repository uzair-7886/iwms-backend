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
import jakarta.persistence.Lob;;

@Entity
@Data
@Table(name = "security")
public class Security {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User user;

    @Lob
    private String authToken;

    private Timestamp expiration;
}
