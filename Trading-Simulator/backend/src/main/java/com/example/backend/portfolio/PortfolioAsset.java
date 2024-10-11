package com.example.backend.portfolio;

import com.example.backend.currency.Currency;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer portfolioAssetID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioID")

    @JsonIgnoreProperties("portfolioAssets")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currencyID")
    private Currency currency;

    private Double amount;
    private Double averagePurchasePrice;

    private LocalDateTime updatedAt;
}
