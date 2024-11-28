package com.example.backend.portfolio;

import com.example.backend.userEvent.UserEventTrackingService;
import com.example.backend.userEvent.UserEvent;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.PortfolioAlreadyExistsException;
import com.example.backend.exceptions.PortfolioNotFoundException;
import com.example.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.example.backend.currency.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private UserEventTrackingService userEventTrackingService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstname("Test");
    }

    @Test
    void createPortfolio_Success() {
        String portfolioName = "My Portfolio";

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);
        when(portfolioRepository.findByUserAndName(user, portfolioName)).thenReturn(Optional.empty());

        Portfolio savedPortfolio = new Portfolio();
        savedPortfolio.setPortfolioid(1);
        savedPortfolio.setName(portfolioName);
        savedPortfolio.setUser(user);
        savedPortfolio.setCreatedAt(LocalDateTime.now());
        savedPortfolio.setUpdatedAt(LocalDateTime.now());

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(savedPortfolio);

        // Mock eventTrackingService.logEvent
        doNothing().when(userEventTrackingService).logEvent(anyString(), any(UserEvent.EventType.class), anyMap());

        Portfolio result = portfolioService.createPortfolio(portfolioName);

        assertNotNull(result);
        assertEquals(portfolioName, result.getName());
        assertEquals(user, result.getUser());

        verify(portfolioRepository).save(any(Portfolio.class));
        verify(userEventTrackingService).logEvent(eq(user.getEmail()), eq(UserEvent.EventType.CREATE_PORTFOLIO), anyMap());
    }

    @Test
    void createPortfolio_PortfolioAlreadyExists_ShouldThrowException() {
        String portfolioName = "Existing Portfolio";

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        Portfolio existingPortfolio = new Portfolio();
        existingPortfolio.setName(portfolioName);

        when(portfolioRepository.findByUserAndName(user, portfolioName)).thenReturn(Optional.of(existingPortfolio));

        assertThrows(PortfolioAlreadyExistsException.class, () -> portfolioService.createPortfolio(portfolioName));

        verify(portfolioRepository, never()).save(any(Portfolio.class));
        // No need to mock eventTrackingService here since save is never called
    }

    @Test
    void getUserPortfolios_Success() {
        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        Portfolio portfolio1 = new Portfolio();
        portfolio1.setPortfolioid(1);
        portfolio1.setName("Portfolio 1");
        portfolio1.setUser(user);
        portfolio1.setCreatedAt(LocalDateTime.now());
        portfolio1.setUpdatedAt(LocalDateTime.now());

        Portfolio portfolio2 = new Portfolio();
        portfolio2.setPortfolioid(2);
        portfolio2.setName("Portfolio 2");
        portfolio2.setUser(user);
        portfolio2.setCreatedAt(LocalDateTime.now());
        portfolio2.setUpdatedAt(LocalDateTime.now());

        when(portfolioRepository.findByUser(user)).thenReturn(Arrays.asList(portfolio1, portfolio2));

        List<PortfolioDTO> portfolios = portfolioService.getUserPortfolios();

        assertNotNull(portfolios);
        assertEquals(2, portfolios.size());
        assertEquals("Portfolio 1", portfolios.get(0).getName());
        assertEquals("Portfolio 2", portfolios.get(1).getName());
    }

    @Test
    void getUserPortfolioByid_Success() {
        int portfolioId = 1;

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(portfolioId);
        portfolio.setName("My Portfolio");
        portfolio.setUser(user);
        portfolio.setCreatedAt(LocalDateTime.now());
        portfolio.setUpdatedAt(LocalDateTime.now());
        portfolio.setPortfolioAssets(new ArrayList<>());

        when(portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioId, user)).thenReturn(Optional.of(portfolio));

        PortfolioDTO result = portfolioService.getUserPortfolioByid(portfolioId);

        assertNotNull(result);
        assertEquals(portfolioId, result.getPortfolioid());
        assertEquals("My Portfolio", result.getName());
    }

    @Test
    void getUserPortfolioByid_NotFound_ShouldThrowException() {
        int portfolioId = 1;

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        when(portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioId, user)).thenReturn(Optional.empty());

        assertThrows(PortfolioNotFoundException.class, () -> portfolioService.getUserPortfolioByid(portfolioId));
    }

    @Test
    void getPortfolioAssetsWithGains_Success() {
        int portfolioId = 1;

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(portfolioId);
        portfolio.setName("My Portfolio");
        portfolio.setUser(user);

        PortfolioAsset asset = new PortfolioAsset();
        asset.setAmount(new BigDecimal("10"));
        asset.setAveragePurchasePrice(new BigDecimal("50"));
        asset.setCurrentPrice(new BigDecimal("60"));

        Currency currency = new Currency();
        currency.setName("Bitcoin");
        currency.setCurrencyid(1);
        currency.setCurrentPrice(new BigDecimal("60"));

        asset.setCurrency(currency);

        portfolio.setPortfolioAssets(new ArrayList<>(List.of(asset)));

        when(portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioId, user)).thenReturn(Optional.of(portfolio));

        List<PortfolioAssetDTO> assetsWithGains = portfolioService.getPortfolioAssetsWithGains(portfolioId);

        assertNotNull(assetsWithGains);
        assertEquals(1, assetsWithGains.size());

        PortfolioAssetDTO assetDTO = assetsWithGains.get(0);
        assertEquals("Bitcoin", assetDTO.getCurrencyName());
        assertEquals(new BigDecimal("10"), assetDTO.getAmount());
        assertEquals(new BigDecimal("50"), assetDTO.getAveragePurchasePrice());
        assertEquals(new BigDecimal("60"), assetDTO.getCurrentPrice());
        assertEquals(new BigDecimal("100"), assetDTO.getGainOrLoss());
    }

    @Test
    void getPortfolioAssetsWithGains_PortfolioNotFound_ShouldThrowException() {
        int portfolioId = 1;

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        when(portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioId, user)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> portfolioService.getPortfolioAssetsWithGains(portfolioId));
    }

    @Test
    void calculateTotalPortfolioGainOrLoss_Success() {
        int portfolioId = 1;

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(portfolioId);
        portfolio.setName("My Portfolio");
        portfolio.setUser(user);

        PortfolioAsset asset1 = new PortfolioAsset();
        asset1.setAmount(new BigDecimal("10"));
        asset1.setAveragePurchasePrice(new BigDecimal("50"));
        asset1.setCurrentPrice(new BigDecimal("60"));

        Currency currency1 = new Currency();
        currency1.setName("Bitcoin");
        currency1.setCurrencyid(1);
        currency1.setCurrentPrice(new BigDecimal("60"));

        asset1.setCurrency(currency1);

        PortfolioAsset asset2 = new PortfolioAsset();
        asset2.setAmount(new BigDecimal("5"));
        asset2.setAveragePurchasePrice(new BigDecimal("100"));
        asset2.setCurrentPrice(new BigDecimal("90"));

        Currency currency2 = new Currency();
        currency2.setName("Ethereum");
        currency2.setCurrencyid(2);
        currency2.setCurrentPrice(new BigDecimal("90"));

        asset2.setCurrency(currency2);

        portfolio.setPortfolioAssets(new ArrayList<>(List.of(asset1, asset2)));

        when(portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioId, user)).thenReturn(Optional.of(portfolio));

        BigDecimal totalGainOrLoss = portfolioService.calculateTotalPortfolioGainOrLoss(portfolioId);

        assertNotNull(totalGainOrLoss);
        assertEquals(new BigDecimal("50.00"), totalGainOrLoss);
    }

    @Test
    void calculateTotalPortfolioGainOrLoss_PortfolioNotFound_ShouldThrowException() {
        int portfolioId = 1;

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        when(portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioId, user)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> portfolioService.calculateTotalPortfolioGainOrLoss(portfolioId));
    }
}
