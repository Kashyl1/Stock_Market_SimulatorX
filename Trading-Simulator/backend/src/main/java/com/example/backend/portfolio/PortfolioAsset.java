package com.example.backend.portfolio;

import com.example.backend.currency.Currency;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "portfolio_assets")
@Schema(description = "Represents an asset within a user's portfolio")
public class PortfolioAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the portfolio asset", example = "1")
    private Integer portfolioAssetid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolioid")
    @Schema(description = "Portfolio that contains this asset")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currencyid")
    @Schema(description = "Currency associated with this asset")
    private Currency currency;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "Amount of the currency owned", example = "1.2345")
    private BigDecimal amount;

    @Column(nullable = true, precision = 30, scale = 10)
    @Schema(description = "Average purchase price of the currency", example = "48000.00")
    private BigDecimal averagePurchasePrice;

    @Schema(description = "Timestamp when the asset was last updated", example = "2023-10-05T15:30:00")
    private LocalDateTime updatedAt;
}
