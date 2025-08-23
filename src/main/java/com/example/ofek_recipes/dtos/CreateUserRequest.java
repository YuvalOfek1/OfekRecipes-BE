package com.example.ofek_recipes.dtos;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String name;
    private String password;
}
