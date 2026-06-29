package com.financetracker.finance_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ✅ FRONTEND URLs (Vercel + Local)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "https://finance-tracker-frontend-jk71ihnan-lalit2507.vercel.app",
                "https://finance-tracker-frontend-1qgft3e83-lalit2507.vercel.app"
        ));

        // ✅ Allow all required methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // ✅ Allow all headers
        config.setAllowedHeaders(List.of("*"));

        // ❗ IMPORTANT for JWT cookie auth
        config.setAllowCredentials(true);

        // Expose headers (optional but safe)
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
