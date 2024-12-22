package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class EmaCalculator implements IndicatorCalculator {
    private final int periods;

    public EmaCalculator(int periods) {
        this.periods = periods;
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < periods) {
            throw new NotEnoughDataForCalculationException("Not enough data for EMA calculation");
        }

        if (periods == 1 && klines.size() == 1) {
            return klines.get(0).getClosePrice().setScale(2, RoundingMode.HALF_UP);
        }

        List<HistoricalKline> initial = klines.subList(0, periods);
        BigDecimal sma = initial.stream()
                .map(HistoricalKline::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(periods), RoundingMode.HALF_UP);

        double k = 2.0 / (periods + 1.0);

        BigDecimal ema = sma;

        for (int i = periods; i < klines.size(); i++) {
            BigDecimal close = klines.get(i).getClosePrice().setScale(2, RoundingMode.HALF_UP);
            ema = close.multiply(BigDecimal.valueOf(k)).add(ema.multiply(BigDecimal.valueOf(1 - k)));
        }

        return ema.setScale(2, RoundingMode.HALF_UP);
    }
}
