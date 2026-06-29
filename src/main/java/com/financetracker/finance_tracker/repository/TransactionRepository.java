package com.financetracker.finance_tracker.repository;


// ─────────────────────────────────────────────
// TransactionRepository.java
//
// Talks to the "transactions" table.
//
// Some queries are complex so we use @Query
// to write them manually in JPQL format.
//
// JPQL is like SQL but uses class names
// instead of table names:
//
// SQL:   SELECT * FROM transactions WHERE username = ?
// JPQL:  SELECT t FROM Transaction t WHERE t.username = ?
// ─────────────────────────────────────────────

import com.financetracker.finance_tracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all transactions for a user, newest first
    // Used in GET /api/transactions
    List<Transaction> findByUsernameOrderByDateDesc(String username);

    // Total income for a user
    // Used in GET /api/transactions/summary
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.username = :username AND t.type = 'income'")
    Double sumIncomeByUsername(@Param("username") String username);

    // Total expenses for a user
    // Used in GET /api/transactions/summary
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.username = :username AND t.type = 'expense'")
    Double sumExpenseByUsername(@Param("username") String username);

    // Total expenses for a specific category this month
    // Used to trigger budget alert email!
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.username = :username " +
            "AND t.category = :category " +
            "AND t.type = 'expense' " +
            "AND EXTRACT(MONTH FROM t.date) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM t.date) = EXTRACT(YEAR FROM CURRENT_DATE)")
    Double sumByCategoryThisMonth(@Param("username") String username,
                                  @Param("category") String category);
}