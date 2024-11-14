package com.example.backend.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ScheduledTransactionChecker {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTransactionChecker.class);
    private final TransactionService transactionService;

    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void checkSuspiciousTransactions() {
        try {
            BigDecimal highValueThreshold = new BigDecimal("100000");
            transactionService.markHighValueTransaction(highValueThreshold);
            transactionService.markFrequentTransactions();
        } catch (Exception e) {
            logger.info("Error occured while checking for suspicious transaction: {}", e.getMessage());
        }
    }
}
