package com.example.backend.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request object for selling an asset")
public class SellAssetRequest {

    @NotNull(message = "Portfolio ID is required")
    @JsonProperty("portfolioid")
    @Schema(description = "ID of the portfolio", example = "1", required = true)
    private Integer portfolioid;

    @NotNull
    @JsonProperty("currencyid")
    @Schema(description = "ID of the currency", example = "1", required = true)
    private Integer currencyid;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount of currency must be positive")
    @JsonProperty("amount")
    @Schema(description = "Amount of currency to sell", example = "0.05")
    private BigDecimal amount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Amount in USD must be positive")
    @JsonProperty("priceInUSD")
    @Schema(description = "Amount in USD to sell", example = "1000.00")
    private BigDecimal priceInUSD;
}
