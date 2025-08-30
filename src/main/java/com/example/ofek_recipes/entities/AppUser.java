package com.example.ofek_recipes.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(unique = true, nullable = false)
    String email;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String passwordHash;
    @Column
    Instant createdAt = Instant.now();

    @PrePersist
    @PreUpdate
    public void normalizeFields() {
        if (email != null) {
            email = email.toLowerCase();
        }
        if (name != null) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
    }
}