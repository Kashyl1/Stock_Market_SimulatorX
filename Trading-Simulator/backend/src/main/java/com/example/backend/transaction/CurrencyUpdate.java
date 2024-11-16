package com.example.backend.transaction;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Represents an update to a currency's price")
public class CurrencyUpdate {

    @Schema(description = "Symbol of the currency", example = "BTC")
    private String symbol;

    @Schema(description = "Updated price of the currency", example = "45000.00")
    private BigDecimal price;
}
