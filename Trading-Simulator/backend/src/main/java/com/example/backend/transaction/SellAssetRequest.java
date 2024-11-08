package com.example.backend.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SellAssetRequest {

    @NotNull(message = "Portfolio ID is required")
    @JsonProperty("portfolioid")
    private Integer portfolioid;

    @NotNull
    @JsonProperty("currencyid")
    private Integer currencyid;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount of currency must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount in USD must be positive")
    @JsonProperty("priceInUSD")
    private BigDecimal priceInUSD;
}
