package com.example.backend.transaction;

import com.example.backend.adminEvent.AdminEvent;
import com.example.backend.adminEvent.AdminEventTrackingService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.exceptions.TransactionAlreadyMarkedAsSuspicious;
import com.example.backend.exceptions.TransactionNotFoundException;
import com.example.backend.mailVerification.VerificationService;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionSecurityService {

    private final TransactionRepository transactionRepository;
    private final AuthenticationService authenticationService;
    private final AdminEventTrackingService adminEventTrackingService;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;
    private final VerificationService verificationService;

    @Operation(summary = "Mark transaction as suspicious (admin)", description = "Admin can mark user transaction as suspicious (admin use)")
    @Transactional
    public void markTransactionAsSuspicious(Integer transactionid, boolean suspicious) {
        Transaction transaction = transactionRepository.findById(transactionid)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        if (transaction.isSuspicious()) {
            throw new TransactionAlreadyMarkedAsSuspicious("Transaction already marked as suspicious!");
        }

        transaction.setSuspicious(suspicious);
        transactionRepository.save(transaction);

        if (suspicious) {
            getTransactionDataForEmail(transaction);
        }

        String adminEmail = authenticationService.getCurrentUserEmail();

        Map<String, Object> details = Map.of(
                "transactionId", transaction.getTransactionid(),
                "userId", transaction.getUser().getId(),
                "userEmail", transaction.getUser().getEmail()
        );
        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.MARK_TRANSACTION_SUSPICIOUS, details);
    }

    @Operation(summary = "Get all suspicious transactions (admin)", description = "Show suspicious transactions (admin use)")
    @Transactional(readOnly = true)
    public List<TransactionHistoryDTO> getSuspiciousTransactions(BigDecimal thresholdAmount) {
        List<Transaction> transactions = transactionRepository.findByAmountGreaterThan(thresholdAmount);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Mark transaction as high value", description = "Update transaction and mark it as high value")
    @Transactional
    public void markHighValueTransaction(BigDecimal thresholdAmount) {
        List<Transaction> transactions = transactionRepository.findByAmountGreaterThan(thresholdAmount);
        for (Transaction transaction : transactions) {
            User user = transaction.getUser();
            LocalDateTime accountCreatedTime = user.getCreatedAt();
            LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
            boolean isNewUser = accountCreatedTime.isAfter(twoWeeksAgo);

            if (!transaction.isSuspicious() && isNewUser) {
                transaction.setSuspicious(true);
                transactionRepository.save(transaction);
                getTransactionDataForEmail(transaction);
            }
        }
    }

    @Operation(summary = "Mark transactions as frequent", description = "Mark transactions as frequent if user did over 100 transaction in 60 minutes")
    @Transactional
    public void markFrequentTransactions() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Transaction> recentTransactions = transactionRepository.findByTimestampBetween(user, oneHourAgo);
            if (recentTransactions.size() > 100) {
                for (Transaction transaction : recentTransactions) {
                    if (!transaction.isSuspicious()) {
                        transaction.setSuspicious(true);
                        transactionRepository.save(transaction);
                        getTransactionDataForEmail(transaction);
                    }
                }
            }
            user.setBlocked(true);
            userRepository.save(user);
        }
    }

    @Operation(summary = "Get data from transaction", description = "Get data about user and transaction to send email")
    public void getTransactionDataForEmail(Transaction transaction) {
        User user = transaction.getUser();
        Currency currency = transaction.getCurrency();
        BigDecimal amount = transaction.getAmount();
        BigDecimal rate = transaction.getRate();
        String transactionType = transaction.getTransactionType();
        int transactionId = transaction.getTransactionid();
        verificationService.sendSuspiciousTransactionEmail(user, transactionId, currency, amount, rate, transactionType);
    }
}
