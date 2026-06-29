package com.financetracker.finance_tracker.dto;


// ─────────────────────────────────────────────
// AuthDTO.java
//
// Contains all request/response objects for auth.
// We use static inner classes to keep everything
// auth-related in ONE file — clean and organized.
//
// RegisterRequest  → what frontend sends to /register
// LoginRequest     → what frontend sends to /login
// UserResponse     → what we send BACK (no password!)
// AuthResponse     → full response: message + user
// ─────────────────────────────────────────────

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTO {

    // ── What React sends to /api/auth/register ──
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String otp;
    }

    // ── What React sends to /api/auth/login ─────
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // ── What we send BACK after login/register ──
    // NEVER include password here!
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
    }

    // ── Full response wrapper ───────────────────
    // React receives: { message: "Login successful", user: { id, username, email } }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthResponse {
        private String message;
        private UserResponse user;
    }
}