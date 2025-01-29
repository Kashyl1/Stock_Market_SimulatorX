package com.example.backend.transaction.transactionService.assets;

import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.currency.CurrencyService;
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
    private CurrencyService currencyService;

    @Mock
    private CurrencyRepository currencyRepository;

    @Test
    public void testGetAvailableAssetsWithPrices_Success() {
        Page<Currency> currencies = getCurrencies();

        when(currencyRepository.findAll(any(Pageable.class))).thenReturn(currencies);

        Page<Map<String, Object>> result = currencyService.getAvailableAssetsWithPrices(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Map<String, Object> asset = result.getContent().get(0);
        assertEquals("BTC", asset.get("id"));
        assertEquals("Bitcoin", asset.get("name"));
        assertEquals(new BigDecimal("50000.00"), asset.get("price_in_usd"));
        assertEquals(new BigDecimal("1000.00"), asset.get("price_change_24h"));
        assertEquals(new BigDecimal("2.0"), asset.get("price_change_percent_24h"));
        assertEquals(0, new BigDecimal("1000000").compareTo((BigDecimal) asset.get("volume_24h")));
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
        currency.setVolume(new BigDecimal("20.0"));
        currency.setImageUrl("http://example.com/btc.png");
        currency.setCurrencyid(1);

        return new PageImpl<>(List.of(currency));
    }

    @Test
    public void testGetAvailableAssetsWithPrices_EmptyList() {
        Page<Currency> currencies = new PageImpl<>(Collections.emptyList());
        when(currencyRepository.findAll(any(Pageable.class))).thenReturn(currencies);

        Page<Map<String, Object>> result = currencyService.getAvailableAssetsWithPrices(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    public void testGetAvailableAssetsWithPrices_ExceptionHandling() {
        when(currencyRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            currencyService.getAvailableAssetsWithPrices(PageRequest.of(0, 10));
        });

        assertEquals("Failed to get available assets with prices.", exception.getMessage());
    }
}
