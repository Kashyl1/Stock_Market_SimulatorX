package com.example.backend.alert.mail;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateEmailAlertRequest {

    @NotNull(message = "Currency ID is required")
    private Integer currencyid;

    @NotNull(message = "Alert type is required")
    private EmailAlertType emailAlertType;

    private BigDecimal percentageChange;

    private BigDecimal targetPrice;
}
