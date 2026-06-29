package com.financetracker.finance_tracker.config;

// ─────────────────────────────────────────────
// CorsConfig.java
//
// WHY NEEDED?
// React runs on localhost:5173
// Spring Boot runs on localhost:8080
// Different ports = browser BLOCKS requests!
//
// This tells Spring Boot:
// "Allow requests from React's origin"
//
// allowCredentials(true) = cookies will work!
// Without this cookies won't send/receive.
// ─────────────────────────────────────────────

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Must be true for cookies to work!
        config.setAllowCredentials(true);

        // Allow React dev server
        // Add your Vercel URL here when deployed
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",  // Vite React
                "http://localhost:3000"   // CRA React
        ));

        // Allow all headers and methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Apply to all routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}