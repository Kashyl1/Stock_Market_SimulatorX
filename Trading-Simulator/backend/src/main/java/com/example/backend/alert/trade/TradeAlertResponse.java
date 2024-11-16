package com.example.backend.alert.trade;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Import Swagger annotations
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for Trade Alert")
public class TradeAlertResponse {
    @Schema(description = "ID of the trade alert", example = "1")
    private Integer tradeAlertId;

    @Schema(description = "ID of the portfolio", example = "1")
    private Integer portfolioId;

    @Schema(description = "Name of the portfolio", example = "My Portfolio")
    private String portfolioName;

    @Schema(description = "ID of the currency", example = "1")
    private Integer currencyId;

    @Schema(description = "Name of the currency", example = "Bitcoin")
    private String currencyName;

    @Schema(description = "Type of the trade alert", example = "BUY")
    private TradeAlertType tradeAlertType;

    @Schema(description = "Type of the condition", example = "PRICE")
    private AlertConditionType conditionType;

    @Schema(description = "Value of the condition", example = "50000.0")
    private BigDecimal conditionValue;

    @Schema(description = "Amount to trade", example = "0.1")
    private BigDecimal tradeAmount;

    @Schema(description = "Indicates whether the alert is active", example = "true")
    private boolean active;

    @Schema(description = "Initial price when the alert was created", example = "48000.0")
    private BigDecimal initialPrice;

    public static TradeAlertResponse fromTradeAlert(TradeAlert tradeAlert) {
        return TradeAlertResponse.builder()
                .tradeAlertId(tradeAlert.getTradeAlertid())
                .portfolioId(tradeAlert.getPortfolio().getPortfolioid())
                .portfolioName(tradeAlert.getPortfolio().getName())
                .currencyId(tradeAlert.getCurrency().getCurrencyid())
                .currencyName(tradeAlert.getCurrency().getName())
                .tradeAlertType(tradeAlert.getTradeAlertType())
                .conditionType(tradeAlert.getConditionType())
                .conditionValue(tradeAlert.getConditionValue())
                .tradeAmount(tradeAlert.getTradeAmount())
                .active(tradeAlert.isActive())
                .initialPrice(tradeAlert.getInitialPrice())
                .build();
    }
}
