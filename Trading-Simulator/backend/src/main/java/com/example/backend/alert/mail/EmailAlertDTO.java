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
@Schema(description = "Data Transfer Object for Email Alert")
public class EmailAlertDTO {
    @Schema(description = "ID of the email alert", example = "1")
    private Integer emailAlertid;

    @Schema(description = "ID of the user who created the alert", example = "1")
    private Integer userid;

    @Schema(description = "Email of the user who created the alert", example = "user@example.com")
    private String userEmail;

    @Schema(description = "ID of the currency associated with the alert", example = "1")
    private Integer currencyid;

    @Schema(description = "Symbol of the currency", example = "BTC")
    private String currencySymbol;

    @Schema(description = "Type of the email alert", example = "PERCENTAGE")
    private EmailAlertType emailAlertType;

    @Schema(description = "Initial price when the alert was created", example = "48000.0")
    private BigDecimal initialPrice;

    @Schema(description = "Target price for price alerts", example = "50000.0")
    private BigDecimal targetPrice;

    @Schema(description = "Percentage change for percentage alerts", example = "5.0")
    private BigDecimal percentageChange;

    @Schema(description = "Indicates whether the alert is active", example = "true")
    private boolean active;
}
