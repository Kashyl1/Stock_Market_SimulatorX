package com.example.backend.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request object for buying an asset")
public class BuyAssetRequest {

    @NotNull(message = "Portfolio ID is required")
    @JsonProperty("portfolioid")
    @Schema(description = "ID of the portfolio", example = "1", required = true)
    private Integer portfolioid;

    @NotBlank(message = "Currency symbol is required")
    @JsonProperty("currencyid")
    @Schema(description = "Symbol of the currency", example = "BTC", required = true)
    private String currencyid; // SYMBOL NIE ID xd

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount in USD must be positive")
    @JsonProperty("amountInUSD")
    @Schema(description = "Amount in USD to spend", example = "1000.00")
    private BigDecimal amountInUSD;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount of currency must be positive")
    @JsonProperty("amountOfCurrency")
    @Schema(description = "Amount of currency to buy", example = "0.05")
    private BigDecimal amountOfCurrency;
}
