package com.example.iwms.iwms.entity;

import java.time.LocalDate;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    // Optional field—if provided, must be unique.
    @Column(unique = true, nullable = true)
    private String emailOrPhone;

    // Optional: for users registering via credentials.
    private String password;

    private LocalDate dob;
    private String gender;
    private String languagePreference;
    private String role;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    // Optional face embedding (stored as a JSON string) if the user registers with face.
    @Column(name = "face_embedding", columnDefinition = "TEXT", nullable = true)
    private String faceEmbedding;

    public static User orElseThrow(String username) {
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
