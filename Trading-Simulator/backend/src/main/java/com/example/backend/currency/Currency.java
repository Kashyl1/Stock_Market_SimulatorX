package com.example.backend.currency;

import com.example.backend.portfolio.PortfolioAsset;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer currencyid;

    @Column(nullable = false, length = 10, unique = true)
    private String symbol;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @JsonIgnore
    private List<PortfolioAsset> portfolioAssets;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal priceChange;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal priceChangePercent;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal highPrice;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal lowPrice;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal volume;

    @Column(nullable = true, precision = 30, scale = 10)
    private BigDecimal currentPrice;

    @Column(nullable = true, length = 255)
    private String imageUrl;
}
