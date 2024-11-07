package com.example.backend.alert.trade;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTradeAlertRequest {

    @NotNull(message = "Portfolio ID is required")
    private Integer portfolioId;

    @NotNull(message = "Currency ID is required")
    private Integer currencyId;

    @NotNull(message = "Trade alert type is required")
    private TradeAlertType tradeAlertType;

    @NotNull(message = "Condition type is required")
    private AlertConditionType conditionType;

    @NotNull(message = "Condition value is required")
    @Positive(message = "Condition value must be positive")
    private BigDecimal conditionValue;

    @NotNull(message = "Trade amount is required")
    @Positive(message = "Trade amount must be positive")
    private BigDecimal tradeAmount;
}
