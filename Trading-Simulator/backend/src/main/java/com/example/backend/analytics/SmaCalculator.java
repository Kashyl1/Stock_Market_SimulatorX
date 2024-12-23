package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class SmaCalculator implements IndicatorCalculator<BigDecimal> {

    private final int periods;

    public SmaCalculator(int periods) {
        this.periods = periods;
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < periods) {
            throw new IllegalArgumentException("Not enough data for SMA calculation");
        }

        List<HistoricalKline> subset = klines.subList(klines.size() - periods, klines.size());

        BigDecimal sum = subset.stream()
                .map(HistoricalKline::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);
    }
}

