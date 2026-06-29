package com.financetracker.finance_tracker.config;

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

        // IMPORTANT: must be true if using cookies/JWT refresh tokens
        config.setAllowCredentials(true);

        // ✅ Allow your frontend origins (ADD YOUR REAL VERCEL URL HERE)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://your-vercel-app.vercel.app"  // 🔴 replace this
        ));

        // Allow all headers (Authorization, Content-Type, etc.)
        config.setAllowedHeaders(List.of("*"));

        // Allow all HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
        config.setAllowedMethods(List.of("*"));

        // Apply to all routes
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
