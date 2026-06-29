package com.financetracker.finance_tracker.service;


// ─────────────────────────────────────────────
// TransactionService.java
//
// All the business logic for transactions.
// Controllers call this. This calls Repository.
//
// Flow:
// Controller → Service → Repository → Database
//
// SecurityContextHolder.getContext().getAuthentication()
// gives us the currently logged-in user's username
// from the JWT token (set by JwtFilter)
// ─────────────────────────────────────────────

import com.financetracker.finance_tracker.dto.SummaryDTO;
import com.financetracker.finance_tracker.model.Transaction;
import com.financetracker.finance_tracker.model.User;
import com.financetracker.finance_tracker.repository.TransactionRepository;
import com.financetracker.finance_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Budget limits from application.properties
    @Value("#{${budget.limit.Food:5000}}")
    private Double foodLimit;

    // Map of category → limit for easy lookup
    private Map<String, Double> getBudgetLimits() {
        return Map.of(
                "Food", 5000.0,
                "Transport", 3000.0,
                "Entertainment", 2000.0,
                "Shopping", 4000.0,
                "Health", 2000.0,
                "Other", 1000.0
        );
    }

    // Get currently logged-in username from JWT
    private String getCurrentUsername() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    // GET all transactions for current user
    public List<Transaction> getAll() {
        return transactionRepository
                .findByUsernameOrderByDateDesc(getCurrentUsername());
    }

    // POST add new transaction
    public Transaction save(Transaction transaction) {
        String username = getCurrentUsername();
        transaction.setUsername(username);
        Transaction saved = transactionRepository.save(transaction);

        // Check budget alert only for expenses
        if ("expense".equals(transaction.getType())) {
            checkBudgetAlert(username, transaction.getCategory());
        }

        return saved;
    }

    // PUT update existing transaction
    public Transaction update(Long id, Transaction updated) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Security check: make sure this transaction belongs to current user
        if (!existing.getUsername().equals(getCurrentUsername())) {
            throw new RuntimeException("Unauthorized");
        }

        existing.setTitle(updated.getTitle());
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setType(updated.getType());
        existing.setDate(updated.getDate());

        return transactionRepository.save(existing);
    }

    // DELETE transaction
    public void delete(Long id) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // Security check
        if (!existing.getUsername().equals(getCurrentUsername())) {
            throw new RuntimeException("Unauthorized");
        }

        transactionRepository.delete(existing);
    }

    // GET summary for dashboard cards
    public SummaryDTO getSummary() {
        String username = getCurrentUsername();
        Double income  = transactionRepository.sumIncomeByUsername(username);
        Double expense = transactionRepository.sumExpenseByUsername(username);
        Double balance = income - expense;
        return new SummaryDTO(income, expense, balance);
    }

    // Check if spending crossed budget → send email
    private void checkBudgetAlert(String username, String category) {
        Double limit = getBudgetLimits().get(category);
        if (limit == null) return; // no limit defined for this category

        Double spent = transactionRepository
                .sumByCategoryThisMonth(username, category);

        if (spent > limit) {
            // Get user's email to send alert
            userRepository.findByUsername(username).ifPresent(user -> {
                emailService.sendBudgetAlert(
                        user.getEmail(), category, spent, limit);
            });
        }
    }
}