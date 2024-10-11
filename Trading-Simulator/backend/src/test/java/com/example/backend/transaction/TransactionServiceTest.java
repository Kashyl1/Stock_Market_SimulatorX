package com.example.backend.transaction;

import com.example.backend.CoinGecko.CoinGeckoService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAsset;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {

    @Mock
    private CoinGeckoService coinGeckoService;

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

    @InjectMocks
    private TransactionService transactionService;

    private User user;
    private Portfolio portfolio;
    private Currency currency;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setBalance(1000.0);

        portfolio = new Portfolio();
        portfolio.setPortfolioID(1);
        portfolio.setName("Test Portfolio");
        portfolio.setUser(user);

        currency = new Currency();
        currency.setCoinGeckoID("bitcoin");
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
    }

    @Test
    public void testBuyAsset_Success() {
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(portfolioRepository.findByPortfolioIDAndUser(1, user)).thenReturn(Optional.of(portfolio));
        when(currencyRepository.findByCoinGeckoID("bitcoin")).thenReturn(Optional.of(currency));
        when(coinGeckoService.getExchangeRates("bitcoin")).thenReturn(Map.of("bitcoin", Map.of("usd", 50000.0)));

        transactionService.buyAsset(1, "bitcoin", 1000.0);

        verify(userRepository, times(1)).save(user);
        assertEquals(0.0, user.getBalance());
    }

    @Test
    public void testBuyAsset_InsufficientBalance() {
        user.setBalance(500.0);
        when(authenticationService.getCurrentUser()).thenReturn(user);
        when(portfolioRepository.findByPortfolioIDAndUser(1, user)).thenReturn(Optional.of(portfolio));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.buyAsset(1, "bitcoin", 1000.0);
        });

        assertEquals("Insufficient balance", exception.getMessage());
    }

}
