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
public class CciIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CciCalculator cciCalculator;

    @ParameterizedTest(name = "{0}")
    @MethodSource("cciTestCases")
    void testCciCalculation(String testName,
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
            BigDecimal result = cciCalculator.calculate(klines);

            if (expectException) {
                fail("Expected NotEnoughDataForCalculationException but no exception was thrown! Test: " + testName);
            }

            assertNotNull("CCI result should not be null!", result);
            assertEquals(
                    "CCI calculation mismatch for test: " + testName,
                    0,
                    expectedValue.compareTo(result)
            );

        } catch (NotEnoughDataForCalculationException e) {
            if (!expectException) {
                fail("NotEnoughDataForCalculationException was not expected for: " + testName);
            }
        } catch (CurrencyNotFoundException e) {
            fail("CurrencyNotFoundException should not happen in this test!");
        }
    }

    static Stream<Arguments> cciTestCases() {
        return Stream.of(
                Arguments.of(
                        "Standard calculation (21 klines)",
                        buildKlines(21),
                        new BigDecimal("126.7"),
                        false
                ),
                Arguments.of(
                        "Not enough data => expect exception",
                        buildKlines(19),
                        null,
                        true
                ),
                Arguments.of(
                        "All prices identical so result = 0 (meanDeviation=0)",
                        buildKlinesSamePrices(),
                        BigDecimal.ZERO,
                        false
                )
        );
    }

    private static List<HistoricalKline> buildKlines(int count) {
        return CciCalculatorTest.createKlines(count, 120, 100, 110);
    }

    private static List<HistoricalKline> buildKlinesSamePrices() {
        return CciCalculatorTest.createKlinesWithSamePrices();
    }
}
