package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.exceptions.CurrencyNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class TrueRangeIntegrationTest extends BaseIntegrationTest {

    // FOrmula: MAX(High Price - Low Price, ABS(High Price - previous close Price), ABS(LowPrice - previous close price)
    @Test
    void testCalculateTrueRangeFromDatabaseUsingCalculateIndicator() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 110, 90, 100, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 105, 115, 95, 105, 2000L);
        createAndSaveHistoricalKline(currency, "1h", 3L, 108, 112, 102, 108, 3000L);

        try {
            List<BigDecimal> trueRangeSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new TrueRangeCalculator());
            Assertions.assertEquals(2, trueRangeSeries.size(), "True Range series size mismatch");

            BigDecimal firstTr = trueRangeSeries.get(0); // max(115-95, abs.115-100, abs(95-100)) == 20
            BigDecimal secondTr = trueRangeSeries.get(1); // max(112-102, abs.112-105, abs.102-108) = 10

            Assertions.assertEquals(0, firstTr.compareTo(new BigDecimal("20.00000")),
                    "First True range value did not match the expected dvalue");
            Assertions.assertEquals(0, secondTr.compareTo(new BigDecimal("10.00000")),
                    "Second True range value did not match the expected value");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        }
    }

    @Test
    void testCalculateTrueRangeWithNoData() {
        try {
            analyticsService.calculateIndicator("NON_EXISTING_COIN", "1h", new TrueRangeCalculator());
            Assertions.fail("Should have trown CurrencyNotFoundException due to non existing coin");
        } catch (CurrencyNotFoundException e) {
            // jo nie je git
        }
    }

    @Test
    void testCalculateTrueRangeWithSingleKline() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");
        createAndSaveHistoricalKline(currency, "1h", 1L, 20, 30, 20, 30, 1000L);

        try {
            List<BigDecimal> trueRangeSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new TrueRangeCalculator());
            Assertions.assertTrue(trueRangeSeries.isEmpty(), "You can't calculate true range with <= 1 kline");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        }
    }

    @Test
    void testCalculateTrueRangeWithMultipleIntervals() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        // 1h
        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 110, 90, 100, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 105, 115, 95, 105, 2000L);

        // 5m interval
        createAndSaveHistoricalKline(currency, "5m", 1L, 50, 60, 40, 50, 3000L);
        createAndSaveHistoricalKline(currency, "5m", 2L, 55, 65, 45, 55, 4000L);
        createAndSaveHistoricalKline(currency, "5m", 3L, 60, 70, 50, 60, 5000L);

        try {
            List<BigDecimal> trueRange5m = analyticsService.calculateIndicator("ROYAL_COIN", "5m", new TrueRangeCalculator());
            Assertions.assertEquals(2, trueRange5m.size(), "True range series size for 5m");
            Assertions.assertEquals(0, trueRange5m.get(0).compareTo(new BigDecimal("20.00000")),
                    "First True Range value for 5m did not match the expected value");
            Assertions.assertEquals(0, trueRange5m.get(1).compareTo(new BigDecimal("20.00000")),
                    "Second True Range value for 5m did not match the expected value");

            List<BigDecimal> trueRange1h = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new TrueRangeCalculator());
            Assertions.assertEquals(1, trueRange1h.size(), "True Range series size for 1h");
            Assertions.assertEquals(0, trueRange1h.get(0).compareTo(new BigDecimal("20.00000")));
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found");
        }
    }

    @Test
    void testCalculateTrueRangeWithVaryingHighLow() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 105, 95, 100, 1000L); // K1
        createAndSaveHistoricalKline(currency, "1h", 2L, 102, 108, 101, 102, 2000L); // K2
        createAndSaveHistoricalKline(currency, "1h", 3L, 101, 107, 99, 101, 3000L);  // K3
        createAndSaveHistoricalKline(currency, "1h", 4L, 103, 109, 102, 103, 4000L); // K4

        try {
            List<BigDecimal> trueRangeSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new TrueRangeCalculator());

            List<BigDecimal> expected = List.of(
                    new BigDecimal("8.00000"), // Max(108-101, abs.108-100, abs.102-100) = 8
                    new BigDecimal("8.00000"), // Max(107-99, abs.107-102, abs.99-102) = 8
                    new BigDecimal("8.00000") // Max(109-102, abs.109-101, abs.102-101) = 8
            );
            Assertions.assertEquals(expected.size(), trueRangeSeries.size(), "True range size");

            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, trueRangeSeries.get(i).compareTo(expected.get(i)),
                        "True Range at index: " + i + " did not match the expected value");
            }
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        }
    }
}
