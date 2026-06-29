package com.financetracker.finance_tracker.security;

// ─────────────────────────────────────────────
// JwtUtil.java
//
// Handles everything JWT related.
//
// WHAT IS JWT?
// A token that proves who you are.
// Like a stamp on your hand at a concert —
// show it at every door and you get in!
//
// 3 main jobs:
// 1. generateToken()   → create token after login
// 2. extractUsername() → read who the token belongs to
// 3. isTokenValid()    → check token not expired/tampered
//
// TOKEN STRUCTURE:
// eyJhbGci....  eyJ1c2VyI....  SflKxwRJSMeK...
//   HEADER    .   PAYLOAD    .   SIGNATURE
//
// Payload contains: username + expiry time
// Signature proves: nobody tampered with it
// ─────────────────────────────────────────────

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Reads from application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Reads from application.properties (in milliseconds)
    // 86400000 = 1 day
    @Value("${jwt.expiration}")
    private long expiration;

    // ── Convert secret string to a Key object ─
    // Key must be minimum 32 characters (256 bits)
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── 1. Generate JWT token ─────────────────
    // Called after successful login/register
    // Returns a token string like: eyJhbGci...
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)           // who this token belongs to
                .setIssuedAt(new Date())        // when it was created
                .setExpiration(               // when it expires
                        new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256) // sign it
                .compact();                     // build the string
    }

    // ── 2. Extract username from token ────────
    // Called in JwtFilter on every request
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ── 3. Check if token is valid ────────────
    // Returns false if expired or tampered
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            // Check token has not expired
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // Token is invalid or expired
            return false;
        }
    }

    // ── Helper: parse and get claims ──────────
    // Claims = the data inside the token
    // (username, issued date, expiry date)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}