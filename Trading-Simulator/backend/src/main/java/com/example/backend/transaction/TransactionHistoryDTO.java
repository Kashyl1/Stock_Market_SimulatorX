package com.example.backend.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {
    private Integer transactionid;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal rate;
    private LocalDateTime timestamp;
    private String currencyName;
    private String portfolioName;
}
