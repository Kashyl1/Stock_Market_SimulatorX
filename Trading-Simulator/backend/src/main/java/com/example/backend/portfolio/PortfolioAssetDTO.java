package com.example.backend.portfolio;

import lombok.*;
import org.checkerframework.checker.index.qual.SearchIndexBottom;

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
}
