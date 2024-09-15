package com.example.backend.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyCryptoResponse {
    private String message;
    private String transactionId;
    private Double remainingBalance;
}
