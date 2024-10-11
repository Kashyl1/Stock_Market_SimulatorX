package com.example.backend.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {
    private Integer transactionID;
    private String transactionType;
    private Double amount;
    private Double rate;
    private LocalDateTime timestamp;
    private String currencyName;
    private String portfolioName;
}
