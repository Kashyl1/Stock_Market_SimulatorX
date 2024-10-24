package com.example.backend.transaction;

import com.example.backend.portfolio.Portfolio;
import com.example.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndPortfolio(User user, Portfolio portfolio, Pageable pageable);
    void deleteAllByUser(User user);

}
