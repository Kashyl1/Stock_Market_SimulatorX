package com.example.backend.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Scheduled task for checking suspicious transactions periodically.
 */
@Component
@RequiredArgsConstructor
public class ScheduledTransactionChecker {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTransactionChecker.class);
    private final TransactionSecurityService transactionSecurityService;

    /**
     * Checks for suspicious transactions every hour.
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void checkSuspiciousTransactions() {
        try {
            BigDecimal highValueThreshold = new BigDecimal("100000");
            transactionSecurityService.markHighValueTransaction(highValueThreshold);
            transactionSecurityService.markFrequentTransactions();
        } catch (Exception e) {
            logger.info("Error occurred while checking for suspicious transactions: {}", e.getMessage());
        }
    }
}
