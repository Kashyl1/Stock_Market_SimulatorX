package com.example.backend.transaction;

import com.example.backend.portfolio.Portfolio;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.currency.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionid;

    @ManyToOne
    @JoinColumn(name = "currencyid", nullable = false)
    private Currency currency;

    @Column(nullable = false, length = 10)
    private String transactionType;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal amount;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal rate;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "portfolioid", nullable = false)
    private Portfolio portfolio;
}
