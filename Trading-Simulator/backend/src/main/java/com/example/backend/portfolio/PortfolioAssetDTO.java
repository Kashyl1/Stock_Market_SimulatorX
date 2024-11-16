package com.example.backend.portfolio;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for portfolio assets")
public class PortfolioAssetDTO {

    @Schema(description = "Name of the currency", example = "Bitcoin")
    private String currencyName;

    @Schema(description = "URL to the currency's image", example = "https://example.com/images/btc.png")
    private String imageUrl;

    @Schema(description = "Amount of the currency owned", example = "1.2345")
    private BigDecimal amount;

    @Schema(description = "Average purchase price of the currency", example = "48000.00")
    private BigDecimal averagePurchasePrice;

    @Schema(description = "Current market price of the currency", example = "50000.00")
    private BigDecimal currentPrice;

    @Schema(description = "Gain or loss on this asset", example = "2500.00")
    private BigDecimal gainOrLoss;

    @Schema(description = "Unique identifier of the currency", example = "1")
    private Integer currencyid;
}
