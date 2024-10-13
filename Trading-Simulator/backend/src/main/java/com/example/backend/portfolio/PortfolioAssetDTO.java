package com.example.backend.portfolio;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAssetDTO {
    private String currencyName;
    private Double amount;
    private Double averagePurchasePrice;
    private Double currentPrice;
    private Double gainOrLoss;
    private String coinGeckoid;
}
