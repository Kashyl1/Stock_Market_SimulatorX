package com.example.backend.transaction.transactionService.history;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.transaction.Transaction;
import com.example.backend.transaction.TransactionHistoryDTO;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.transaction.TransactionService;
import com.example.backend.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionHistoryTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Test
    public void testGetTransactionHistory_Success() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setEmail(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Currency currency = new Currency();
        currency.setName("Bitcoin");

        Portfolio portfolio = new Portfolio();
        portfolio.setName("Main Portfolio");

        Transaction transaction = new Transaction();
        transaction.setTransactionid(1);
        transaction.setTransactionType("BUY");
        transaction.setAmount(new BigDecimal("0.5"));
        transaction.setRate(new BigDecimal("50000.00"));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setCurrency(currency);
        transaction.setPortfolio(portfolio);

        Page<Transaction> transactions = new PageImpl<>(List.of(transaction));

        when(transactionRepository.findByUser(eq(currentUser), any(Pageable.class))).thenReturn(transactions);

        Page<TransactionHistoryDTO> result = transactionService.getTransactionHistory(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        TransactionHistoryDTO dto = result.getContent().get(0);
        assertEquals(1, dto.getTransactionid());
        assertEquals("BUY", dto.getTransactionType());
        assertEquals(new BigDecimal("0.5"), dto.getAmount());
        assertEquals(new BigDecimal("50000.00"), dto.getRate());
        assertEquals("Bitcoin", dto.getCurrencyName());
        assertEquals("Main Portfolio", dto.getPortfolioName());
    }

    @Test
    public void testGetTransactionHistory_NoTransactions() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setEmail(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Page<Transaction> transactions = new PageImpl<>(Collections.emptyList());
        when(transactionRepository.findByUser(eq(currentUser), any(Pageable.class))).thenReturn(transactions);

        Page<TransactionHistoryDTO> result = transactionService.getTransactionHistory(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    public void testGetTransactionHistoryByPortfolio_Success() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setEmail(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setName("Main Portfolio");
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setName("Bitcoin");

        Transaction transaction = new Transaction();
        transaction.setTransactionid(1);
        transaction.setTransactionType("BUY");
        transaction.setAmount(new BigDecimal("0.5"));
        transaction.setRate(new BigDecimal("50000.00"));
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setCurrency(currency);
        transaction.setPortfolio(portfolio);

        Page<Transaction> transactions = new PageImpl<>(List.of(transaction));

        when(transactionRepository.findByUserAndPortfolio(eq(currentUser), eq(portfolio), any(Pageable.class))).thenReturn(transactions);

        Page<TransactionHistoryDTO> result = transactionService.getTransactionHistoryByPortfolio(1, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        TransactionHistoryDTO dto = result.getContent().get(0);
        assertEquals(1, dto.getTransactionid());
        assertEquals("BUY", dto.getTransactionType());
        assertEquals(new BigDecimal("0.5"), dto.getAmount());
        assertEquals(new BigDecimal("50000.00"), dto.getRate());
        assertEquals("Bitcoin", dto.getCurrencyName());
        assertEquals("Main Portfolio", dto.getPortfolioName());
    }

    @Test
    public void testGetTransactionHistoryByPortfolio_PortfolioNotFound() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setEmail(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.getTransactionHistoryByPortfolio(1, PageRequest.of(0, 10));
        });

        assertEquals("Portfolio not found", exception.getMessage());
    }

    @Test
    public void testGetTransactionHistoryByPortfolio_NoTransactions() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setEmail(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setName("Main Portfolio");
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Page<Transaction> transactions = new PageImpl<>(Collections.emptyList());

        when(transactionRepository.findByUserAndPortfolio(eq(currentUser), eq(portfolio), any(Pageable.class))).thenReturn(transactions);

        Page<TransactionHistoryDTO> result = transactionService.getTransactionHistoryByPortfolio(1, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    public void testGetPortfolioByidAndUser_Success() {
        User currentUser = new User();
        currentUser.setEmail("user@example.com");

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);

        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Portfolio result = transactionService.getPortfolioByidAndUser(1, currentUser);

        assertNotNull(result);
        assertEquals(portfolio, result);
    }

    @Test
    public void testGetPortfolioByidAndUser_NotFound() {
        User currentUser = new User();
        currentUser.setEmail("user@example.com");

        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.getPortfolioByidAndUser(1, currentUser);
        });

        assertEquals("Portfolio not found", exception.getMessage());
    }

    @Test
    public void testGetPortfolioByidAndUser_NullUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getPortfolioByidAndUser(1, null);
        });

        assertEquals("User cannot be null", exception.getMessage());
    }
}
