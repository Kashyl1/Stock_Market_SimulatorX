package com.example.backend.alert;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAlertRequest {

    @NotNull(message = "Currency ID is required")
    private Integer currencyid;

    @NotNull(message = "Alert type is required")
    private AlertType alertType;

    private BigDecimal percentageChange;

    private BigDecimal targetPrice;
}
