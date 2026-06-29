package com.financetracker.finance_tracker.controller;


// ─────────────────────────────────────────────
// TransactionController.java
//
// All transaction endpoints:
// GET    /api/transactions          → get all
// POST   /api/transactions          → add new
// PUT    /api/transactions/{id}     → update
// DELETE /api/transactions/{id}     → delete
// GET    /api/transactions/summary  → dashboard cards
//
// All these are PROTECTED — need JWT cookie.
// Spring Security handles that automatically
// through our JwtFilter + SecurityConfig.
// ─────────────────────────────────────────────

import com.financetracker.finance_tracker.dto.SummaryDTO;
import com.financetracker.finance_tracker.model.Transaction;
import com.financetracker.finance_tracker.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // GET /api/transactions
    // Returns all transactions for logged-in user
    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    // POST /api/transactions
    // Body: { title, amount, category, type, date }
    @PostMapping
    public ResponseEntity<Transaction> add(
            @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.save(transaction));
    }

    // PUT /api/transactions/{id}
    // Body: { title, amount, category, type, date }
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(
            @PathVariable Long id,
            @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.update(id, transaction));
    }

    // DELETE /api/transactions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // GET /api/transactions/summary
    // Returns { totalIncome, totalExpense, balance }
    // Used for the 3 cards on dashboard
    @GetMapping("/summary")
    public ResponseEntity<SummaryDTO> summary() {
        return ResponseEntity.ok(transactionService.getSummary());
    }
}