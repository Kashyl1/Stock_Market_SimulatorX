package com.example.backend.transaction.transactionController;

import com.example.backend.exceptions.*;
import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.transaction.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void buyAsset_Success() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doNothing().when(transactionService).buyAsset(anyInt(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Asset purchased successfully"));
    }

    @Test
    public void buyAsset_MissingPortfolioId_ShouldReturnBadRequest() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void buyAsset_NegativeAmountInUSD_ShouldReturnBadRequest() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("-1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void buyAsset_PortfolioNotFound_ShouldReturnNotFound() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(999);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new PortfolioNotFoundException("Portfolio not found"))
                .when(transactionService).buyAsset(anyInt(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Portfolio not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    public void buyAsset_InsufficientFunds_ShouldReturnBadRequest() throws Exception {
        BuyAssetRequest request = new BuyAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid("BTC");
        request.setAmountInUSD(new BigDecimal("1000000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new InsufficientFundsException("Insufficient balance"))
                .when(transactionService).buyAsset(anyInt(), anyString(), any(BigDecimal.class));

        mockMvc.perform(post("/api/transactions/buy-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    public void sellAsset_Success() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid(1);
        request.setAmount(new BigDecimal("1000"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doNothing().when(transactionService).sellAsset(anyInt(), anyInt(), any(BigDecimal.class));

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("Asset sold successfully"));
    }

    @Test
    public void sellAsset_NegativeAmount_ShouldReturnBadRequest() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid(1);
        request.setAmount(new BigDecimal("-0.05"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sellAsset_MissingCurrencyId_ShouldReturnBadRequest() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setAmount(new BigDecimal("0.5"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sellAsset_InsufficientAssetAmount_ShouldReturnBadRequest() throws Exception {
        SellAssetRequest request = new SellAssetRequest();
        request.setPortfolioid(1);
        request.setCurrencyid(1);
        request.setAmount(new BigDecimal("1000.00"));

        String jsonRequest = objectMapper.writeValueAsString(request);

        doThrow(new InsufficientAssetAmountException("Insufficient amount of currency to sell"))
                .when(transactionService).sellAsset(anyInt(), anyInt(), any(BigDecimal.class));

        mockMvc.perform(post("/api/transactions/sell-asset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient amount of currency to sell"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    public void getAvailableAssets_Success() throws Exception {
        Map<String, Object> asset1 = Map.of("name", "Bitcoin", "price", 50000);
        Map<String, Object> asset2 = Map.of("name", "Ethereum", "price", 3000);
        List<Map<String, Object>> assetsList = List.of(asset1, asset2);

        Pageable pageable = PageRequest.of(0, 50, Sort.by("name").ascending());
        Page<Map<String, Object>> assetsPage = new PageImpl<>(assetsList, pageable, assetsList.size());

        when(transactionService.getAvailableAssetsWithPrices(any(Pageable.class))).thenReturn(assetsPage);

        mockMvc.perform(get("/api/transactions/available-assets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Bitcoin"))
                .andExpect(jsonPath("$.content[0].price").value(50000))
                .andExpect(jsonPath("$.content[1].name").value("Ethereum"))
                .andExpect(jsonPath("$.content[1].price").value(3000));
    }

    @Test
    public void getAvailableAssets_PriceNotAvailable_ShouldReturnServiceUnavailable() throws Exception {
        when(transactionService.getAvailableAssetsWithPrices(any(Pageable.class)))
                .thenThrow(new PriceNotAvailableException("Prices are currently unavailable"));

        mockMvc.perform(get("/api/transactions/available-assets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Prices are currently unavailable"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    public void getTransactionHistory_Success() throws Exception {
        TransactionHistoryDTO transaction1 = new TransactionHistoryDTO();
        TransactionHistoryDTO transaction2 = new TransactionHistoryDTO();
        List<TransactionHistoryDTO> transactionList = List.of(transaction1, transaction2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
        Page<TransactionHistoryDTO> transactionsPage = new PageImpl<>(transactionList, pageable, transactionList.size());

        when(transactionService.getTransactionHistory(any(Pageable.class))).thenReturn(transactionsPage);

        mockMvc.perform(get("/api/transactions/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
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
    }

    @Test
    public void getTransactionHistoryByPortfolio_Success() throws Exception {
        Integer portfolioId = 1;
        TransactionHistoryDTO transaction1 = new TransactionHistoryDTO();
        TransactionHistoryDTO transaction2 = new TransactionHistoryDTO();
        List<TransactionHistoryDTO> transactionList = List.of(transaction1, transaction2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("timestamp").descending());
        Page<TransactionHistoryDTO> transactionsPage = new PageImpl<>(transactionList, pageable, transactionList.size());

        when(transactionService.getTransactionHistoryByPortfolio(eq(portfolioId), any(Pageable.class))).thenReturn(transactionsPage);

        mockMvc.perform(get("/api/transactions/history/portfolio/{portfolioid}", portfolioId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
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
    }
}
