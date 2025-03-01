package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class WilliamsRCalculator implements IndicatorCalculator<BigDecimal> {

    /*
    The Williams %R formula
    %R = (Highest High - Close) / (Highest High - Lowest Low) * -100
    Where:
    Highest high - the highest price reached during 14 periods
    Close - Current close kline price
    Lowest low - the lowest price reached during 14 periods
    EXCEL OBLICZENIA
     */
    private static final int PERIODS = 14;

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < PERIODS) {
            throw new NotEnoughDataForCalculationException("Not enough data to calculate WilliamsR indicator");
        }

        List<HistoricalKline> subset = klines.subList(klines.size() - PERIODS, klines.size());

        BigDecimal highestHigh = subset.stream()
                .map(HistoricalKline::getHighPrice)
                .max(BigDecimal::compareTo)
                .orElseThrow();

        BigDecimal lowestLow = subset.stream()
                .map(HistoricalKline::getLowPrice)
                .min(BigDecimal::compareTo)
                .orElseThrow();

        BigDecimal close = klines.get(klines.size() - 1).getClosePrice();

        BigDecimal numerator = highestHigh.subtract(close);
        BigDecimal denominator = highestHigh.subtract(lowestLow);

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return numerator
                .divide(denominator, 8, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(-100));
    }
}
