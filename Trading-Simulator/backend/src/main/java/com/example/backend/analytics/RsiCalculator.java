package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RsiCalculator implements IndicatorCalculator<BigDecimal> {
    private final int periods;

    public RsiCalculator(int periods) {
        this.periods = periods;
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < periods + 1) {
            throw new NotEnoughDataForCalculationException("Not enough data for RSI calculation");
        }

        List<HistoricalKline> relevantData = klines.subList(klines.size() - (periods + 1), klines.size());

        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;

        for (int i = 1; i <= periods; i++) {
            BigDecimal change = relevantData.get(i).getClosePrice().subtract(relevantData.get(i - 1).getClosePrice());
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                avgGain = avgGain.add(change);
            } else {
                avgLoss = avgLoss.add(change.abs());
            }
        }

        avgGain = avgGain.divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);
        avgLoss = avgLoss.divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);

        for (int i = periods + 1; i < relevantData.size(); i++) {
            BigDecimal currentChange = relevantData.get(i).getClosePrice().subtract(relevantData.get(i - 1).getClosePrice());
            BigDecimal currentGain = currentChange.compareTo(BigDecimal.ZERO) > 0 ? currentChange : BigDecimal.ZERO;
            BigDecimal currentLoss = currentChange.compareTo(BigDecimal.ZERO) < 0 ? currentChange.abs() : BigDecimal.ZERO;

            avgGain = avgGain.multiply(BigDecimal.valueOf(periods - 1))
                    .add(currentGain)
                    .divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);

            avgLoss = avgLoss.multiply(BigDecimal.valueOf(periods - 1))
                    .add(currentLoss)
                    .divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);
        }

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }

        BigDecimal rs = avgGain.divide(avgLoss, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(100).subtract(
                BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), RoundingMode.HALF_UP)
        ).setScale(2, RoundingMode.HALF_UP);
    }
}