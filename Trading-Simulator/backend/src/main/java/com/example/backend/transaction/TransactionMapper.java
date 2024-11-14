package com.example.backend.transaction;

import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionHistoryDTO toDTO(Transaction transaction) {
        return TransactionHistoryDTO.builder()
               .transactionid(transaction.getTransactionid())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .rate(transaction.getRate())
                .timestamp(transaction.getTimestamp())
                .currencyName(transaction.getCurrency().getName())
                .portfolioName(transaction.getPortfolio().getName())
                .suspicious(transaction.isSuspicious())
                .userEmail(transaction.getUser().getEmail())
                .portfolioid(transaction.getPortfolio().getPortfolioid())
                .totalPrice(transaction.getRate().multiply(transaction.getAmount()))
                .build();
    }
}
