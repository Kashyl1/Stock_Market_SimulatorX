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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BullBearPowerCalculatorIntegrationTest extends BaseIntegrationTest {
    /*
      Bull/Bear Power Indicator:
      Bull Power = High - EMA
      Bear Power = Low - EMA

      General Bull/Bear Power Indicator:
      BullBearPower = ((High - EMA) + (Low - EMA)) / 2
     */
    @Autowired
    private BullBearPowerCalculator bullBearPowerCalculator;

    @ParameterizedTest(name = "{0}")
    @MethodSource("bullBearPowerTestCases")
    void testBullBearPowerCalculation(String testName, List<HistoricalKline> klines, BigDecimal expectedBullBearPower) {
        Currency currency = createAndSaveCurrency("TCB", "Bitcoin");

        for (HistoricalKline kline : klines) {
            createAndSaveHistoricalKline(currency, "1h", kline.getOpenTime(),
                    kline.getOpenPrice().doubleValue(),
                    kline.getHighPrice().doubleValue(),
                    kline.getLowPrice().doubleValue(),
                    kline.getClosePrice().doubleValue(),
                    kline.getCloseTime());
        }

        try {
            List<HistoricalKline> fetchedKlines = historicalKlineRepository.findByCurrencyAndTimeIntervalOrderByOpenTimeAsc(currency, "1h");

            BigDecimal actualBullBearPower = bullBearPowerCalculator.calculate(fetchedKlines);

            assertNotNull(actualBullBearPower, "Bull/Bear Power should not be null");
            assertEquals(0, expectedBullBearPower.compareTo(actualBullBearPower.setScale(5, BigDecimal.ROUND_HALF_UP)),
                    "Bull/Bear Power did not match the expected value for test: " + testName);

        } catch (CurrencyNotFoundException e) {
            fail("Currency should be found for test: " + testName);
        } catch (NotEnoughDataForCalculationException e) {
            fail("Not enough data for Bull/Bear Power calculation for test: " + testName);
        }
    }

    static Stream<Arguments> bullBearPowerTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic Bull/Bear Power Calculation",
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
                        new BigDecimal("6.00000")
                ),
                Arguments.of(
                        "Bull/Bear Power with Negative Prices",
                        List.of(
                                createKline(new BigDecimal("-50.0"), new BigDecimal("-60.0"), new BigDecimal("-55.0")),
                                createKline(new BigDecimal("-40.0"), new BigDecimal("-50.0"), new BigDecimal("-45.0")),
                                createKline(new BigDecimal("-30.0"), new BigDecimal("-40.0"), new BigDecimal("-35.0")),
                                createKline(new BigDecimal("-20.0"), new BigDecimal("-30.0"), new BigDecimal("-25.0")),
                                createKline(new BigDecimal("-10.0"), new BigDecimal("-20.0"), new BigDecimal("-15.0")),
                                createKline(new BigDecimal("0.0"), new BigDecimal("-10.0"), new BigDecimal("-5.0")),
                                createKline(new BigDecimal("-50.0"), new BigDecimal("-60.0"), new BigDecimal("-55.0")),
                                createKline(new BigDecimal("-40.0"), new BigDecimal("-50.0"), new BigDecimal("-45.0")),
                                createKline(new BigDecimal("-30.0"), new BigDecimal("-40.0"), new BigDecimal("-35.0")),
                                createKline(new BigDecimal("-20.0"), new BigDecimal("-30.0"), new BigDecimal("-25.0")),
                                createKline(new BigDecimal("-10.0"), new BigDecimal("-20.0"), new BigDecimal("-15.0")),
                                createKline(new BigDecimal("0.0"), new BigDecimal("-10.0"), new BigDecimal("-5.0")),
                                createKline(new BigDecimal("-50.0"), new BigDecimal("-60.0"), new BigDecimal("-55.0")),
                                createKline(new BigDecimal("-40.0"), new BigDecimal("-50.0"), new BigDecimal("-45.0")),
                                createKline(new BigDecimal("-30.0"), new BigDecimal("-40.0"), new BigDecimal("-35.0")),
                                createKline(new BigDecimal("-20.0"), new BigDecimal("-30.0"), new BigDecimal("-25.0")),
                                createKline(new BigDecimal("-10.0"), new BigDecimal("-20.0"), new BigDecimal("-15.0")),
                                createKline(new BigDecimal("0.0"), new BigDecimal("-10.0"), new BigDecimal("-5.0"))
                        ),
                        new BigDecimal("21.56327") // SMA = -26.56327 Bear Power = −10.0−(−26.56327)=−10.0+26.56327=16.56327 } Bull power = 0.0−(−26.56327)=26.56327
                        // BullBearPower = (Bear Power + Bull Power) / 2 = 21.56327
                )
        );
    }

    private static HistoricalKline createKline(BigDecimal high, BigDecimal low, BigDecimal close) {
        return HistoricalKline.builder()
                .highPrice(high)
                .lowPrice(low)
                .closePrice(close)
                .build();
    }
}
