package com.financetracker.finance_tracker.dto;


// ─────────────────────────────────────────────
// SummaryDTO.java
//
// This is returned by GET /api/transactions/summary
//
// React uses these 3 values for the dashboard cards:
// 💳 Balance      = totalIncome - totalExpense
// 📈 Total Income = sum of all income transactions
// 📉 Total Expense = sum of all expense transactions
// ─────────────────────────────────────────────

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double balance;
}