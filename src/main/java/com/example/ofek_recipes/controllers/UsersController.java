package com.example.ofek_recipes.controllers;

import com.example.ofek_recipes.dtos.AuthResponse;
import com.example.ofek_recipes.dtos.CreateUserRequest;
import com.example.ofek_recipes.dtos.UserResponse;
import com.example.ofek_recipes.entities.AppUser;
import com.example.ofek_recipes.repositories.UsersRepository;
import com.example.ofek_recipes.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public UserResponse createUser(@RequestBody  CreateUserRequest request) {
    
        usersRepository.findByEmail(request.getEmail().toLowerCase()).ifPresent(u -> { throw new RuntimeException("User already exists"); });
        AppUser user = AppUser.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        AppUser saved = usersRepository.save(user);
        return UserResponse.builder()
                .email(saved.getEmail())
                .name(saved.getName()).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody CreateUserRequest request) {
        AppUser user = usersRepository.findByEmail(request.getEmail().toLowerCase()).orElse(null);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getEmail());
        String normalizedName = user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1).toLowerCase();
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(normalizedName)
                .build();
    }
}
