package com.example.backend.portfolio;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAssetDTO {
    private String currencyName;
    private BigDecimal amount;
    private BigDecimal averagePurchasePrice;
    private BigDecimal currentPrice;
    private BigDecimal gainOrLoss;
    private Integer currencyid;
}
