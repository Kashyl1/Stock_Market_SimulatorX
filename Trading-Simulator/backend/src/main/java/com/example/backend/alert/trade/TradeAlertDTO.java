package com.example.backend.alert.trade;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Trade Alert")
public class TradeAlertDTO {
    @Schema(description = "ID of the trade alert", example = "1")
    private Integer tradeAlertid;

    @Schema(description = "ID of the user who created the alert", example = "1")
    private Integer userid;

    @Schema(description = "ID of the portfolio associated with the alert", example = "1")
    private Integer portfolioid;

    @Schema(description = "Email of the user who created the alert", example = "user@example.com")
    private String userEmail;

    @Schema(description = "ID of the currency associated with the alert", example = "1")
    private Integer currencyid;

    @Schema(description = "Symbol of the currency", example = "BTC")
    private String currencySymbol;

    @Schema(description = "Type of the trade alert", example = "BUY")
    private TradeAlertType tradeAlertType;

    @Schema(description = "Initial price when the alert was created", example = "48000.0")
    private BigDecimal initialPrice;

    @Schema(description = "Price at which the alert should trigger", example = "50000.0")
    private BigDecimal conditionPrice;

    @Schema(description = "Amount to trade", example = "0.1")
    private BigDecimal tradeAmount;

    @Schema(description = "Indicates whether the alert is active", example = "true")
    private boolean active;

    @Schema(description = "Type of the order", example = "LIMIT")
    private OrderType orderType;
}
