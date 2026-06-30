package com.financetracker.finance_tracker.controller;

import com.financetracker.finance_tracker.dto.AuthDTO;
import com.financetracker.finance_tracker.model.User;
import com.financetracker.finance_tracker.repository.UserRepository;
import com.financetracker.finance_tracker.security.JwtUtil;
import com.financetracker.finance_tracker.service.EmailService;
import com.financetracker.finance_tracker.service.OtpService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    // ─────────────────────────────────────────────
    // SEND OTP
    // ─────────────────────────────────────────────
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String username = body.get("username");

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Username already taken"));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email already registered"));
        }

        otpService.generateAndSendOtp(email);

        return ResponseEntity.ok(
                new MessageResponse("OTP sent successfully to " + email));
    }

    // ─────────────────────────────────────────────
    // REGISTER (Verify OTP + Create User)
    // ─────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody AuthDTO.RegisterRequest req,
            HttpServletResponse response) {

        // Verify OTP
        if (!otpService.verifyOtp(req.getEmail(), req.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid or expired OTP"));
        }

        // Check username
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Username already taken"));
        }

        // Check email
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email already registered"));
        }

        // Create user
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        // Welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        System.out.println("Welcome email sent to: " + user.getEmail());

        // Generate JWT
        String token = jwtUtil.generateToken(user.getUsername());

        // Set Cookie
        setAuthCookie(response, token);

        AuthDTO.UserResponse userResponse =
                new AuthDTO.UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail());

        return ResponseEntity.ok(
                new AuthDTO.AuthResponse(
                        "Registered successfully",
                        userResponse));
    }

    // ─────────────────────────────────────────────
    // LOGIN
    // ─────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthDTO.LoginRequest req,
            HttpServletResponse response) {

        User user = userRepository.findByUsername(req.getUsername())
                .orElse(null);

        if (user == null ||
                !passwordEncoder.matches(req.getPassword(), user.getPassword())) {

            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Invalid username or password"));
        }

        String token = jwtUtil.generateToken(user.getUsername());

        setAuthCookie(response, token);

        AuthDTO.UserResponse userResponse =
                new AuthDTO.UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail());

        return ResponseEntity.ok(
                new AuthDTO.AuthResponse(
                        "Login successful",
                        userResponse));
    }

    // ─────────────────────────────────────────────
    // LOGOUT
    // ─────────────────────────────────────────────
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        Cookie cookie = new Cookie("auth_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);

        return ResponseEntity.ok(
                new MessageResponse("Logged out successfully"));
    }

    // ─────────────────────────────────────────────
    // Helper Method
    // ─────────────────────────────────────────────
   private void setAuthCookie(HttpServletResponse response, String token) {
    Cookie cookie = new Cookie("auth_token", token);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(86400);
    cookie.setSecure(true);   // ← uncomment for HTTPS production
    response.addCookie(cookie);

    // Also set SameSite=None for cross origin cookies
    response.addHeader("Set-Cookie",
        "auth_token=" + token +
        "; Path=/; HttpOnly; Secure; SameSite=None; Max-Age=86400");
}

    // ─────────────────────────────────────────────
    // Response Wrappers
    // ─────────────────────────────────────────────
    record ErrorResponse(String message) {
    }

    record MessageResponse(String message) {
    }
}
