package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.CurrencyNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
public class PositiveDMIntegrationTest extends BaseIntegrationTest {


     /*
     * Formula for Positive Directional Movement (PositiveDM):
     * if (current high price - previous high price) > (previous low price - current low price)
     * AND (current high - previous high > 0), then PositiveDM = current high price - previous high price
     * otherwise PositiveDM = 0
     */

    @ParameterizedTest(name = "{0}")
    @MethodSource("positiveDMIntegrationTestCases")
    void testCalculatePositiveDMFromDatabaseUsingCalculateIndicator(String testName, String symbol, String interval, List<HistoricalKline> klines, List<BigDecimal> expected) {
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
            PositiveDMCalculator positiveDMCalculator = new PositiveDMCalculator();
            List<BigDecimal> positiveDMSeries = analyticsService.calculateIndicator(symbol, interval, positiveDMCalculator);
            Assertions.assertEquals(expected.size(), positiveDMSeries.size(), "Negative DM series mismatch " + testName);

            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, expected.get(i).compareTo(positiveDMSeries.get(i)),
                        "Positive DM at index: " + i + " did not match expected value for: " + testName);
            }
        }  catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found for: " + testName);
        }
    }

    static Stream<Arguments> positiveDMIntegrationTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic PositiveDM Integration test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("90")),
                                createKline(new BigDecimal("105"), new BigDecimal("102")),
                                createKline(new BigDecimal("108"), new BigDecimal("95"))
                        ),
                        List.of(
                                new BigDecimal("5.00000"), // (105 - 100) = 5 > (90 - 102) = -12 --> 5 > 0 && 5 > -12 DM = 5
                                new BigDecimal("0.00000") // (108 - 105) = 3 > (102 - 95) = 7 --> 3 > 7 && 3 > 0 DM = 0
                        )
                ),
                Arguments.of(
                        "PositiveDM Equals diff Integration test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("95")),
                                createKline(new BigDecimal("105"), new BigDecimal("90")),
                                createKline(new BigDecimal("110"), new BigDecimal("85"))
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (105 - 100) = 5 > (95 - 90) = 5 --> 5>5 && 5 > 0 DM = 0
                                new BigDecimal("0.00000")  // (110 - 105) = 5 > (90 - 85) = 5 --> DM = 0

                        )
                ),
                Arguments.of(
                        "PositiveDM Negative High difference integration test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("121"), new BigDecimal("120")),
                                createKline(new BigDecimal("134"), new BigDecimal("115")),
                                createKline(new BigDecimal("151"), new BigDecimal("110"))
                        ),
                        List.of(
                                new BigDecimal("13.00000"), // (134 - 121) = 13 > (120 - 115) = 5 --> 13 > 5 && 13 > 0 DM = 13
                                new BigDecimal("17.00000")  // (151 - 134) = 17 > (115 - 110) = 5 --> DM = 17

                        )
                ),
                Arguments.of(
                        "PositiveDM Greater than Zero Integration Test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("110"), new BigDecimal("92")),
                                createKline(new BigDecimal("117"), new BigDecimal("85")),
                                createKline(new BigDecimal("125"), new BigDecimal("80"))
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (117 - 110) = 7 > (92 - 85) = 7 --> 7 > 7 && 7 > 0 DM = 0
                                new BigDecimal("8.00000")  // (125 - 117) = 8 > (85 - 80) = 5 --> DM = 8
                        )
                ),
                Arguments.of(
                        "PositiveDM with varying High low Integration Tests",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("105"), new BigDecimal("95")),
                                createKline(new BigDecimal("108"), new BigDecimal("94")),
                                createKline(new BigDecimal("114"), new BigDecimal("85")),
                                createKline(new BigDecimal("108"), new BigDecimal("80"))

                        ),
                        List.of(
                                new BigDecimal("3.00000"), // (108 - 105) = 3 > (95 - 94) = 1 --> 3 > 1 && 3 > 0 DM = 3
                                new BigDecimal("0.00000"), // (114 - 108) = 6 > (94 - 85) = 9 --> DM = 0
                                new BigDecimal("0.00000")  // (108 - 114) = -6 DM = 0
                        )
                )
        );
    }

    @Test
    void testCalculatePositiveDMWithMultipleIntervals() {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        createKline(new BigDecimal("110"), new BigDecimal("90"));
        createAndSaveHistoricalKline(currency, "1h", 1L, 100, 110, 90, 100, 1000L);
        createAndSaveHistoricalKline(currency, "1h", 2L, 105, 115, 95, 80, 2000L);
        // (115 - 110) = 5 > (90 - 95) = -5 --> 5 > -5 && 5 > 0

        createAndSaveHistoricalKline(currency, "5m", 3L, 50, 65, 53, 70, 1000L);
        createAndSaveHistoricalKline(currency, "5m", 4L, 55, 70, 63, 65, 5L);
        createAndSaveHistoricalKline(currency, "5m", 5L, 60, 70, 55, 70, 6000L);

        PositiveDMCalculator positiveDMCalculator = new PositiveDMCalculator();

        try {
            List<BigDecimal> positiveDM1hSeries = analyticsService.calculateIndicator("ROYAL_COIN", "1h", positiveDMCalculator);
            Assertions.assertEquals(1, positiveDM1hSeries.size(), "Positive DM series size for 1h should be 1");
            Assertions.assertEquals(0, positiveDM1hSeries.get(0).compareTo(new BigDecimal("5.00000")));

            List<BigDecimal> positiveDM5mSeries = analyticsService.calculateIndicator("ROYAL_COIN", "5m", positiveDMCalculator);
            List<BigDecimal> expected = List.of(
                    new BigDecimal("5.00000"), // (70 - 65) = 5 > (53 - 63) = -10 --> DM = 5
                    new BigDecimal("0.00000") // (70 - 70) = 0 > DM = 0
            );

            Assertions.assertEquals(expected.size(), positiveDM5mSeries.size());

            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, expected.get(i).compareTo(positiveDM5mSeries.get(i)),
                        "Positive DM at index: " + i + " did not match expected value");
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

