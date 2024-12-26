package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PositiveDMCalculatorTest {

     /*
     * Formula for Positive Directional Movement (PositiveDM):
     * if (current high price - previous high price) > (previous low price - current low price)
     * AND (current high - previous high > 0), then PositiveDM = current high price - previous high price
     * otherwise PositiveDM = 0
     */

    @ParameterizedTest(name = "{0}")
    @MethodSource("positiveDMTestCases")
    void testPositiveDMCalculator(String testName, List<HistoricalKline> klines, List<BigDecimal> expected) {
        PositiveDMCalculator positiveDMCalculator = new PositiveDMCalculator();
        List<BigDecimal> positiveDMs = positiveDMCalculator.calculate(klines);

        Assertions.assertEquals(expected.size(), positiveDMs.size(), "Positive DM size mismatch for " + testName);

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(0, expected.get(i).compareTo(positiveDMs.get(i)),
                    "Positive DM at index: " + i + " did not match expected value for " + testName);
        }
    }

    static Stream<Arguments> positiveDMTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic PositiveDM",
                        createKlines(
                                new BigDecimal("110"), new BigDecimal("90"),
                                new BigDecimal("115"), new BigDecimal("95"),
                                new BigDecimal("112"), new BigDecimal("102")
                        ),
                        List.of(
                                new BigDecimal("5.00000"), // (115 - 110) = 5 > (90 - 95) = -5 --> 5 > -5 && -5 > 0 DM = 5
                                new BigDecimal("0.00000")  // (112 - 115) = -3 --> DM = 0
                        )
                ),
                Arguments.of(
                        "PositiveDM Equals Diff",
                        createKlines(
                                new BigDecimal("110"), new BigDecimal("90"),
                                new BigDecimal("115"), new BigDecimal("85"),
                                new BigDecimal("117"), new BigDecimal("80")
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (115 - 110) = 5 > (90 - 85) = 5 --> 5 > 5 DM = 0;
                                new BigDecimal("0.00000")  // (117 - 115) = 2 > (85 - 80) = 5 --> 2 > 5 DM = 0
                        )
                ),
                Arguments.of(
                        "Insufficient Data",
                        createKlines(
                                new BigDecimal("80"), new BigDecimal("60") // <= 1 kline
                        ),
                        List.of()
                ),
                Arguments.of(
                        "Varying Values",
                        createKlines(
                                new BigDecimal("105"), new BigDecimal("95"),
                                new BigDecimal("108"), new BigDecimal("90"),
                                new BigDecimal("107"), new BigDecimal("85"),
                                new BigDecimal("101"), new BigDecimal("80")
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (108 - 105) = 3 > (95 - 90) = 5 --> 3 > 5 DM = 0
                                new BigDecimal("0.00000"), // K3: -1 > 5 → DM = 0
                                new BigDecimal("0.00000")  // K4: -6 > 5 → DM = 0
                        )
                ),
                Arguments.of(
                        "PositiveDM Greater than Zero",
                        createKlines(
                                new BigDecimal("100"), new BigDecimal("80"),
                                new BigDecimal("106"), new BigDecimal("75")
                        ),
                        List.of(
                                new BigDecimal("6.00000")  // (106 - 100) = 6 > (80 - 75) = 5 --> 6 > 5 && 6 > 0
                        )
                ),
                Arguments.of(
                        "PositiveDM Negative High Difference",
                        createKlines(
                                new BigDecimal("120"), new BigDecimal("100"),
                                new BigDecimal("115"), new BigDecimal("95")
                        ),
                        List.of(
                                new BigDecimal("0.00000")  // K2: -5 > 5 → DM = 0
                        )
                )
        );
    }

    private static List<HistoricalKline> createKlines(BigDecimal... highLowPairs) {
        List<HistoricalKline> klines = new ArrayList<>();
        for (int i = 0; i < highLowPairs.length; i += 2) {
            klines.add(createKline(highLowPairs[i], highLowPairs[i + 1]));
        }
        return klines;
    }

    private static HistoricalKline createKline(BigDecimal highPrice, BigDecimal lowPrice) {
        return HistoricalKline.builder()
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .build();
    }
}
