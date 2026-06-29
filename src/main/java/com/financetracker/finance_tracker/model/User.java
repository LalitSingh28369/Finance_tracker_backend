package com.financetracker.finance_tracker.model;


// ─────────────────────────────────────────────
// User.java — The User table in your database
//
// @Entity    = This class maps to a DB table
// @Table     = Name of the table
// @Id        = Primary key
// @Column    = Maps to a column in the table
//
// Lombok annotations:
// @Data      = auto generates getters/setters
// @NoArgsConstructor = empty constructor
// @AllArgsConstructor = constructor with all fields
// ─────────────────────────────────────────────

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // stored encrypted with BCrypt

    private String role = "ROLE_USER"; // for authorization later
}