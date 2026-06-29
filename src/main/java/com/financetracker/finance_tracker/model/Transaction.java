package com.financetracker.finance_tracker.model;


// ─────────────────────────────────────────────
// Transaction.java — The Transaction table
//
// Each transaction belongs to a user.
// type = "income" or "expense"
// ─────────────────────────────────────────────

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "transactions",
        indexes = @Index(columnList = "username")) // index for fast queries
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Double amount;

    private String category; // Food, Transport, etc.

    private String type; // "income" or "expense"

    private LocalDate date;

    // Which user owns this transaction
    // We store username (not user object) for simplicity
    private String username;
}