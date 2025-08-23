package com.example.ofek_recipes.repositories;

import com.example.ofek_recipes.entities.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipesRepository extends JpaRepository<Recipe, UUID> {
    @EntityGraph(attributePaths = "userId")
    Page<Recipe> findAll(Pageable page);
    List<Recipe> findByUserId(UUID userId);
}
