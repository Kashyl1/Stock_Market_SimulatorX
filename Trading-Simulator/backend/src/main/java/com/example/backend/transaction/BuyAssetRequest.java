package com.example.backend.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BuyAssetRequest {
    @NotNull(message = "Portfolio ID is required")
    @JsonProperty("portfolioid")
    private Integer portfolioid;

    @NotBlank(message = "Currency symbol is required")
    @JsonProperty("currencyid")
    private String currencyid; // SYMBOL NIE ID xd

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount in USD must be positive")
    @JsonProperty("amountInUSD")
    private BigDecimal amountInUSD;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount of currency must be positive")
    @JsonProperty("amountOfCurrency")
    private BigDecimal amountOfCurrency;
}