package com.financetracker.finance_tracker.repository;


// ─────────────────────────────────────────────
// UserRepository.java
//
// Talks to the "users" table in PostgreSQL.
//
// You don't write SQL here!
// Spring Data JPA reads the method name and
// automatically generates the SQL for you.
//
// findByUsername("rahul")
// → SELECT * FROM users WHERE username = 'rahul'
//
// existsByUsername("rahul")
// → SELECT COUNT(*) FROM users WHERE username = 'rahul'
// ─────────────────────────────────────────────

import com.financetracker.finance_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username → used in login
    Optional<User> findByUsername(String username);

    // Check username already exists → used in register
    boolean existsByUsername(String username);

    // Check email already exists → used in register
    boolean existsByEmail(String email);
}