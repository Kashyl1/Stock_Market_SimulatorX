package com.example.backend.transaction.assets;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.transaction.TransactionService;
import com.example.backend.user.UserRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AvailableAssetsWithPricesTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private CurrencyRepository currencyRepository;

    @Test
    public void testGetAvailableAssetsWithPrices_Success() {
        Page<Currency> currencies = getCurrencies();

        when(currencyRepository.findAll(any(Pageable.class))).thenReturn(currencies);

        Page<Map<String, Object>> result = transactionService.getAvailableAssetsWithPrices(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Map<String, Object> asset = result.getContent().get(0);
        assertEquals("BTC", asset.get("id"));
        assertEquals("Bitcoin", asset.get("name"));
        assertEquals(new BigDecimal("50000.00"), asset.get("price_in_usd"));
        assertEquals(new BigDecimal("1000.00"), asset.get("price_change_24h"));
        assertEquals(new BigDecimal("2.0"), asset.get("price_change_percent_24h"));
        assertEquals(new BigDecimal("1000000"), asset.get("volume_24h"));
        assertEquals("http://example.com/btc.png", asset.get("image_url"));
        assertEquals(1, asset.get("currencyid"));
    }

    private static Page<Currency> getCurrencies() {
        Currency currency = new Currency();
        currency.setSymbol("BTC");
        currency.setName("Bitcoin");
        currency.setCurrentPrice(new BigDecimal("50000.00"));
        currency.setPriceChange(new BigDecimal("1000.00"));
        currency.setPriceChangePercent(new BigDecimal("2.0"));
        currency.setVolume(new BigDecimal("1000000"));
        currency.setImageUrl("http://example.com/btc.png");
        currency.setCurrencyid(1);

        Page<Currency> currencies = new PageImpl<>(List.of(currency));
        return currencies;
    }

    @Test
    public void testGetAvailableAssetsWithPrices_EmptyList() {
        Page<Currency> currencies = new PageImpl<>(Collections.emptyList());
        when(currencyRepository.findAll(any(Pageable.class))).thenReturn(currencies);

        Page<Map<String, Object>> result = transactionService.getAvailableAssetsWithPrices(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    public void testGetAvailableAssetsWithPrices_ExceptionHandling() {
        when(currencyRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.getAvailableAssetsWithPrices(PageRequest.of(0, 10));
        });

        assertEquals("Failed to get available assets with prices.", exception.getMessage());
    }
}
