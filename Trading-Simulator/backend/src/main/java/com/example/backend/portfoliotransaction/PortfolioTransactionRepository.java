package com.example.backend.portfoliotransaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioTransactionRepository extends JpaRepository<PortfolioTransaction, Integer> {
}
