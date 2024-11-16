package com.example.backend.user;

import lombok.Data;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request object for adding funds to a user's account")
public class AddFundsRequest {

    @Schema(description = "Amount to add to the account", example = "100.00", required = true)
    private BigDecimal amount;
}
