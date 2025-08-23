package com.example.ofek_recipes.entities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class RecipeTagListConverter implements AttributeConverter<List<RecipeTag>, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<RecipeTag> attribute) {
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not convert list of tags to JSON", e);
        }
    }

    @Override
    public List<RecipeTag> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) return Collections.emptyList();
            return objectMapper.readValue(dbData, new TypeReference<List<RecipeTag>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not convert JSON to list of tags", e);
        }
    }
}

