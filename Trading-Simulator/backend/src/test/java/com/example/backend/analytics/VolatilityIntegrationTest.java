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
public class VolatilityIntegrationTest extends BaseIntegrationTest{

    @Test
    void testCalculateVolatilityFromDatabaseUsingCalculateIndicator() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "ToMarka!");

        Assertions.assertTrue(currencyRepository.findById(currency.getCurrencyid()).isPresent(),
                "Currency should be saved in the database");

        createAndSaveHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);
        createAndSaveHistoricalKline(currency, "1h", 3L, 30, 35, 29, 30, 3000L);
        createAndSaveHistoricalKline(currency, "1h", 4L, 40, 45, 39, 40, 4000L);
        createAndSaveHistoricalKline(currency, "1h", 5L, 50, 55, 49, 50, 5000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new VolatilityCalculator(periods));
            BigDecimal expected = BigDecimal.valueOf(8.16);
            Assertions.assertEquals(1, result.setScale(2, RoundingMode.HALF_UP).compareTo(expected),
                    "Volatility calculation match the expected approximate value.");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateVolatilityWithNoData() { // periods > klines
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "ToMarka!");

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new VolatilityCalculator(periods));
            Assertions.fail("Should have thrown an exception due to no data available");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for Volatility calculation"));
        }
    }

    @Test
    void testCalculateVolatilityWithFewerCandlesThanPeriod() {
        // n klines < m periods no nie, tak sie nie bawimy
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "ToMarka!");

        createAndSaveHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);

        int periods = 3;
        try {
            analyticsService.calculateIndicator("ROYAL_COIN", "1h", new VolatilityCalculator(periods));
            Assertions.fail("Expected exception due to not enough data");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for Volatility calculation"));
        }
    }

    @Test
    void testCalculateVolatilityWithOneCandleShouldReturnZero() {
        // jo je git
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "ToMarka!");

        createAndSaveHistoricalKline(currency, "1h", 1L, 50, 55, 45, 50, 1000L);

        int periods = 1;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new VolatilityCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(0.0)),
                    "Volatility can't be calculated for 1 kline");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateVolatilityAllSamePrice() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "ToMarka!");

        double closePrice = 100.0;
        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 105, 95, closePrice, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 100, 105, 95, closePrice, 2000L);
        createAndSaveHistoricalKline(currency, "1h", 3L, 100, 105, 95, closePrice, 3000L);
        createAndSaveHistoricalKline(currency, "1h", 4L, 100, 105, 95, closePrice, 4000L);
        createAndSaveHistoricalKline(currency, "1h", 5L, 100, 105, 95, closePrice, 5000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new VolatilityCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(0.0)),
                    "Volatility is 0 if every kline is the same");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateVolatilityDifferentIntervalAndMoreCandles() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "ToMarka!");

        createAndSaveHistoricalKline(currency, "5m", 1L, 10, 15, 9, 10, 1000L);
        createAndSaveHistoricalKline(currency, "5m", 2L, 15, 20, 14, 15, 2000L);
        createAndSaveHistoricalKline(currency, "5m", 3L, 20, 25, 19, 20, 3000L);
        createAndSaveHistoricalKline(currency, "5m", 4L, 25, 30, 24, 25, 4000L);
        createAndSaveHistoricalKline(currency, "5m", 5L, 30, 35, 29, 30, 5000L);
        createAndSaveHistoricalKline(currency, "5m", 6L, 35, 40, 34, 35, 6000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "5m", new VolatilityCalculator(periods));
            Assertions.assertEquals(0, result.setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.valueOf(4.08)),
                    "Volatility should be 4.08 for the last three candles");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }
}
