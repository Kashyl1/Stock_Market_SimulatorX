package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.exceptions.CurrencyNotFoundException;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class EmaIntegrationTest extends BaseIntegrationTest {

    @Test
    void testCalculateEmaFromDatabaseUsingCalculateIndicator() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createAndSaveHistoricalKline(currency, "1h", 1L, 10, 35, 9, 30, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 20, 45, 19, 40, 2000L);
        createAndSaveHistoricalKline(currency, "1h", 3L, 30, 55, 29, 50, 3000L);

        int periods = 3;
        try {
            List<BigDecimal> emaSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new EmaCalculator(periods));
            BigDecimal result = emaSeries.get(emaSeries.size() - 1);
            BigDecimal expected = BigDecimal.valueOf(40.00);
            Assertions.assertEquals(0, result.setScale(2, RoundingMode.HALF_UP).compareTo(expected));
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        }
    }

    @Test
    void testCalculateEmaWithNoData() { // periods > klines
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        int periods = 3;
        try {
            List<BigDecimal> emaSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new EmaCalculator(periods));
            BigDecimal result = emaSeries.get(emaSeries.size() - 1);
            Assertions.fail("Should have thrown an exception due to no data available");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for EMA calculation"));
        }
    }

    @Test
    void testCalculateEmaWithFewerCandlesThanPeriod() {
        // 2 klines < 3 periods no nie, tak sie nie bawimy
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createAndSaveHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);

        int periods = 3;
        try {
            analyticsService.calculateIndicator("ROYAL_COIN", "1h", new EmaCalculator(periods));
            Assertions.fail("Expected exception due to not enough data");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for EMA calculation"));
        }
    }

    @Test
    void testCalculateEmaWithOneCandle() {
        // jo je git
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");
        createAndSaveHistoricalKline(currency, "1h", 1L, 50, 55, 45, 50, 1000L);

        // EMA dla 1 świecy z okresem 1 = po prostu cena zamknięcia tej świecy = 50
        int periods = 1;
        try {
            List<BigDecimal> emaSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new EmaCalculator(periods));
            BigDecimal result = emaSeries.get(emaSeries.size() - 1);
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(50.0)),
                    "EMA should match the single candle close price");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateEmaAllSamePrice() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");


        double closePrice = 100.0;
        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 105, 95, closePrice, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 100, 105, 95, closePrice, 2000L);
        createAndSaveHistoricalKline(currency, "1h", 3L, 100, 105, 95, closePrice, 3000L);
        createAndSaveHistoricalKline(currency, "1h", 4L, 100, 105, 95, closePrice, 4000L);
        createAndSaveHistoricalKline(currency, "1h", 5L, 100, 105, 95, closePrice, 5000L);

        int periods = 3;
        try {
            List<BigDecimal> emaSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new EmaCalculator(periods));
            BigDecimal result = emaSeries.get(emaSeries.size() - 1);
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(100.0)),
                    "EMA should match the same price of all candles");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateEmaDifferentIntervalAndMoreCandles() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createAndSaveHistoricalKline(currency, "5m", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "5m", 2L, 15, 20, 14, 15, 2000L);
        createAndSaveHistoricalKline(currency, "5m", 3L, 20, 25, 19, 20, 3000L);
        createAndSaveHistoricalKline(currency, "5m", 4L, 25, 30, 24, 25, 4000L);
        createAndSaveHistoricalKline(currency, "5m", 5L, 30, 35, 29, 30, 5000L);
        createAndSaveHistoricalKline(currency, "5m", 6L, 35, 40, 34, 35, 6000L);

        int periods = 3;
        try {
            List<BigDecimal> emaSeries = analyticsService.calculateIndicator("ROYAL_COIN", "5m", new EmaCalculator(periods));
            BigDecimal result = emaSeries.get(emaSeries.size() - 1);
            Assertions.assertEquals(0, result.setScale(3, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(30.0)),
                    "EMA should be 30.0 for the last three candles");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }
}
