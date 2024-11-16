package com.example.backend.alert.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for Email Alert")
public class EmailAlertResponse {
    @Schema(description = "ID of the alert", example = "1")
    private Integer alertId;

    @Schema(description = "ID of the currency", example = "1")
    private Integer currencyId;

    @Schema(description = "Name of the currency", example = "Bitcoin")
    private String currencyName;

    @Schema(description = "Type of the email alert", example = "PERCENTAGE")
    private EmailAlertType emailAlertType;

    @Schema(description = "Percentage change for percentage alerts", example = "5.0")
    private BigDecimal percentageChange;

    @Schema(description = "Target price for price alerts", example = "50000.0")
    private BigDecimal targetPrice;

    @Schema(description = "Indicates whether the alert is active", example = "true")
    private boolean active;

    @Schema(description = "Initial price when the alert was created", example = "48000.0")
    private BigDecimal initialPrice;
}
