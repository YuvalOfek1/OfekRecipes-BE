package com.example.ofek_recipes.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "uploads")
@Data
@Component
public class UploadDirConfig {
    private String dir;

}
