package com.example.ofek_recipes.dtos;

import com.example.ofek_recipes.entities.RecipeTag;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        UUID id,
        String title,
        String description,
        String ingredientMd,
        String processMd,
        String photoUrl,
        UUID authorId,
        String authorName,
        Instant createdAt,
        Instant updatedAt,
        Integer prepTimeMinutes,
        List<RecipeTag> tags
)
{}
