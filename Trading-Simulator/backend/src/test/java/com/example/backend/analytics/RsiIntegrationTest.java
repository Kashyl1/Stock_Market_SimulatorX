package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.exceptions.CurrencyNotFoundException;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SpringBootTest
@ActiveProfiles("test")
public class RsiIntegrationTest extends BaseIntegrationTest {

    @Test
    void testCalculateRsiFromDatabaseUsingCalculateIndicator() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createAndSaveHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);
        createAndSaveHistoricalKline(currency, "1h", 3L, 30, 35, 29, 30, 3000L);
        createAndSaveHistoricalKline(currency, "1h", 4L, 40, 45, 39, 40, 4000L);
        createAndSaveHistoricalKline(currency, "1h", 5L, 50, 55, 49, 50, 5000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            BigDecimal expected = BigDecimal.valueOf(100);
            Assertions.assertEquals(0, result.compareTo(expected));
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        }
    }

    @Test
    void testCalculateRsiWithNoData() { // periods > klines
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");


        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            Assertions.fail("Should have thrown an exception due to no data available");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for RSI calculation"));
        }
    }

    @Test
    void testCalculateRsiWithFewerCandlesThanPeriod() {
        // n klines < m periods no nie, tak sie nie bawimy
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");


        createAndSaveHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);

        int periods = 3;
        try {
            analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            Assertions.fail("Expected exception due to not enough data");
        } catch (NotEnoughDataForCalculationException e) {
            System.out.println(e.getMessage());
            Assertions.assertTrue(e.getMessage().contains("Not enough data for RSI calculation"));
        }
    }

    @Test
    void testCalculateRsiAllSamePrice() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        double closePrice = 100.0;
        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 105, 95, closePrice, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 100, 105, 95, closePrice, 2000L);
        createAndSaveHistoricalKline(currency, "1h", 3L, 100, 105, 95, closePrice, 3000L);
        createAndSaveHistoricalKline(currency, "1h", 4L, 100, 105, 95, closePrice, 4000L);
        createAndSaveHistoricalKline(currency, "1h", 5L, 100, 105, 95, closePrice, 5000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(100.0)),
                    "Same price = rsi 100");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateRsiDifferentIntervalAndMoreCandles() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createAndSaveHistoricalKline(currency, "5m", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "5m", 2L, 15, 20, 14, 15, 2000L);
        createAndSaveHistoricalKline(currency, "5m", 3L, 20, 25, 19, 20, 3000L);
        createAndSaveHistoricalKline(currency, "5m", 4L, 25, 30, 24, 25, 4000L);
        createAndSaveHistoricalKline(currency, "5m", 5L, 30, 35, 29, 30, 5000L);
        createAndSaveHistoricalKline(currency, "5m", 6L, 35, 40, 34, 35, 6000L);

        int periods = 4;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "5m", new RsiCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(100.0)),
                    "Rsi should be 100, there is no loss cuz 15-10 > 20-15 > 25-20 i tak dalejxd");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }
}
