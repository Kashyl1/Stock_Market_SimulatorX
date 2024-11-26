package com.example.backend.currency;

import com.example.backend.portfolio.PortfolioAsset;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Currencies")
@Schema(description = "Represents a cryptocurrency with market data")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the currency", example = "1")
    private Integer currencyid;

    @Column(nullable = false, length = 10, unique = true)
    @Schema(description = "Symbol of the currency", example = "BTC")
    private String symbol;

    @Column(nullable = false, length = 100, unique = true)
    @Schema(description = "Name of the currency", example = "Bitcoin")
    private String name;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @JsonIgnore
    @Schema(description = "List of portfolio assets associated with this currency")
    private List<PortfolioAsset> portfolioAssets;

    @Column(nullable = true, precision = 30, scale = 10)
    @Schema(description = "Price change over the last 24 hours", example = "500.00")
    private BigDecimal priceChange;

    @Column(nullable = true, precision = 30, scale = 10)
    @Schema(description = "Price change percentage over the last 24 hours", example = "5.0")
    private BigDecimal priceChangePercent;

    @Column(nullable = true, precision = 30, scale = 10)
    @Schema(description = "Highest price in the last 24 hours", example = "51000.00")
    private BigDecimal highPrice;

    @Column(nullable = true, precision = 30, scale = 10)
    @Schema(description = "Lowest price in the last 24 hours", example = "48000.00")
    private BigDecimal lowPrice;

    @Column(nullable = true, precision = 30, scale = 10)
    @Schema(description = "Trading volume over the last 24 hours", example = "1200.50")
    private BigDecimal volume;

    @Column(nullable = true, precision = 30, scale = 10)
    @Schema(description = "Current market price", example = "50000.00")
    private BigDecimal currentPrice;

    @Column(nullable = true, length = 255)
    @Schema(description = "URL to the currency's image", example = "https://example.com/images/btc.png")
    private String imageUrl;

    @Column(name = "market_cap")
    @Schema(description = "Current market cap", example = "100000.00")
    private BigDecimal marketCap;
}
