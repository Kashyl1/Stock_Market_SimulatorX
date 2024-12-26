package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

class SmaCalculatorTest {


    /*
    * Formula for SMA (Simple Moving Average):
    *
    * SMA = (Price_1 + Price_2 + ... + Price_N) / N
    * Tescik ma 5 świec ich cena zamkniecia ponize 10-20-30-40-50
      Dajemy okres na 3 przez co sprawdzamy ostatnie 3 ceny zamknięcia dla naszych świec wynik 40 git jo je git
    /*
    @Test
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("smaTestCases")
    void testSmaCalculation(String testName, List<HistoricalKline> klines, int periods, BigDecimal expected, boolean expectException) {
        SmaCalculator smaCalculator = new SmaCalculator(periods);

        if (expectException) {
            Assertions.assertThrows(NotEnoughDataForCalculationException.class, () -> {
                smaCalculator.calculate(klines);
            }, "Expected NotEnoughDataForCalculationException for: " + testName);
        } else {
            BigDecimal result = smaCalculator.calculate(klines);
            Assertions.assertEquals(expected, result, "SMA calculation did not match the expected value for: " + testName);
        }
    }

    static Stream<Arguments> smaTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic calculation with sufficient data",
                        List.of(
                                createKline(new BigDecimal("10")),
                                createKline(new BigDecimal("20")),
                                createKline(new BigDecimal("30")),
                                createKline(new BigDecimal("40")),
                                createKline(new BigDecimal("50"))
                        ),
                        3,
                        new BigDecimal("40"), // (30 + 40 + 50) / 3 = 40
                        false
                ),
                Arguments.of(
                        "Calculation with all equal prices",
                        List.of(
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100"))
                        ),
                        5,
                        new BigDecimal("100"), // (100 + 100 + 100 + 100 + 100) / 5 = 100
                        false
                ),
                Arguments.of(
                        "Calculation with insufficient data",
                        List.of(
                                createKline(new BigDecimal("10")),
                                createKline(new BigDecimal("20"))
                        ),
                        3,
                        null,
                        true
                ),
                Arguments.of(
                        "Calculation with single-period SMA",
                        List.of(
                                createKline(new BigDecimal("50"))
                        ),
                        1,
                        new BigDecimal("50"), // SMA for 1 period is the same as the price
                        false
                ),
                Arguments.of(
                        "Calculation with negative prices",
                        List.of(
                                createKline(new BigDecimal("-10")),
                                createKline(new BigDecimal("-20")),
                                createKline(new BigDecimal("-30")),
                                createKline(new BigDecimal("-40")),
                                createKline(new BigDecimal("-50"))
                        ),
                        5,
                        new BigDecimal("-30"), // (-10 + -20 + -30 + -40 + -50) / 5 = -30
                        false
                )
        );
    }

    private static HistoricalKline createKline(BigDecimal closePrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        return kline;
    }
}