package com.example.backend.portfoliotransaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.transaction.Transaction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PortfolioTransactions")
public class PortfolioTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portfolioTransactionID;

    @ManyToOne
    @JoinColumn(name = "portfolioID", nullable = false)
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "transactionID", nullable = false)
    private Transaction transaction;
}
