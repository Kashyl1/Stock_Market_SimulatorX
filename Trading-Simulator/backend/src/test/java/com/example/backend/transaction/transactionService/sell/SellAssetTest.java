package com.example.backend.transaction.transactionService.sell;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAsset;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.transaction.Transaction;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.transaction.TransactionService;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class SellAssetTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioAssetRepository portfolioAssetRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Test
    public void testSellAsset_SuccessfulSale() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(new BigDecimal("1000.00").setScale(8, RoundingMode.HALF_UP));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setCurrencyid(1);
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
        currency.setCurrentPrice(new BigDecimal("50000.00000000").setScale(8, RoundingMode.HALF_UP));
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        PortfolioAsset portfolioAsset = new PortfolioAsset();
        portfolioAsset.setPortfolio(portfolio);
        portfolioAsset.setCurrency(currency);
        portfolioAsset.setAmount(new BigDecimal("1.00000000").setScale(8, RoundingMode.HALF_UP));
        portfolioAsset.setAveragePurchasePrice(new BigDecimal("45000.00000000").setScale(8, RoundingMode.HALF_UP));
        when(portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)).thenReturn(Optional.of(portfolioAsset));

        BigDecimal amountOfCurrency = new BigDecimal("0.50000000").setScale(8, RoundingMode.HALF_UP);

        BigDecimal originalBalance = currentUser.getBalance();

        assertDoesNotThrow(() -> transactionService.sellAsset(1, 1, amountOfCurrency));

        BigDecimal amountInUSD = amountOfCurrency.multiply(currency.getCurrentPrice()).setScale(8, RoundingMode.HALF_UP);
        BigDecimal expectedBalance = originalBalance.add(amountInUSD).setScale(8, RoundingMode.HALF_UP);

        BigDecimal actualBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        assertEquals(0, expectedBalance.compareTo(actualBalance));

        BigDecimal expectedNewAmount = new BigDecimal("0.50000000").setScale(8, RoundingMode.HALF_UP);
        BigDecimal actualNewAmount = portfolioAsset.getAmount().setScale(8, RoundingMode.HALF_UP);

        assertEquals(0, expectedNewAmount.compareTo(actualNewAmount));

        verify(portfolioAssetRepository).save(portfolioAsset);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals("SELL", savedTransaction.getTransactionType());
        assertEquals(0, amountOfCurrency.compareTo(savedTransaction.getAmount()));
        assertEquals(0, currency.getCurrentPrice().compareTo(savedTransaction.getRate()));
        assertEquals(currentUser, savedTransaction.getUser());
        assertEquals(portfolio, savedTransaction.getPortfolio());
        assertEquals(currency, savedTransaction.getCurrency());
    }

    @Test
    public void testSellAsset_InsufficientCurrencyAmount() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setCurrencyid(1);
        currency.setCurrentPrice(new BigDecimal("50000.00000000"));
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        PortfolioAsset portfolioAsset = new PortfolioAsset();
        portfolioAsset.setAmount(new BigDecimal("0.10000000"));
        when(portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)).thenReturn(Optional.of(portfolioAsset));

        BigDecimal amountOfCurrency = new BigDecimal("0.50000000");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.sellAsset(1, 1, amountOfCurrency);
        });

        assertEquals("Insufficient amount of currency to sell", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testSellAsset_CurrencyNotFound() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        when(currencyRepository.findById(99)).thenReturn(Optional.empty());

        BigDecimal amountOfCurrency = new BigDecimal("0.50000000");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.sellAsset(1, 99, amountOfCurrency);
        });

        assertEquals("Currency not found in database", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testSellAsset_CurrencyRateIsNull() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setCurrencyid(1);
        currency.setCurrentPrice(null);
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        BigDecimal amountOfCurrency = new BigDecimal("0.50000000");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.sellAsset(1, 1, amountOfCurrency);
        });

        assertEquals("Current price not available for currency ID: 1", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testSellAsset_PortfolioNotFound() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.empty());

        BigDecimal amountOfCurrency = new BigDecimal("0.50000000");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.sellAsset(1, 1, amountOfCurrency);
        });

        assertEquals("Portfolio not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testSellAsset_UserDoesNotOwnCurrency() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setCurrencyid(1);
        currency.setCurrentPrice(new BigDecimal("50000.00000000"));
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        when(portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)).thenReturn(Optional.empty());

        BigDecimal amountOfCurrency = new BigDecimal("0.50000000");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.sellAsset(1, 1, amountOfCurrency);
        });

        assertEquals("You do not own this currency", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }


    @Test
    public void testSellAsset_NegativeAmountOfCurrency() {
        BigDecimal amountOfCurrency = new BigDecimal("-0.50000000");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.sellAsset(1, 1, amountOfCurrency);
        });

        assertEquals("Amount of currency must be positive", exception.getMessage());
    }

    @Test
    public void testSellAsset_NullCurrencyId() {
        BigDecimal amountOfCurrency = new BigDecimal("0.50000000");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.sellAsset(1, null, amountOfCurrency);
        });

        assertEquals("Currency ID cannot be null", exception.getMessage());
    }

    @Test
    public void testSellAsset_LargeAmountOfCurrency() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(new BigDecimal("1000.00").setScale(8, RoundingMode.HALF_UP));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser))
                .thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setCurrencyid(1);
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
        currency.setCurrentPrice(new BigDecimal("50000.00000000").setScale(8, RoundingMode.HALF_UP));
        when(currencyRepository.findById(1)).thenReturn(Optional.of(currency));

        PortfolioAsset portfolioAsset = new PortfolioAsset();
        portfolioAsset.setPortfolio(portfolio);
        portfolioAsset.setCurrency(currency);
        portfolioAsset.setAmount(new BigDecimal("1000000.00000000").setScale(8, RoundingMode.HALF_UP));
        portfolioAsset.setAveragePurchasePrice(new BigDecimal("45000.00000000").setScale(8, RoundingMode.HALF_UP));
        when(portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency))
                .thenReturn(Optional.of(portfolioAsset));

        BigDecimal amountOfCurrency = new BigDecimal("999999.99999999").setScale(8, RoundingMode.HALF_UP);

        when(userRepository.save(any(User.class))).thenReturn(currentUser);
        when(portfolioAssetRepository.save(any(PortfolioAsset.class))).thenReturn(portfolioAsset);

        BigDecimal originalBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        assertDoesNotThrow(() -> transactionService.sellAsset(1, 1, amountOfCurrency));

        BigDecimal amountInUSD = amountOfCurrency.multiply(currency.getCurrentPrice()).setScale(8, RoundingMode.HALF_UP);
        BigDecimal expectedBalance = originalBalance.add(amountInUSD).setScale(8, RoundingMode.HALF_UP);
        BigDecimal actualBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        assertEquals(0, expectedBalance.compareTo(actualBalance));
    }
}
