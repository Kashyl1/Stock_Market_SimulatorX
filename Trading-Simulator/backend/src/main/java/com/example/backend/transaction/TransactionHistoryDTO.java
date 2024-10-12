package com.example.backend.transaction;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {
    private Integer transactionid;
    private String transactionType;
    private Double amount;
    private Double rate;
    private LocalDateTime timestamp;
    private String currencyName;
    private String portfolioName;
}
