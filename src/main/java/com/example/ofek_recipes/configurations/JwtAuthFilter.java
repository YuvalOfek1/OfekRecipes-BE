package com.example.ofek_recipes.configurations;

import com.example.ofek_recipes.services.JwtService;
import com.example.ofek_recipes.repositories.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("inside jwt filter");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception ignored) {}
        }
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userOpt = usersRepository.findByEmail(email);
            if (userOpt.isPresent() && jwtService.isTokenValid(token, email)) {
                var user = userOpt.get();
                var auth = new UsernamePasswordAuthenticationToken(
                        user.getEmail(), null, java.util.Collections.emptyList());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/users/register") || path.startsWith("/users/login") || path.startsWith("/uploads");
    }
}
