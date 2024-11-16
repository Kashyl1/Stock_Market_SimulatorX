package com.example.backend.transaction;

import com.example.backend.portfolio.Portfolio;
import com.example.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Transaction Repository", description = "Repository interface for Transaction entities")
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndPortfolio(User user, Portfolio portfolio, Pageable pageable);

    void deleteAllByUser(User user);

    void deleteAllByPortfolio(Portfolio portfolio);

    Page<Transaction> findByPortfolio(Portfolio portfolio, Pageable pageable);

    List<Transaction> findByAmountGreaterThan(BigDecimal amount);

    List<Transaction> findByTimestampBetween(User user, LocalDateTime end);
}
