package com.example.backend.transaction.transactionController;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.config.JwtService;
import com.example.backend.exceptions.*;
import com.example.backend.transaction.*;
import com.example.backend.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
@WithMockUser(username = "testuser@example.com")
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AuthenticationService authenticationService;


    @MockBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setFirstname("testuser");
        mockUser.setEmail("testuser@example.com");
        mockUser.setBalance(new BigDecimal("10000.00"));

        when(authenticationService.getCurrentUser(anyString())).thenReturn(mockUser);
    }

    @Test
    public void buyAsset_Success() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doNothing().when(transactionService).buyAsset(
                eq(1),
                eq("BTC"),
                eq(new BigDecimal("1000.00")),
                isNull(),
                eq(mockUser)
        );

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Asset purchased successfully"));

        verify(transactionService, times(1)).buyAsset(
                eq(1),
                eq("BTC"),
                eq(new BigDecimal("1000.00")),
                isNull(),
                eq(mockUser)
        );
    }

    @Test
    public void buyAsset_MissingPortfolioId_ShouldReturnBadRequest() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.portfolioid").value("Portfolio ID is required"));

        verify(transactionService, never()).buyAsset(anyInt(), anyString(), any(), any(), any());
    }

    @Test
    public void buyAsset_NegativeAmountInUSD_ShouldReturnBadRequest() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("-1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amountInUSD").value("Amount in USD must be positive"));

        verify(transactionService, never()).buyAsset(anyInt(), anyString(), any(), any(), any());
    }


    @Test
    public void buyAsset_PortfolioNotFound_ShouldReturnNotFound() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(999);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new PortfolioNotFoundException("Portfolio not found"))
                .when(transactionService).buyAsset(
                        eq(999),
                        eq("BTC"),
                        eq(new BigDecimal("1000.00")),
                        isNull(),
                        eq(mockUser)
                );

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Portfolio not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(transactionService, times(1)).buyAsset(
                eq(999),
                eq("BTC"),
                eq(new BigDecimal("1000.00")),
                isNull(),
                eq(mockUser)
        );
    }

    @Test
    public void buyAsset_InsufficientFunds_ShouldReturnBadRequest() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new InsufficientFundsException("Insufficient balance"))
                .when(transactionService).buyAsset(
                        eq(1),
                        eq("BTC"),
                        eq(new BigDecimal("1000000.00")),
                        isNull(),
                        eq(mockUser)
                );

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance"))
                .andExpect(jsonPath("$.status").value(400));

        verify(transactionService, times(1)).buyAsset(
                eq(1),
                eq("BTC"),
                eq(new BigDecimal("1000000.00")),
                isNull(),
                eq(mockUser)
        );
    }

    @Test
    public void sellAsset_Success() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid(1);
        request.setAmount(new BigDecimal("1000"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doNothing().when(transactionService).sellAsset(
                eq(1),
                eq(1),
                eq(new BigDecimal("1000")),
                isNull(),
                eq(mockUser)
        );

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Asset sold successfully"));

        verify(transactionService, times(1)).sellAsset(
                eq(1),
                eq(1),
                eq(new BigDecimal("1000")),
                isNull(),
                eq(mockUser)
        );
    }

    @Test
    public void sellAsset_NegativeAmount_ShouldReturnBadRequest() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid(1);
        request.setAmount(new BigDecimal("-0.05"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount of currency must be positive"));

        verify(transactionService, never()).sellAsset(anyInt(), anyInt(), any(), any(), any());
    }


    @Test
    public void sellAsset_MissingCurrencyId_ShouldReturnBadRequest() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setAmount(new BigDecimal("0.5"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.currencyid").value("must not be null"));

        verify(transactionService, never()).sellAsset(anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    public void sellAsset_InsufficientAssetAmount_ShouldReturnBadRequest() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid(1);
        request.setAmount(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new InsufficientAssetAmountException("Insufficient amount of currency to sell"))
                .when(transactionService).sellAsset(
                        eq(1),
                        eq(1),
                        eq(new BigDecimal("1000.00")),
                        isNull(),
                        eq(mockUser)
                );

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient amount of currency to sell"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").doesNotExist());

        verify(transactionService, times(1)).sellAsset(
                eq(1),
                eq(1),
                eq(new BigDecimal("1000.00")),
                isNull(),
                eq(mockUser)
        );
    }

    @Test
    public void getAvailableAssets_Success() throws Exception {
        Map<String, Object> asset1 = Map.of(
                "id", "BTC",
                "name", "Bitcoin",
                "price_in_usd", 50000,
                "price_change_24h", 2000,
                "price_change_percent_24h", 4.0,
                "volume_24h", 1000000,
                "image_url", "http://example.com/btc.png",
                "currencyid", "BTC"
        );
        Map<String, Object> asset2 = Map.of(
                "id", "ETH",
                "name", "Ethereum",
                "price_in_usd", 3000,
                "price_change_24h", 150,
                "price_change_percent_24h", 5.0,
                "volume_24h", 500000,
                "image_url", "http://example.com/eth.png",
                "currencyid", "ETH"
        );
        List<Map<String, Object>> assetsList = List.of(asset1, asset2);

        Pageable pageable = PageRequest.of(0, 50, Sort.by("name").ascending());
        Page<Map<String, Object>> assetsPage = new PageImpl<>(assetsList, pageable, assetsList.size());

        when(transactionService.getAvailableAssetsWithPrices(any(Pageable.class))).thenReturn(assetsPage);

        mockMvc.perform(get("/api/transactions/available-assets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value("BTC"))
                .andExpect(jsonPath("$.content[0].name").value("Bitcoin"))
                .andExpect(jsonPath("$.content[0].price_in_usd").value(50000))
                .andExpect(jsonPath("$.content[0].price_change_24h").value(2000))
                .andExpect(jsonPath("$.content[0].price_change_percent_24h").value(4.0))
                .andExpect(jsonPath("$.content[0].volume_24h").value(1000000))
                .andExpect(jsonPath("$.content[0].image_url").value("http://example.com/btc.png"))
                .andExpect(jsonPath("$.content[0].currencyid").value("BTC"))
                .andExpect(jsonPath("$.content[1].id").value("ETH"))
                .andExpect(jsonPath("$.content[1].name").value("Ethereum"))
                .andExpect(jsonPath("$.content[1].price_in_usd").value(3000))
                .andExpect(jsonPath("$.content[1].price_change_24h").value(150))
                .andExpect(jsonPath("$.content[1].price_change_percent_24h").value(5.0))
                .andExpect(jsonPath("$.content[1].volume_24h").value(500000))
                .andExpect(jsonPath("$.content[1].image_url").value("http://example.com/eth.png"))
                .andExpect(jsonPath("$.content[1].currencyid").value("ETH"));

        verify(transactionService, times(1)).getAvailableAssetsWithPrices(any(Pageable.class));
    }

    @Test
    public void getAvailableAssets_PriceNotAvailable_ShouldReturnServiceUnavailable() throws Exception {
        when(transactionService.getAvailableAssetsWithPrices(any(Pageable.class)))
                .thenThrow(new PriceNotAvailableException("Prices are currently unavailable"));

        mockMvc.perform(get("/api/transactions/available-assets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Prices are currently unavailable"))
                .andExpect(jsonPath("$.status").value(503));

        verify(transactionService, times(1)).getAvailableAssetsWithPrices(any(Pageable.class));
    }

    @Test
    public void getTransactionHistory_Success() throws Exception {
        TransactionHistoryDTO transaction1 = TransactionHistoryDTO.builder()
                .transactionid(1)
                .transactionType("BUY")
                .amount(new BigDecimal("0.5"))
                .rate(new BigDecimal("50000"))
                .timestamp(LocalDateTime.now())
                .currencyName("Bitcoin")
                .portfolioName("My Portfolio")
                .build();

        TransactionHistoryDTO transaction2 = TransactionHistoryDTO.builder()
                .transactionid(2)
                .transactionType("SELL")
                .amount(new BigDecimal("0.2"))
                .rate(new BigDecimal("60000"))
                .timestamp(LocalDateTime.now())
                .currencyName("Bitcoin")
                .portfolioName("My Portfolio")
                .build();

        List<TransactionHistoryDTO> transactionList = List.of(transaction1, transaction2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
        Page<TransactionHistoryDTO> transactionsPage = new PageImpl<>(transactionList, pageable, transactionList.size());

        when(transactionService.getTransactionHistory(any(Pageable.class))).thenReturn(transactionsPage);

        mockMvc.perform(get("/api/transactions/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].transactionid").value(1))
                .andExpect(jsonPath("$.content[0].transactionType").value("BUY"))
                .andExpect(jsonPath("$.content[0].amount").value(0.5))
                .andExpect(jsonPath("$.content[0].rate").value(50000))
                .andExpect(jsonPath("$.content[0].currencyName").value("Bitcoin"))
                .andExpect(jsonPath("$.content[0].portfolioName").value("My Portfolio"))
                .andExpect(jsonPath("$.content[1].transactionid").value(2))
                .andExpect(jsonPath("$.content[1].transactionType").value("SELL"))
                .andExpect(jsonPath("$.content[1].amount").value(0.2))
                .andExpect(jsonPath("$.content[1].rate").value(60000))
                .andExpect(jsonPath("$.content[1].currencyName").value("Bitcoin"))
                .andExpect(jsonPath("$.content[1].portfolioName").value("My Portfolio"));

        verify(transactionService, times(1)).getTransactionHistory(any(Pageable.class));
    }

    @Test
    public void getTransactionHistory_ServiceException_ShouldReturnInternalServerError() throws Exception {
        when(transactionService.getTransactionHistory(any(Pageable.class)))
                .thenThrow(new RuntimeException("Service exception"));

        mockMvc.perform(get("/api/transactions/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Service exception"))
                .andExpect(jsonPath("$.status").value(500));

        verify(transactionService, times(1)).getTransactionHistory(any(Pageable.class));
    }

    @Test
    public void getTransactionHistoryByPortfolio_Success() throws Exception {
        Integer portfolioId = 1;

        TransactionHistoryDTO transaction1 = TransactionHistoryDTO.builder()
                .transactionid(1)
                .transactionType("BUY")
                .amount(new BigDecimal("0.5"))
                .rate(new BigDecimal("50000"))
                .timestamp(LocalDateTime.now())
                .currencyName("Bitcoin")
                .portfolioName("My Portfolio")
                .build();

        TransactionHistoryDTO transaction2 = TransactionHistoryDTO.builder()
                .transactionid(2)
                .transactionType("SELL")
                .amount(new BigDecimal("0.2"))
                .rate(new BigDecimal("60000"))
                .timestamp(LocalDateTime.now())
                .currencyName("Bitcoin")
                .portfolioName("My Portfolio")
                .build();

        List<TransactionHistoryDTO> transactionList = List.of(transaction1, transaction2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
        Page<TransactionHistoryDTO> transactionsPage = new PageImpl<>(transactionList, pageable, transactionList.size());

        when(transactionService.getTransactionHistoryByPortfolio(eq(portfolioId), any(Pageable.class))).thenReturn(transactionsPage);

        mockMvc.perform(get("/api/transactions/history/portfolio/{portfolioid}", portfolioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].transactionid").value(1))
                .andExpect(jsonPath("$.content[0].transactionType").value("BUY"))
                .andExpect(jsonPath("$.content[0].amount").value(0.5))
                .andExpect(jsonPath("$.content[0].rate").value(50000))
                .andExpect(jsonPath("$.content[0].currencyName").value("Bitcoin"))
                .andExpect(jsonPath("$.content[0].portfolioName").value("My Portfolio"))
                .andExpect(jsonPath("$.content[1].transactionid").value(2))
                .andExpect(jsonPath("$.content[1].transactionType").value("SELL"))
                .andExpect(jsonPath("$.content[1].amount").value(0.2))
                .andExpect(jsonPath("$.content[1].rate").value(60000))
                .andExpect(jsonPath("$.content[1].currencyName").value("Bitcoin"))
                .andExpect(jsonPath("$.content[1].portfolioName").value("My Portfolio"));

        verify(transactionService, times(1)).getTransactionHistoryByPortfolio(eq(portfolioId), any(Pageable.class));
    }

    @Test
    public void getTransactionHistoryByPortfolio_InvalidPortfolioId_ShouldReturnNotFound() throws Exception {
        Integer invalidPortfolioId = 999;

        when(transactionService.getTransactionHistoryByPortfolio(eq(invalidPortfolioId), any(Pageable.class)))
                .thenThrow(new PortfolioNotFoundException("Portfolio not found"));

        mockMvc.perform(get("/api/transactions/history/portfolio/{portfolioid}", invalidPortfolioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Portfolio not found"))
                .andExpect(jsonPath("$.status").value(404));

        verify(transactionService, times(1)).getTransactionHistoryByPortfolio(eq(invalidPortfolioId), any(Pageable.class));
    }

    @Test
    public void getTransactionHistoryByPortfolio_ServiceException_ShouldReturnInternalServerError() throws Exception {
        Integer portfolioId = 1;

        when(transactionService.getTransactionHistoryByPortfolio(eq(portfolioId), any(Pageable.class)))
                .thenThrow(new RuntimeException("Service exception"));

        mockMvc.perform(get("/api/transactions/history/portfolio/{portfolioid}", portfolioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Service exception"))
                .andExpect(jsonPath("$.status").value(500));

        verify(transactionService, times(1)).getTransactionHistoryByPortfolio(eq(portfolioId), any(Pageable.class));
    }
}
