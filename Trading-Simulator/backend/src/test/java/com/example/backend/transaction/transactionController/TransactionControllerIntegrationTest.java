package com.example.backend.transaction.transactionController;

import com.example.backend.auth.AuthenticationRequest;
import com.example.backend.auth.AuthenticationResponse;
import com.example.backend.auth.RegisterRequest;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.portfolio.CreatePortfolioRequest;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.transaction.BuyAssetRequest;
import com.example.backend.transaction.TransactionService;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CacheManager cacheManager;


    private ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;
    private Integer portfolioId;
    private Currency testCurrency;

    @BeforeEach
    public void setUp() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("testuser@example.com");
        registerRequest.setPassword("TestPassword123!");
        registerRequest.setFirstname("Test");
        registerRequest.setLastname("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("testuser@example.com")
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setEmail("testuser@example.com");
        authRequest.setPassword("TestPassword123!");

        String authResponse = mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthenticationResponse authenticationResponse = objectMapper.readValue(authResponse, AuthenticationResponse.class);
        jwtToken = authenticationResponse.getToken();

        CreatePortfolioRequest portfolioRequest = new CreatePortfolioRequest();
        portfolioRequest.setName("Test Portfolio");

        String portfolioResponse = mockMvc.perform(post("/api/portfolios/create")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> portfolioResponseMap = objectMapper.readValue(portfolioResponse, Map.class);
        portfolioId = (Integer) portfolioResponseMap.get("portfolioid");

        user.setBalance(new BigDecimal("1000.00"));
        userRepository.save(user);

        testCurrency = new Currency();
        testCurrency.setSymbol("BTC_TEST");
        testCurrency.setName("Bitcoin Test");
        testCurrency.setCurrentPrice(new BigDecimal("50000.00"));
        currencyRepository.save(testCurrency);

        assertNotNull(testCurrency, "testCurrency should not be null after initialization");
    }
    @AfterEach
    public void tearDown() {
        Objects.requireNonNull(cacheManager.getCache("currentUser")).clear();
    }

    @Test
    public void testBuyAsset_Success() throws Exception {
        BigDecimal amountInUSD = new BigDecimal("500.00");

        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(portfolioId);
        request.setCurrencyid(testCurrency.getSymbol());
        request.setAmountInUSD(amountInUSD);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Asset purchased successfully"));
    }

    @Test
    public void testBuyAsset_InsufficientFunds() throws Exception {
        BigDecimal amountInUSD = new BigDecimal("1500.00");

        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(portfolioId);
        request.setCurrencyid(testCurrency.getSymbol());
        request.setAmountInUSD(amountInUSD);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }

    @Test
    public void testBuyAsset_InvalidPortfolioId() throws Exception {
        BigDecimal amountInUSD = new BigDecimal("500.00");
        Integer invalidPortfolioId = 9999; // Zakładamy, że ten ID nie istnieje

        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(invalidPortfolioId);
        request.setCurrencyid(testCurrency.getSymbol());
        request.setAmountInUSD(amountInUSD);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Portfolio not found"));

    }

    @Test
    public void testBuyAsset_InvalidCurrencySymbol() throws Exception {
        BigDecimal amountInUSD = new BigDecimal("500.00");
        String invalidCurrencySymbol = "INVALID_SYMBOL";

        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(portfolioId);
        request.setCurrencyid(invalidCurrencySymbol);
        request.setAmountInUSD(amountInUSD);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Currency not found in database"));
    }
    @Test
    public void testBuyAsset_NegativeAmount() throws Exception {
        BigDecimal amountInUSD = new BigDecimal("-500.00");

        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(portfolioId);
        request.setCurrencyid(testCurrency.getSymbol());
        request.setAmountInUSD(amountInUSD);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
