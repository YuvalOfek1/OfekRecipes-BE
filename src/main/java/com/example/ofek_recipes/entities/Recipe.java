package com.example.ofek_recipes.entities;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recipes")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(name = "fk_user_id")
    UUID userId;
    @Column(nullable = false)
    String title;
    @Column(nullable = false, columnDefinition = "text")
    String ingredientMd;
    @Column(nullable = false, columnDefinition = "text")
    String processMd;
    String photoUrl;
    @Column
    Instant  createdAt = Instant.now();
    @Column
    Instant  updatedAt = Instant.now();
    @PreUpdate
    void touch() {this.updatedAt = Instant.now(); }
    Integer prepTimeMinutes;
    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    JsonNode tagsJson;
    @Column
    String description;

}
