package com.example.backend.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellAssetRequest {

    @NotNull(message = "Portfolio ID is required")
    @JsonProperty("portfolioID")
    private Integer portfolioID;

    @NotBlank(message = "Currency ID is required")
    @JsonProperty("currencyID")
    private String currencyID;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be positive")
    @JsonProperty("amount")
    private Double amount;
}
