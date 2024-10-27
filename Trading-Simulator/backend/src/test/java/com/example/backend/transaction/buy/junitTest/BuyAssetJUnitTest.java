package com.example.backend.transaction.buy.junitTest;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BuyAssetJUnitTest {
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
    public void testBuyAsset_SuccessfulPurchase() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
        currency.setCurrentPrice(BigDecimal.valueOf(50000.00000000));
        when(currencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(currency));

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        when(userRepository.save(any(User.class))).thenReturn(currentUser);
        when(portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> transactionService.buyAsset(1, "BTC", amountInUSD));

        assertEquals(BigDecimal.valueOf(500), currentUser.getBalance());

        ArgumentCaptor<PortfolioAsset> portfolioAssetCaptor = ArgumentCaptor.forClass(PortfolioAsset.class);
        verify(portfolioAssetRepository).save(portfolioAssetCaptor.capture());
        PortfolioAsset savedPortfolioAsset = portfolioAssetCaptor.getValue();

        BigDecimal expectedAmount = amountInUSD.divide(currency.getCurrentPrice(), 8, BigDecimal.ROUND_HALF_UP);
        assertEquals(0, expectedAmount.compareTo(savedPortfolioAsset.getAmount()));
        assertEquals(0, currency.getCurrentPrice().compareTo(savedPortfolioAsset.getCurrentPrice()));
        assertEquals(0, currency.getCurrentPrice().compareTo(savedPortfolioAsset.getAveragePurchasePrice()));
        assertEquals(portfolio, savedPortfolioAsset.getPortfolio());
        assertEquals(currency, savedPortfolioAsset.getCurrency());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals("BUY", savedTransaction.getTransactionType());
        assertEquals(0, expectedAmount.compareTo(savedTransaction.getAmount()));
        assertEquals(0, currency.getCurrentPrice().compareTo(savedTransaction.getRate()));
        assertEquals(currentUser, savedTransaction.getUser());
        assertEquals(portfolio, savedTransaction.getPortfolio());
        assertEquals(currency, savedTransaction.getCurrency());
    }

    @Test
    public void testBuyAsset_InsufficientBalance() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(100));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setSymbol("BTC");
        currency.setCurrentPrice(BigDecimal.valueOf(50000));
        when(currencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(currency));

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "BTC", amountInUSD);
        });

        assertEquals("Insufficient balance", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(portfolioAssetRepository, never()).save(any(PortfolioAsset.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testBuyAsset_CurrencyNotFound() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        when(currencyRepository.findBySymbol("UNKNOWN")).thenReturn(Optional.empty());

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "UNKNOWN", amountInUSD);
        });

        assertEquals("Currency not found in database", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(portfolioAssetRepository, never()).save(any(PortfolioAsset.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testBuyAsset_CurrencyRateIsNull() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setSymbol("BTC");
        currency.setCurrentPrice(null);
        when(currencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(currency));

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "BTC", amountInUSD);
        });

        assertEquals("Current price not available for BTC", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(portfolioAssetRepository, never()).save(any(PortfolioAsset.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testBuyAsset_PortfolioNotFound() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.empty());

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "BTC", amountInUSD);
        });

        assertEquals("Portfolio not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(portfolioAssetRepository, never()).save(any(PortfolioAsset.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testBuyAsset_NegativeAmountInUSD() {
        BigDecimal amountInUSD = BigDecimal.valueOf(-500);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "BTC", amountInUSD);
        });

        assertNotNull(exception);
    }

    @Test
    public void testBuyAsset_NullCurrencySymbol() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        Exception exception = assertThrows(NullPointerException.class, () -> {
            transactionService.buyAsset(1, null, amountInUSD);
        });

        assertNotNull(exception);
    }

    @Test
    public void testBuyAsset_EmptyCurrencySymbol() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        when(currencyRepository.findBySymbol("")).thenReturn(Optional.empty());

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "", amountInUSD);
        });

        assertEquals("Currency not found in database", exception.getMessage());
    }

    @Test
    public void testBuyAsset_NullAmountInUSD() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.buyAsset(1, "BTC", null);
        });

        assertEquals("Amount in USD must be positive", exception.getMessage());
    }

    @Test
    public void testBuyAsset_AmountInUSDisZero() {
        BigDecimal amountInUSD = BigDecimal.ZERO;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.buyAsset(1, "BTC", amountInUSD);
        });

        assertEquals("Amount in USD must be positive", exception.getMessage());
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

        BigDecimal amountInUSD = amountOfCurrency.multiply(currency.getCurrentPrice())
                .setScale(8, RoundingMode.HALF_UP);
        BigDecimal expectedBalance = originalBalance.add(amountInUSD).setScale(8, RoundingMode.HALF_UP);
        BigDecimal actualBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        System.out.println("Expected balance: " + expectedBalance);
        System.out.println("Actual balance: " + actualBalance);

        assertEquals(0, expectedBalance.compareTo(actualBalance));
    }

    @Test
    public void testBuyAsset_LargeAmountInUSD() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(new BigDecimal("1000000000000.00").setScale(8, RoundingMode.HALF_UP));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser))
                .thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
        currency.setCurrentPrice(new BigDecimal("50000.00000000").setScale(8, RoundingMode.HALF_UP));
        when(currencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(currency));

        BigDecimal amountInUSD = new BigDecimal("999999999999.99").setScale(8, RoundingMode.HALF_UP);

        when(userRepository.save(any(User.class))).thenReturn(currentUser);
        when(portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency))
                .thenReturn(Optional.empty());

        BigDecimal originalBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        assertDoesNotThrow(() -> transactionService.buyAsset(1, "BTC", amountInUSD));

        BigDecimal expectedBalance = originalBalance.subtract(amountInUSD).setScale(8, RoundingMode.HALF_UP);
        BigDecimal actualBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        assertEquals(0, expectedBalance.compareTo(actualBalance));

        BigDecimal expectedAmount = amountInUSD.divide(currency.getCurrentPrice(), 8, RoundingMode.HALF_UP);

        ArgumentCaptor<PortfolioAsset> portfolioAssetCaptor = ArgumentCaptor.forClass(PortfolioAsset.class);
        verify(portfolioAssetRepository).save(portfolioAssetCaptor.capture());
        PortfolioAsset savedPortfolioAsset = portfolioAssetCaptor.getValue();

        assertEquals(0, expectedAmount.compareTo(savedPortfolioAsset.getAmount()));
    }

    @Test
    public void testBuyAsset_AmountInUSDIsOne() {
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
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
        currency.setCurrentPrice(new BigDecimal("50000.00000000").setScale(8, RoundingMode.HALF_UP));
        when(currencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(currency));

        BigDecimal amountInUSD = BigDecimal.ONE.setScale(8, RoundingMode.HALF_UP);

        when(userRepository.save(any(User.class))).thenReturn(currentUser);
        when(portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency))
                .thenReturn(Optional.empty());

        BigDecimal originalBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        assertDoesNotThrow(() -> transactionService.buyAsset(1, "BTC", amountInUSD));

        BigDecimal expectedBalance = originalBalance.subtract(amountInUSD).setScale(8, RoundingMode.HALF_UP);
        BigDecimal actualBalance = currentUser.getBalance().setScale(8, RoundingMode.HALF_UP);

        System.out.println("Expected balance: " + expectedBalance);
        System.out.println("Actual balance: " + actualBalance);

        assertEquals(0, expectedBalance.compareTo(actualBalance));
    }

    @Test
    public void testBuyAsset_CurrencyRepositoryThrowsException() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        when(currencyRepository.findBySymbol("BTC")).thenThrow(new RuntimeException("Database connection error"));

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "BTC", amountInUSD);
        });

        assertEquals("Database connection error", exception.getMessage());
    }

    @Test
    public void testBuyAsset_UserRepositoryThrowsException() {
        String email = "user@example.com";
        when(authenticationService.getCurrentUserEmail()).thenReturn(email);

        User currentUser = new User();
        currentUser.setBalance(BigDecimal.valueOf(1000));
        when(authenticationService.getCurrentUser(email)).thenReturn(currentUser);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setUser(currentUser);
        when(portfolioRepository.findByPortfolioidAndUser(1, currentUser)).thenReturn(Optional.of(portfolio));

        Currency currency = new Currency();
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
        currency.setCurrentPrice(BigDecimal.valueOf(50000.00000000));
        when(currencyRepository.findBySymbol("BTC")).thenReturn(Optional.of(currency));

        BigDecimal amountInUSD = BigDecimal.valueOf(500);

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database write error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "BTC", amountInUSD);
        });

        assertEquals("Database write error", exception.getMessage());
    }
}
