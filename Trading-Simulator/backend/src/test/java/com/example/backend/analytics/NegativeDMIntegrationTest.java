package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.CurrencyNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
public class NegativeDMIntegrationTest extends  BaseIntegrationTest {

    /*
    Formula for Negative Directional Movement (NegativeDM):
    if (Previous low price - current low price) > (current High price - high previous price)
    AND (previous low price - current low price > 0, then NegativeDM = previous low price - current low price
    otherwise negativeDM = 0
     */

    @ParameterizedTest(name = "{0}")
    @MethodSource("negativeDMIntegrationTestCases")
    void testCalculateNegativeDMFromDatabaseUsingCalculateIndicator(String testName, String symbol, String interval, List<HistoricalKline> klines, List<BigDecimal> expected) {
        Currency currency = createAndSaveCurrency(symbol, "toMarka");

        for (HistoricalKline kline : klines) {
            createAndSaveHistoricalKline(currency, interval, kline.getOpenTime(),
                    kline.getOpenPrice().doubleValue(),
                    kline.getHighPrice().doubleValue(),
                    kline.getLowPrice().doubleValue(),
                    kline.getClosePrice().doubleValue(),
                    kline.getCloseTime());
        }
        try {
            NegativeDMCalculator negativeDMCalculator = new NegativeDMCalculator();
            List<BigDecimal> negativeDMSeries = analyticsService.calculateIndicator(symbol, interval, negativeDMCalculator);
            Assertions.assertEquals(expected.size(), negativeDMSeries.size(), "Negative DM series mismatch " + testName);

            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, expected.get(i).compareTo(negativeDMSeries.get(i)),
                        "Negative DM at index: " + i + " did not match expected value for: " + testName);
            }
        }  catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found for: " + testName);
        }
    }

    static Stream<Arguments> negativeDMIntegrationTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic NegativeDM Integration test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("90")),
                                createKline(new BigDecimal("105"), new BigDecimal("95")),
                                createKline(new BigDecimal("108"), new BigDecimal("102"))
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (90 - 95) < 0 DM = 0
                                new BigDecimal("0.00000") // (95 - 102) < 0 DM = 0
                        )
                ),
                Arguments.of(
                        "NegativeDM Equals diff Integration test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("95")),
                                createKline(new BigDecimal("105"), new BigDecimal("90")),
                                createKline(new BigDecimal("110"), new BigDecimal("85"))
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (95 - 90) = 5 > (105 - 100) = 5 --> DM = 0;
                                new BigDecimal("0.00000")  // (90 - 85) = 5 > (110 - 105) = 5 --> DM = 0

                        )
                ),
                Arguments.of(
                        "NegativeDM Negative High difference integration test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("121"), new BigDecimal("120")),
                                createKline(new BigDecimal("116"), new BigDecimal("110")),
                                createKline(new BigDecimal("108"), new BigDecimal("100"))
                        ),
                        List.of(
                                new BigDecimal("10.00000"),  // (120 - 110) = 10 > (116 - 121) = -5 --> 10 > -5 && 10 > 0 DM = 10
                                new BigDecimal("10.00000")  //  // (110 - 100) = 10 > (108 - 116) = -8 --> 10 > -8 && 10 > 0 DM = 10

                        )
                ),
                Arguments.of(
                        "NegativeDM Greater than Zero Integration Test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("110"), new BigDecimal("90")),
                                createKline(new BigDecimal("115"), new BigDecimal("85")),
                                createKline(new BigDecimal("112"), new BigDecimal("80"))
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (90 - 85) = 5 > (115 - 110) = 5 --> DM = 0
                                new BigDecimal("5.00000")  // (85 - 80) = 5 > (112 - 112) = 0 --> DM = 5
                        )
                ),
                Arguments.of(
                        "NegativeDM with varying High low Integration Tests",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("105"), new BigDecimal("95")),
                                createKline(new BigDecimal("108"), new BigDecimal("90")),
                                createKline(new BigDecimal("107"), new BigDecimal("85")),
                                createKline(new BigDecimal("116"), new BigDecimal("80"))

                        ),
                        List.of(
                                new BigDecimal("5.00000"), // (95 - 90) = 5 > (108 - 105) = 3 --> DM = 5
                                new BigDecimal("5.00000"), // (90 - 85) = 5 > (107 - 108) = -1 --> DM = 5
                                new BigDecimal("0.00000")  // (85 - 80) = 5 > (116 - 107) = 9 ->> DM = 0
                        )
                )
        );
    }

    @Test
    void testCalculateNegativeDMWithMultipleIntervals() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createKline(new BigDecimal("110"), new BigDecimal("90"));
        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 110, 90, 100, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 105, 115, 95, 80, 2000L);
        // (90 - 95) = 5 > 0 = 0

        createAndSaveHistoricalKline(currency, "5m", 3L, 50, 65, 53, 70, 1000L);
        createAndSaveHistoricalKline(currency, "5m", 4L, 55, 70, 63, 65, 5L);
        createAndSaveHistoricalKline(currency, "5m", 5L, 60, 70, 55, 70, 6000L);

        NegativeDMCalculator negativeDMCalculator = new NegativeDMCalculator();

        try {
            List<BigDecimal> negativeDM1hSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", negativeDMCalculator);
            Assertions.assertEquals(1, negativeDM1hSeries.size(), "Negative DM series size for 1h should be 1");
            Assertions.assertEquals(0, negativeDM1hSeries.get(0).compareTo(new BigDecimal("0.00000")));

            List<BigDecimal> negativeDM5mSeries = analyticsService.calculateIndicator("ROYAL_COIN", "5m", negativeDMCalculator);
            List<BigDecimal> expected = List.of(
                    new BigDecimal("0.00000"), // (53 - 63) = -10 --> DM = 0
                    new BigDecimal("8.00000") // (63 - 55) = 8 > (70 - 70) = 0 --> 8 > 0
            );

            Assertions.assertEquals(expected.size(), negativeDM5mSeries.size());

            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, expected.get(i).compareTo(negativeDM5mSeries.get(i)),
                        "Negative DM at index: " + i + " did not match expected value");
            }
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found");
        }
    }



    private static HistoricalKline createKline(BigDecimal highPrice, BigDecimal lowPrice) {
        return HistoricalKline.builder()
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .build();
    }
}
