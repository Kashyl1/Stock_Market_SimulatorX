package com.example.backend.transaction;

import com.example.backend.portfolio.Portfolio;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.currency.Currency;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Transactions")
@Schema(description = "Represents a transaction made by a user")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the transaction", example = "1")
    private Integer transactionid;

    @ManyToOne
    @JoinColumn(name = "currencyid", nullable = false)
    @Schema(description = "Currency involved in the transaction", required = true)
    private Currency currency;

    @Column(nullable = false, length = 10)
    @Schema(description = "Type of transaction (e.g., BUY, SELL)", example = "BUY", required = true)
    private String transactionType;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "Amount of currency transacted", example = "0.05", required = true)
    private BigDecimal amount;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "Rate at which the transaction occurred", example = "45000.00", required = true)
    private BigDecimal rate;

    @Column(nullable = false)
    @Schema(description = "Timestamp of the transaction", example = "2023-10-01T12:34:56", required = true)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @Schema(description = "User who made the transaction", required = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "portfolioid", nullable = false)
    @Schema(description = "Portfolio associated with the transaction", required = true)
    private Portfolio portfolio;

    @Column(nullable = false)
    @Schema(description = "Indicates if the transaction is marked as suspicious", example = "false")
    private boolean suspicious = false;
}
