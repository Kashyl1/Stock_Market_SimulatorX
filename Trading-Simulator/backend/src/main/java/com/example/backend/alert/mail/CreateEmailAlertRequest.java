package com.example.backend.alert.mail;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Schema(description = "Request to create a new email alert")
public class CreateEmailAlertRequest {

    @NotNull(message = "Currency ID is required")
    @Schema(description = "ID of the currency for the alert", example = "1")
    private Integer currencyid;

    @NotNull(message = "Alert type is required")
    @Schema(description = "Type of the email alert", example = "PERCENTAGE/PRICE")
    private EmailAlertType emailAlertType;

    @Schema(description = "Percentage change for percentage alerts", example = "5.0")
    private BigDecimal percentageChange;

    @Schema(description = "Target price for price alerts", example = "50000.0")
    private BigDecimal targetPrice;
}
