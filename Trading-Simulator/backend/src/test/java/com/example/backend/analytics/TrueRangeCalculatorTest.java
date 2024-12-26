package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

public class TrueRangeCalculatorTest {
    // FOrmula: MAX(High Price - Low Price, ABS(High Price - previous close Price), ABS(LowPrice - previous close price)

    @ParameterizedTest(name = "{0}")
    @MethodSource("trueRangeTestCases")
    void testTrueRangeCalculation(String testName, List<HistoricalKline> klines, List<BigDecimal> expected) {
        TrueRangeCalculator trueRangeCalculator = new TrueRangeCalculator();
        List<BigDecimal> trueRange = trueRangeCalculator.calculate(klines);
        Assertions.assertEquals(trueRange.size(), expected.size(), "True range size mismatch");

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(0, trueRange.get(i).compareTo(expected.get(i)),
                    "True range at index: " + i + " did not match the expected value for " + testName);
        }
    }

    static Stream<Arguments> trueRangeTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic True Range Calculation",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("90")),
                                createKline(new BigDecimal("105"), new BigDecimal("115"), new BigDecimal("95")),
                                createKline(new BigDecimal("108"), new BigDecimal("112"), new BigDecimal("102"))
                        ),
                        List.of(
                                new BigDecimal("20.00000"), // TR 2 = MAX(115-95=20, abs.115-100=15, abs.95-100=5 TR = 20
                                new BigDecimal("10.00000")  // TR 3 = max(112-102=10, abs.112-105=7, abs.112-102=10 TR = 10
                        )
                ),
                Arguments.of(
                        "True Range With Insufficient Data",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("95"))
                        ),
                        List.of() // Klines size cant be <= 1
                ),
                Arguments.of(
                        "True Range Calculation With Varying Values",
                        List.of(
                                createKline(new BigDecimal("50"), new BigDecimal("60"), new BigDecimal("40")),
                                createKline(new BigDecimal("55"), new BigDecimal("65"), new BigDecimal("50")),
                                createKline(new BigDecimal("60"), new BigDecimal("70"), new BigDecimal("55")),
                                createKline(new BigDecimal("65"), new BigDecimal("75"), new BigDecimal("60"))
                        ),
                        List.of(
                                new BigDecimal("15.00000"), // TR 2 = max(65-50=15, abs.65-50=15, 50-50=0) TR = 15
                                new BigDecimal("15.00000"), // TR 3 = max(70-55=15, abs.70-55=15, abs.55-55=0 TR = 15
                                new BigDecimal("15.00000")  // TR 4 = max(75-60=15, abs.75-60=15, abs.60-60=0 TR = 15
                        )
                ),
                Arguments.of(
                        "True Range Calculation With Varying High and Low Prices",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("105"), new BigDecimal("95")),
                                createKline(new BigDecimal("102"), new BigDecimal("108"), new BigDecimal("90")),
                                createKline(new BigDecimal("101"), new BigDecimal("107"), new BigDecimal("85")),
                                createKline(new BigDecimal("103"), new BigDecimal("116"), new BigDecimal("80"))
                        ),
                        List.of(
                                new BigDecimal("18.00000"), // TR 2 = max(108 - 90=18, abs.90-100=10, abs.108-100=8 TR = 18
                                new BigDecimal("22.00000"), // TR 3 = max(107-85=22, abs.85-102=17, abs.107-102=5 TR = 22
                                new BigDecimal("36.00000")  // TR 4 = max(116-80=36, abs.80-101=21, abs.116-101=15 TR = 36
                        )
                )
        );
    }

    private static HistoricalKline createKline(BigDecimal closePrice, BigDecimal highPrice, BigDecimal lowPrice) {
        return HistoricalKline.builder()
                .closePrice(closePrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .build();
    }
}
