package com.example.ofek_recipes.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fe")
public record FeConfig(String url) {}
