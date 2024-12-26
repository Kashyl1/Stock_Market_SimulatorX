package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.currency.HistoricalKlineRepository;
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
public class AdxCalculatorIntegrationTest extends BaseIntegrationTest {
    /** To sum up: formula for ADX step by step: N - PERIODS (we are calculating for N = 14)
     * 1. True Range (TR):
     *    TR = max(High - Low, |High - Previous Close|, |Low - Previous Close|)
     * 2. Directional Movement (+DM and -DM):
     *    +DM = max(Current High - Previous High, 0) (if this value is greater than the absolute decrease in Low)
     *    -DM = max(Previous Low - Current Low, 0) (if this value is greater than the absolute increase in High)
     * 3. Smooth the TR, +DM, and -DM using Wilder's Smoothing:
     *    Smoothed Value_n = ((Smoothed Value_{n-1} * (N - 1)) + Current Value) / N
     * 4. Directional Indicators (+DI and -DI):
     *    +DI = (Smoothed +DM / Smoothed TR) * 100
     *    -DI = (Smoothed -DM / Smoothed TR) * 100
     * 5. Directional Index (DX):
     *    DX = (|+DI - -DI| / (+DI + -DI)) * 100
     * 6. Average Directional Index (ADX):
     *    ADX is the smoothed average of DX values over the same period N:
     *    Smoothed ADX_n = ((Smoothed ADX_{n-1} * (N - 1)) + Current DX) / N
     * The calculation requires at least PERIODS * 2 + 1 data points to stabilize the ADX value.
     */

    @Autowired
    private AdxCalculator adxCalculator;

    @Autowired
    private TrueRangeCalculator trueRangeCalculator;

    @Autowired
    private PositiveDMCalculator positiveDMCalculator;

    @Autowired
    private NegativeDMCalculator negativeDMCalculator;

    @Autowired
    private WilderSmoothingCalculator wilderSmoothingCalculator;

    @Autowired
    private DirectionalIndicatorCalculator directionalIndicatorCalculator;

    @Autowired
    private DXCalculator dxCalculator;

    @ParameterizedTest(name = "{0}")
    @MethodSource("adxIntegrationTestCases")
    void testCalculateAdxIntegration(
            String testName,
            List<HistoricalKline> klines,
            BigDecimal expectedAdx
    ) {
        Currency currency = createAndSaveCurrency("ROYAL_COIN", "toMarka");

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

            BigDecimal calculatedAdx = adxCalculator.calculate(fetchedKlines);

            assertNotNull(calculatedAdx, "ADX should not be null for: " + testName);
            assertEquals(0, expectedAdx.compareTo(calculatedAdx),
                    "ADX is not matching expected value: " + testName);

        } catch (CurrencyNotFoundException e) {
            fail("Currency should be found: " + testName);
        } catch (NotEnoughDataForCalculationException e) {
            fail("No enough data for ADX calculation (min PERIODS * 2 + 1 klines): " + testName);
        }
    }

    static Stream<Arguments> adxIntegrationTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic ADX increasing prices Integration Test",
                        List.of(
                                createKline(100, 110, 90),
                                createKline(105, 115, 95),
                                createKline(110, 120, 100),
                                createKline(115, 125, 105),
                                createKline(120, 130, 110),
                                createKline(125, 135, 115),
                                createKline(130, 140, 120),
                                createKline(135, 145, 125),
                                createKline(140, 150, 130),
                                createKline(145, 155, 135),
                                createKline(150, 160, 140),
                                createKline(155, 165, 145),
                                createKline(160, 170, 150),
                                createKline(165, 175, 155),
                                createKline(170, 180, 160),
                                createKline(175, 185, 165),
                                createKline(180, 190, 170),
                                createKline(185, 195, 175),
                                createKline(190, 200, 180),
                                createKline(195, 205, 185),
                                createKline(200, 210, 190),
                                createKline(205, 215, 195),
                                createKline(210, 220, 200),
                                createKline(215, 225, 205),
                                createKline(220, 230, 210),
                                createKline(225, 235, 215),
                                createKline(230, 240, 220),
                                createKline(235, 245, 225),
                                createKline(240, 250, 230)
                        ),
                        new BigDecimal("100.00000")
                ),
                Arguments.of(
                        "ADX with Constant Prices",
                        List.of(
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90),
                                createKline(100, 110, 90)
                        ),
                        new BigDecimal("0.00000")
                ),
                Arguments.of(
                        "BASIC ADX test",
                        List.of(
                                createKline(100, 110, 90),
                                createKline(105, 115, 95),
                                createKline(110, 120, 100),
                                createKline(108, 112, 104),
                                createKline(120, 116, 111),
                                createKline(125, 123, 112),
                                createKline(140, 150, 143),
                                createKline(141, 145, 141),
                                createKline(142, 150, 144),
                                createKline(145, 145, 133),
                                createKline(150, 152, 151),
                                createKline(141, 144, 135),
                                createKline(155, 152, 146),
                                createKline(135, 122, 121),
                                createKline(132, 131, 128),
                                createKline(126, 132, 127),
                                createKline(132, 128, 125),
                                createKline(133, 125, 123),
                                createKline(144, 143, 142),
                                createKline(131, 133, 132),
                                createKline(154, 161, 154),
                                createKline(122, 131, 128),
                                createKline(133, 145, 133),
                                createKline(150, 152, 151),
                                createKline(141, 144, 135),
                                createKline(155, 152, 146),
                                createKline(135, 122, 121),
                                createKline(141, 144, 135),
                                createKline(135, 152, 142),
                                createKline(148, 150, 147),
                                createKline(137, 147, 143)
                        ),
                        new BigDecimal("15.674612975583090379") // from excel
                )
        );
    }

    private static HistoricalKline createKline(double closePrice, double highPrice, double lowPrice) {
        return HistoricalKline.builder()
                .closePrice(BigDecimal.valueOf(closePrice))
                .highPrice(BigDecimal.valueOf(highPrice))
                .lowPrice(BigDecimal.valueOf(lowPrice))
                .build();
    }
}
