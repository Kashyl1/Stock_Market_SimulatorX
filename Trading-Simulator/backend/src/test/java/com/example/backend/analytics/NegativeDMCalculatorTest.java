package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NegativeDMCalculatorTest {
    /*
    Formula for Negative Directional Movement (NegativeDM):
    if (Previous low price - current low price) > (current High price - high previous price)
    AND (previous low price - current low price > 0, then NegativeDM = previous low price - current low price
    otherwise negativeDM = 0
     */

    @ParameterizedTest(name = "{0}")
    @MethodSource("negativeDMTestCases")
    void testNegativeDMCalculator(String testName, List<HistoricalKline> klines, List<BigDecimal> expected) {
        NegativeDMCalculator negativeDMCalculator = new NegativeDMCalculator();
        List<BigDecimal> negativeDMs = negativeDMCalculator.calculate(klines);
        Assertions.assertEquals(expected.size(), negativeDMs.size(), "Negative DM size mismatch");
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(0, negativeDMs.get(i).compareTo(expected.get(i)),
                    "Negative DM at index: " + i + " did not match expected value for: " + testName);
        }
    }

    static Stream<Arguments> negativeDMTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic NegativeDM",
                        createKlines(
                                new BigDecimal("110"), new BigDecimal("90"),
                                new BigDecimal("115"), new BigDecimal("95"),
                                new BigDecimal("112"), new BigDecimal("102")
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (90 - 95) = -5 --> DM = 0
                                new BigDecimal("0.00000")  // (95 - 102) = -7  --> DM = 0
                        )
                ),
                Arguments.of(
                        "NegativeDM Equals Diff",
                        createKlines(
                                new BigDecimal("110"), new BigDecimal("90"),
                                new BigDecimal("115"), new BigDecimal("85"),
                                new BigDecimal("120"), new BigDecimal("80")
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (90 - 85) = 5 > (115 - 110) = 5 --> 5 > 5 DM = 0
                                new BigDecimal("0.00000")  // (85 - 80) = 5 > (120 - 115) = 0 --> 5 > 5 DM = 0
                        )
                ),
                Arguments.of(
                        "Insufficient Data",
                        createKlines(
                                new BigDecimal("80"), new BigDecimal("60")
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
                                new BigDecimal("5.00000"), // (95 - 90) = 5 > (108 - 105) = 3 --> 5 > 3 DM = 5
                                new BigDecimal("5.00000"), // (90 - 85) = 5 > (107 - 108) = -1 --> 5 > -1 && 5 > 0 DM = 5
                                new BigDecimal("5.00000")  // (85 - 80) = 5 > (101 - 107) = -6 --> -5 > -6 && -5 > 0 DM = 5
                        )
                ),
                Arguments.of(
                        "NegativeDM Greater than Zero",
                        createKlines(
                                new BigDecimal("110"), new BigDecimal("90"),
                                new BigDecimal("115"), new BigDecimal("85"),
                                new BigDecimal("112"), new BigDecimal("80")
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // (90 - 85) = 5 > (115 - 110) = 5 --> 5 > 5 && 5 > 0 DM = 0
                                new BigDecimal("5.00000")  // (85 - 80) = 5 > (112 - 112) = 0 --> 5 > 0 && 5 > 0 DM = 5
                        )
                ),
                Arguments.of(
                        "NegativeDM Negative High Difference",
                        createKlines(
                                new BigDecimal("120"), new BigDecimal("100"),
                                new BigDecimal("95"), new BigDecimal("90")
                        ),
                        List.of(
                                new BigDecimal("10.00000")  // K2: (100 - 90) = 10 > (95 - 120) = -25 --> DM = 10
                        )
                )
        );
    }

    private static List<HistoricalKline> createKlines(BigDecimal... closeHighPairs) {
        List<HistoricalKline> klines = new ArrayList<>();
        for (int i = 0; i < closeHighPairs.length; i += 2) {
            klines.add(createKline(closeHighPairs[i], closeHighPairs[i + 1]));
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
