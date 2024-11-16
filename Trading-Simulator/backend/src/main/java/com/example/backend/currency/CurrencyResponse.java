package com.example.backend.currency;

import lombok.Data;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Response object containing currency market data")
public class CurrencyResponse {
    @Schema(description = "Symbol of the currency", example = "BTC")
    private String symbol;

    @Schema(description = "Last traded price", example = "50000.00")
    private BigDecimal lastPrice;

    @Schema(description = "Price change over the last 24 hours", example = "500.00")
    private BigDecimal priceChange;

    @Schema(description = "Price change percentage over the last 24 hours", example = "5.0")
    private BigDecimal priceChangePercent;

    @Schema(description = "Highest price in the last 24 hours", example = "51000.00")
    private BigDecimal highPrice;

    @Schema(description = "Lowest price in the last 24 hours", example = "48000.00")
    private BigDecimal lowPrice;

    @Schema(description = "Trading volume over the last 24 hours", example = "1200.50")
    private BigDecimal volume;
}
