package com.example.backend.portfolio;

import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.exceptions.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void createPortfolio_Success() throws Exception {
        CreatePortfolioRequest request = new CreatePortfolioRequest();
        request.setName("My Portfolio");

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioid(1);
        portfolio.setName("My Portfolio");
        portfolio.setCreatedAt(LocalDateTime.now());
        portfolio.setUpdatedAt(LocalDateTime.now());

        when(portfolioService.createPortfolio(anyString())).thenReturn(portfolio);

        mockMvc.perform(post("/api/portfolios/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"My Portfolio\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioid", is(1)))
                .andExpect(jsonPath("$.name", is("My Portfolio")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(portfolioService).createPortfolio("My Portfolio");
    }

    @Test
    public void createPortfolio_PortfolioAlreadyExists_ShouldReturnConflict() throws Exception {
        when(portfolioService.createPortfolio(anyString())).thenThrow(new PortfolioAlreadyExistsException("Portfolio with that name already exists"));

        mockMvc.perform(post("/api/portfolios/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Existing Portfolio\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Portfolio with that name already exists")))
                .andExpect(jsonPath("$.status", is(409)));

        verify(portfolioService).createPortfolio("Existing Portfolio");
    }

    @Test
    public void getUserPortfolios_Success() throws Exception {
        PortfolioDTO portfolio1 = PortfolioDTO.builder()
                .portfolioid(1)
                .name("Portfolio 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PortfolioDTO portfolio2 = PortfolioDTO.builder()
                .portfolioid(2)
                .name("Portfolio 2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(portfolioService.getUserPortfolios()).thenReturn(Arrays.asList(portfolio1, portfolio2));

        mockMvc.perform(get("/api/portfolios/my-portfolios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].portfolioid", is(1)))
                .andExpect(jsonPath("$[0].name", is("Portfolio 1")))
                .andExpect(jsonPath("$[1].portfolioid", is(2)))
                .andExpect(jsonPath("$[1].name", is("Portfolio 2")));

        verify(portfolioService).getUserPortfolios();
    }

    @Test
    public void getUserPortfolioByid_Success() throws Exception {
        int portfolioId = 1;

        PortfolioDTO portfolioDTO = PortfolioDTO.builder()
                .portfolioid(portfolioId)
                .name("My Portfolio")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(portfolioService.getUserPortfolioByid(portfolioId)).thenReturn(portfolioDTO);

        mockMvc.perform(get("/api/portfolios/{id}", portfolioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.portfolioid", is(portfolioId)))
                .andExpect(jsonPath("$.name", is("My Portfolio")));

        verify(portfolioService).getUserPortfolioByid(portfolioId);
    }

    @Test
    public void getUserPortfolioByid_NotFound_ShouldReturnNotFound() throws Exception {
        int portfolioId = 1;

        when(portfolioService.getUserPortfolioByid(portfolioId)).thenThrow(new PortfolioNotFoundException("Portfolio not found"));

        mockMvc.perform(get("/api/portfolios/{id}", portfolioId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Portfolio not found")))
                .andExpect(jsonPath("$.status", is(404)));

        verify(portfolioService).getUserPortfolioByid(portfolioId);
    }

    @Test
    public void getPortfolioAssetsWithGains_Success() throws Exception {
        int portfolioId = 1;

        PortfolioAssetDTO assetDTO = PortfolioAssetDTO.builder()
                .currencyName("Bitcoin")
                .amount(new BigDecimal("10"))
                .averagePurchasePrice(new BigDecimal("50000"))
                .currentPrice(new BigDecimal("60000"))
                .gainOrLoss(new BigDecimal("10000"))
                .currencyid(1)
                .build();

        when(portfolioService.getPortfolioAssetsWithGains(portfolioId)).thenReturn(Collections.singletonList(assetDTO));

        mockMvc.perform(get("/api/portfolios/{id}/gains", portfolioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currencyName", is("Bitcoin")))
                .andExpect(jsonPath("$[0].amount", is(10)))
                .andExpect(jsonPath("$[0].averagePurchasePrice", is(50000)))
                .andExpect(jsonPath("$[0].currentPrice", is(60000)))
                .andExpect(jsonPath("$[0].gainOrLoss", is(10000)))
                .andExpect(jsonPath("$[0].currencyid", is(1)));

        verify(portfolioService).getPortfolioAssetsWithGains(portfolioId);
    }

    @Test
    public void getPortfolioAssetsWithGains_PortfolioNotFound_ShouldReturnNotFound() throws Exception {
        int portfolioId = 1;

        when(portfolioService.getPortfolioAssetsWithGains(portfolioId)).thenThrow(new PortfolioNotFoundException("Portfolio not found"));

        mockMvc.perform(get("/api/portfolios/{id}/gains", portfolioId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Portfolio not found")))
                .andExpect(jsonPath("$.status", is(404)));

        verify(portfolioService).getPortfolioAssetsWithGains(portfolioId);
    }

    @Test
    public void getTotalPortfolioGainOrLoss_Success() throws Exception {
        int portfolioId = 1;
        BigDecimal gainOrLoss = new BigDecimal("5000");

        when(portfolioService.calculateTotalPortfolioGainOrLoss(portfolioId)).thenReturn(gainOrLoss);

        mockMvc.perform(get("/api/portfolios/{id}/total-gain-or-loss", portfolioId))
                .andExpect(status().isOk())
                .andExpect(content().string("5000"));

        verify(portfolioService).calculateTotalPortfolioGainOrLoss(portfolioId);
    }

    @Test
    public void getTotalPortfolioGainOrLoss_PortfolioNotFound_ShouldReturnNotFound() throws Exception {
        int portfolioId = 1;

        when(portfolioService.calculateTotalPortfolioGainOrLoss(portfolioId)).thenThrow(new PortfolioNotFoundException("Portfolio not found"));

        mockMvc.perform(get("/api/portfolios/{id}/total-gain-or-loss", portfolioId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Portfolio not found")))
                .andExpect(jsonPath("$.status", is(404)));

        verify(portfolioService).calculateTotalPortfolioGainOrLoss(portfolioId);
    }
}