package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Component
public class BullBearPowerCalculator implements IndicatorCalculator<BigDecimal> {

    /*
    Formula for Bull/bear:
    BP power = (High price - EMA(13)) + (Low price - EMA(13)) / 2
     */

    private static final MathContext MATH_CONTEXT = new MathContext(20, RoundingMode.HALF_UP);
    private final EmaCalculator emaCalculator;
    private static final int PERIODS = 13;

    public BullBearPowerCalculator(EmaCalculatorFactory factory) {
        this.emaCalculator = factory.create(13);
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < PERIODS) {
            throw new NotEnoughDataForCalculationException("Not enough data for Bull/Bear calculations");
        }

        // EMA (13)
        List<BigDecimal> emaSeries = emaCalculator.calculate(klines);

        BigDecimal latestBullBearPower = BigDecimal.ZERO;

        for (int i = PERIODS - 1; i < klines.size(); i++) {
            BigDecimal highPrice = klines.get(i).getHighPrice();
            BigDecimal lowPrice = klines.get(i).getLowPrice();
            BigDecimal ema = emaSeries.get(i - (PERIODS - 1));
            BigDecimal bullPower = highPrice.subtract(ema).setScale(5, RoundingMode.HALF_UP);
            BigDecimal bearPower = lowPrice.subtract(ema).setScale(5, RoundingMode.HALF_UP);

            latestBullBearPower = bullPower.add(bearPower).divide(BigDecimal.valueOf(2), MATH_CONTEXT);

        }
        return latestBullBearPower;
    }
}
