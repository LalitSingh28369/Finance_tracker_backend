package com.financetracker.finance_tracker.config;

import com.financetracker.finance_tracker.security.JwtFilter;
import com.financetracker.finance_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserRepository userRepository;

    // Load user from DB
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByUsername(username)
                .map(user ->
                        org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password(user.getPassword())
                                .roles("USER")
                                .build()
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
    }

    // SECURITY CONFIG
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ❌ disable CSRF for APIs
            .csrf(csrf -> csrf.disable())

            // 🔥 IMPORTANT: enable CORS (uses CorsConfig if present)
            .cors(cors -> {})

            // 🔐 API rules
            .authorizeHttpRequests(auth -> auth
                    // public auth APIs
                    .requestMatchers("/api/auth/**").permitAll()

                    // allow preflight requests (VERY IMPORTANT)
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // everything else needs JWT
                    .anyRequest().authenticated()
            )

            // 🚫 no sessions (JWT based auth)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔐 JWT filter
            .addFilterBefore(jwtFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // PASSWORD ENCODER
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AUTH MANAGER
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
