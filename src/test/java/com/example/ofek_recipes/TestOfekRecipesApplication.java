package com.example.ofek_recipes;

import org.springframework.boot.SpringApplication;

public class TestOfekRecipesApplication {

	public static void main(String[] args) {
		SpringApplication.from(OfekRecipesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
