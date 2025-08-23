package com.example.ofek_recipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class OfekRecipesApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfekRecipesApplication.class, args);
	}

}
