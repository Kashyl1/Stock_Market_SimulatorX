package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BullBearPowerCalculatorTest {
    /*
      Bull/Bear Power Indicator:
      Bull Power = High - EMA
      Bear Power = Low - EMA

      General Bull/Bear Power Indicator:
      BullBearPower = ((High - EMA) + (Low - EMA)) / 2
     */
    private BullBearPowerCalculator bullBearPowerCalculator;

    @BeforeEach
    void setUp() {
        EmaCalculatorFactory emaCalculatorFactory = new EmaCalculatorFactory();
        bullBearPowerCalculator = new BullBearPowerCalculator(emaCalculatorFactory);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("bullBearPowerTestCases")
    void testCalculateBullBearPower(String testName, List<HistoricalKline> klines, BigDecimal expected, boolean expectException) {
        if (expectException) {
            assertThrows(NotEnoughDataForCalculationException.class, () -> {
                bullBearPowerCalculator.calculate(klines);
            }, "Should throw NotEnoughDataForCalculationException for: " + testName);
        } else {
            BigDecimal actualBP = bullBearPowerCalculator.calculate(klines);

            assertNotNull(actualBP, "Bull/Bear Power should not be null for: " + testName);
            assertEquals(0, expected.compareTo(actualBP.setScale(5, RoundingMode.HALF_UP)),
                    "Bull/Bear Power does not match the expected value for: " + testName);
        }
    }

    static Stream<Arguments> bullBearPowerTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic Calculation Bull Bear power indicator",
                        List.of(
                                createKline(new BigDecimal("100.0"), new BigDecimal("90.0"), new BigDecimal("95.0")),
                                createKline(new BigDecimal("101.0"), new BigDecimal("91.0"), new BigDecimal("96.0")),
                                createKline(new BigDecimal("102.0"), new BigDecimal("92.0"), new BigDecimal("97.0")),
                                createKline(new BigDecimal("103.0"), new BigDecimal("93.0"), new BigDecimal("98.0")),
                                createKline(new BigDecimal("104.0"), new BigDecimal("94.0"), new BigDecimal("99.0")),
                                createKline(new BigDecimal("105.0"), new BigDecimal("95.0"), new BigDecimal("100.0")),
                                createKline(new BigDecimal("106.0"), new BigDecimal("96.0"), new BigDecimal("101.0")),
                                createKline(new BigDecimal("107.0"), new BigDecimal("97.0"), new BigDecimal("102.0")),
                                createKline(new BigDecimal("108.0"), new BigDecimal("98.0"), new BigDecimal("103.0")),
                                createKline(new BigDecimal("109.0"), new BigDecimal("99.0"), new BigDecimal("104.0")),
                                createKline(new BigDecimal("110.0"), new BigDecimal("100.0"), new BigDecimal("105.0")),
                                createKline(new BigDecimal("111.0"), new BigDecimal("101.0"), new BigDecimal("106.0")),
                                createKline(new BigDecimal("112.0"), new BigDecimal("102.0"), new BigDecimal("107.0")),
                                createKline(new BigDecimal("113.0"), new BigDecimal("103.0"), new BigDecimal("108.0"))
                        ),
                        new BigDecimal("6.00000"),
                        false
                ),
                Arguments.of(
                        "Insufficient Data for Calculation",
                        List.of(
                                createKline(new BigDecimal("100.0"), new BigDecimal("90.0"), new BigDecimal("95.0")),
                                createKline(new BigDecimal("101.0"), new BigDecimal("91.0"), new BigDecimal("96.0"))
                        ),
                        null,
                        true
                ),
                Arguments.of(
                        "Calculation with Zero EMA",
                        List.of(
                                createKline(new BigDecimal("100.0"), new BigDecimal("90.0"), new BigDecimal("95.0")),
                                createKline(new BigDecimal("101.0"), new BigDecimal("91.0"), new BigDecimal("96.0")),
                                createKline(new BigDecimal("102.0"), new BigDecimal("92.0"), new BigDecimal("97.0")),
                                createKline(new BigDecimal("103.0"), new BigDecimal("93.0"), new BigDecimal("98.0")),
                                createKline(new BigDecimal("104.0"), new BigDecimal("94.0"), new BigDecimal("99.0")),
                                createKline(new BigDecimal("105.0"), new BigDecimal("95.0"), new BigDecimal("100.0")),
                                createKline(new BigDecimal("106.0"), new BigDecimal("96.0"), new BigDecimal("101.0")),
                                createKline(new BigDecimal("107.0"), new BigDecimal("97.0"), new BigDecimal("102.0")),
                                createKline(new BigDecimal("108.0"), new BigDecimal("98.0"), new BigDecimal("103.0")),
                                createKline(new BigDecimal("109.0"), new BigDecimal("99.0"), new BigDecimal("104.0")),
                                createKline(new BigDecimal("110.0"), new BigDecimal("100.0"), new BigDecimal("105.0")),
                                createKline(new BigDecimal("111.0"), new BigDecimal("101.0"), new BigDecimal("106.0")),
                                createKline(new BigDecimal("112.0"), new BigDecimal("102.0"), new BigDecimal("107.0")),
                                createKline(new BigDecimal("113.0"), new BigDecimal("103.0"), new BigDecimal("108.0"))
                        ),
                        new BigDecimal("6.00000"),
                        false
                )
        );
    }

    private static HistoricalKline createKline(BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice) {
        return HistoricalKline.builder()
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .closePrice(closePrice)
                .build();
    }
}
