package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class EmaCalculator implements IndicatorCalculator<List<BigDecimal>> {
    private final int periods;
    private final MathContext mathContext = new MathContext(20, RoundingMode.HALF_UP);

    public EmaCalculator(int periods) {
        this.periods = periods;
    }

    @Override
    public List<BigDecimal> calculate(List<HistoricalKline> klines) {
        if (klines.size() < periods) {
            throw new NotEnoughDataForCalculationException("Not enough data for EMA calculation");
        }

        List<BigDecimal> emaSeries = new ArrayList<>();

        BigDecimal sma = klines.stream()
                .limit(periods)
                .map(HistoricalKline::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(periods), mathContext);
        emaSeries.add(sma);

        double k = 2.0 / (periods + 1.0);
        BigDecimal multiplier = BigDecimal.valueOf(k);
        BigDecimal oneMinusK = BigDecimal.valueOf(1 - k);

        BigDecimal previousEma = sma;

        for (int i = periods; i < klines.size(); i++) {
            BigDecimal close = klines.get(i).getClosePrice();
            BigDecimal ema = close.multiply(multiplier)
                    .add(previousEma.multiply(oneMinusK));
            emaSeries.add(ema);
            previousEma = ema;
        }

        return emaSeries;
    }

    public List<BigDecimal> calculateEmaFromValues(List<BigDecimal> values) {
        if (values.size() < periods) {
            throw new NotEnoughDataForCalculationException("Not enough data for EMA calculation");
        }

        List<BigDecimal> emaSeries = new ArrayList<>();

        BigDecimal sma = values.stream()
                .limit(periods)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(periods), mathContext);
        emaSeries.add(sma);

        double k = 2.0 / (periods + 1.0);
        BigDecimal multiplier = BigDecimal.valueOf(k);
        BigDecimal oneMinusK = BigDecimal.valueOf(1 - k);

        BigDecimal previousEma = sma;

        for (int i = periods; i < values.size(); i++) {
            BigDecimal close = values.get(i);
            BigDecimal ema = close.multiply(multiplier)
                    .add(previousEma.multiply(oneMinusK));
            emaSeries.add(ema);
            previousEma = ema;
        }

        return emaSeries;
    }
}
