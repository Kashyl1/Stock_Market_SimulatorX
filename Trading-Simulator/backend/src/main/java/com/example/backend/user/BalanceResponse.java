package com.example.backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing the user's account balance and a message")
public class BalanceResponse {

    @Schema(description = "Current account balance", example = "500.00")
    private BigDecimal balance;

    @Schema(description = "Informational message", example = "Funds have been added successfully.")
    private String message;
}
