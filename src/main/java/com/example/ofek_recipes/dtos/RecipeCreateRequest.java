package com.example.ofek_recipes.dtos;

import com.example.ofek_recipes.entities.RecipeTag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record RecipeCreateRequest(
        @NotBlank String title,
        @NotBlank String ingredientMd,
        @NotBlank String processMd,
        Integer prepTimeMinutes,
        List<RecipeTag> tags
) {}
