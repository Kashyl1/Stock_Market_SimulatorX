package com.example.backend.alert.trade;

import jakarta.validation.constraints.*;
import lombok.Data;

// Import Swagger annotations
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to create a new trade alert")
public class CreateTradeAlertRequest {

    @NotNull(message = "Portfolio ID is required")
    @Schema(description = "ID of the portfolio", example = "1")
    private Integer portfolioId;

    @NotNull(message = "Currency ID is required")
    @Schema(description = "ID of the currency", example = "1")
    private Integer currencyId;

    @NotNull(message = "Trade alert type is required")
    @Schema(description = "Type of the trade alert", example = "BUY")
    private TradeAlertType tradeAlertType;

    @NotNull(message = "Condition type is required")
    @Schema(description = "Type of the condition", example = "PRICE")
    private AlertConditionType conditionType;

    @NotNull(message = "Condition value is required")
    @Positive(message = "Condition value must be positive")
    @Schema(description = "Value of the condition", example = "50000.0")
    private BigDecimal conditionValue;

    @NotNull(message = "Trade amount is required")
    @Positive(message = "Trade amount must be positive")
    @Schema(description = "Amount to trade", example = "0.1")
    private BigDecimal tradeAmount;
}
