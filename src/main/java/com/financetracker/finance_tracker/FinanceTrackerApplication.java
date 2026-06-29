package com.financetracker.finance_tracker;


// ─────────────────────────────────────────────
// FinanceTrackerApplication.java
//
// Entry point of the Spring Boot app.
// Run this class to start your backend server.
// It starts on http://localhost:8080
// ─────────────────────────────────────────────

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceTrackerApplication.class, args);
		System.out.println("✅ Finance Tracker Backend is running!");
		System.out.println("📍 API available at: http://localhost:8080/api");
	}
}