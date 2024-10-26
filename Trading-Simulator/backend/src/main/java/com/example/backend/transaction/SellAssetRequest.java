package com.example.backend.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    @JsonProperty("amount")
    private BigDecimal amount;
}
