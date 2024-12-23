package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class VolatilityCalculator implements IndicatorCalculator<BigDecimal> {
    private final int periods;

    public VolatilityCalculator(int periods) {
        this.periods = periods;
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < periods) {
            throw new NotEnoughDataForCalculationException("Not enough data for Volatility calculation");
        }

        List<HistoricalKline> recent = klines.subList(klines.size() - periods, klines.size());

        BigDecimal mean = recent.stream()
                .map(HistoricalKline::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);

        BigDecimal varianceSum = BigDecimal.ZERO;
        for (HistoricalKline k : recent) {
            BigDecimal diff = k.getClosePrice().subtract(mean);
            varianceSum = varianceSum.add(diff.pow(2));
        }

        BigDecimal variance = varianceSum.setScale(2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(periods).setScale(2, RoundingMode.HALF_UP), RoundingMode.HALF_UP);

        return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
    }
}


