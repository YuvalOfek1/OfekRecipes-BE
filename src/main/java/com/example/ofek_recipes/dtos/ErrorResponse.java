package com.example.ofek_recipes.dtos;

public record ErrorResponse(
    int status,
    String message,
    String path
) {}

