package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.CurrencyNotFoundException;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
@ActiveProfiles("test")
public class WilliamsRIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WilliamsRCalculator williamsRCalculator;

    @ParameterizedTest(name = "{0}")
    @MethodSource("williamsRTestCases")
    void testWilliamsRCalculation(String testName,
                                  List<HistoricalKline> klines,
                                  BigDecimal expectedValue,
                                  boolean expectException) {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

        for (HistoricalKline kline : klines) {
            createAndSaveHistoricalKline(
                    currency,
                    "1h",
                    kline.getOpenTime(),
                    kline.getOpenPrice().doubleValue(),
                    kline.getHighPrice().doubleValue(),
                    kline.getLowPrice().doubleValue(),
                    kline.getClosePrice().doubleValue(),
                    kline.getCloseTime()
            );
        }

        try {
            BigDecimal result = williamsRCalculator.calculate(klines);

            if (expectException) {
                fail("Expected NotEnoughDataForCalculationException for: " + testName);
            } else {
                assertNotNull( "Williams %R should not be null!", String.valueOf(result));
                assertEquals(
                        "Williams %R calculation is different than expected value",
                        expectedValue.compareTo(result),
                        0
                );
            }
        } catch (NotEnoughDataForCalculationException e) {
            if (!expectException) {
                fail("There should not be exception! " + e.getMessage() + " for test: " + testName);
            }
        } catch (CurrencyNotFoundException e) {
            fail("There should not be CurrencyNotFoundException");
        }
    }

    static Stream<Arguments> williamsRTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic calculation",
                        buildKlines(14, 110, 90, 100),
                        new BigDecimal("-50.00000000"),
                        false
                ),
                Arguments.of(
                        "Close = Lowest so expected value = -100",
                        buildKlines(14, 120, 100, 100),
                        new BigDecimal("-100.00000000"),
                        false
                ),
                Arguments.of(
                        "Close = Highest so expected value = 0",
                        buildKlines(14, 130, 100, 130),
                        BigDecimal.ZERO,
                        false
                ),
                Arguments.of(
                        "Less than 14 klines, should be an exception",
                        buildKlines(13, 110, 90, 100),
                        null,
                        true
                ),
                Arguments.of(
                        "Highest == Lowest so Denominator = 0, result = 0",
                        buildKlinesSamePrice(),
                        BigDecimal.ZERO,
                        false
                )
        );
    }

    private static List<HistoricalKline> buildKlinesSamePrice() {
        return WilliamsRCalculatorTest.createKlinesSameHighLow(14);
    }

    private static List<HistoricalKline> buildKlines(int count, double highest, double lowest, double close) {
        return WilliamsRCalculatorTest.createKlines(count, highest, lowest, close);
    }
}