package com.example.backend.portfolio;

import com.example.backend.currency.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "portfolio_assets")
public class PortfolioAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portfolioAssetid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolioid")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currencyid")
    private Currency currency;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal amount;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal currentPrice;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal averagePurchasePrice;

    private LocalDateTime updatedAt;
}
