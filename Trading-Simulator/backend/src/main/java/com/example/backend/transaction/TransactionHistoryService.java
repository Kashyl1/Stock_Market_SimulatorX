package com.example.backend.transaction;

import com.example.backend.adminEvent.AdminEvent;
import com.example.backend.adminEvent.AdminEventTrackingService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.PortfolioNotFoundException;
import com.example.backend.exceptions.TransactionNotFoundException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final AuthenticationService authenticationService;
    private final TransactionRepository transactionRepository;
    private final AdminEventTrackingService adminEventTrackingService;
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    @Operation(summary = "Show Transaction history for user", description = "Processes the transaction history show by user")
    public Page<TransactionHistoryDTO> getTransactionHistory(Pageable pageable) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Page<Transaction> transactions = transactionRepository.findByUser(currentUser, pageable);
        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Show Transaction history for portfolio", description = "Processes the transaction history show by portfolio")
    public Page<TransactionHistoryDTO> getTransactionHistoryByPortfolio(Integer portfolioid, Pageable pageable) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Portfolio portfolio = getPortfolioByidAndUser(portfolioid, currentUser);
        Page<Transaction> transactions = transactionRepository.findByUserAndPortfolio(currentUser, portfolio, pageable);
        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Get all transactions (admin)", description = "Show all transactions that have been made (admin use)")
    public Page<TransactionHistoryDTO> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);

        String adminEmail = authenticationService.getCurrentUserEmail();

        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.GET_ALL_TRANSACTIONS);

        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Get all user transactions (admin)", description = "Show all user transactions (admin use)")
    public Page<TransactionHistoryDTO> getTransactionsByUser(Integer userid, Pageable pageable) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Page<Transaction> transactions = transactionRepository.findByUser(user, pageable);

        String adminEmail = authenticationService.getCurrentUserEmail();

        Map<String, Object> details = Map.of(
                "userId", user.getId(),
                "userEmail", user.getEmail(),
                "userName", user.getFirstname(),
                "userLastname", user.getLastname()
        );

        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.GET_TRANSACTIONS_BY_USER, details);

        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Get transaction by user ID (admin)", description = "Show user transactions by ID (admin use)")
    @Transactional(readOnly = true)
    public TransactionHistoryDTO getTransactionById(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        return transactionMapper.toDTO(transaction);
    }

    private Page<TransactionHistoryDTO> mapTransactionsToDTO(Page<Transaction> transactions) {
        return transactions.map(transactionMapper::toDTO);
    }

    @Operation(summary = "Get portfolio by user ID (admin)", description = "Show user portfolio")
    @Transactional
    public Portfolio getPortfolioByidAndUser(Integer portfolioid, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
    }

    @Operation(summary = "Get transaction by portfolio ID (admin)", description = "Show user transaction by portfolio ID (admin use)")
    @Transactional(readOnly = true)
    public Page<TransactionHistoryDTO> getTransactionsByPortfolio(Integer portfolioid, Pageable pageable) {
        Portfolio portfolio = portfolioRepository.findById(portfolioid)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
        Page<Transaction> transactions = transactionRepository.findByPortfolio(portfolio, pageable);
        return mapTransactionsToDTO(transactions);
    }
}
