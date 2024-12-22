package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RsiCalculator implements IndicatorCalculator {
    private final int periods;

    public RsiCalculator(int periods) {
        this.periods = periods;
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < periods + 1) {
            throw new NotEnoughDataForCalculationException("Not enough data for RSI calculation");
        }

        List<HistoricalKline> recent = klines.subList(klines.size() - (periods + 1), klines.size());

        BigDecimal gainSum = BigDecimal.ZERO;
        BigDecimal lossSum = BigDecimal.ZERO;

        for (int i = 1; i < recent.size(); i++) {
            BigDecimal change = recent.get(i).getClosePrice().subtract(recent.get(i - 1).getClosePrice());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                gainSum = gainSum.add(change);
            } else {
                lossSum = lossSum.add(change.abs());
            }
        }

        BigDecimal avgGain = gainSum.divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);
        BigDecimal avgLoss = lossSum.divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal rs = avgGain.divide(avgLoss, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), RoundingMode.HALF_UP));
    }
}