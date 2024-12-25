package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class WilderSmoothingCalculator implements IndicatorCalculator<List<BigDecimal>> {
    private final int PERIODS = 14;
    private final MathContext mathContext = new MathContext(20, RoundingMode.HALF_UP);

    @Override
    public List<BigDecimal> calculate(List<HistoricalKline> klines) {
        if (klines.size() < PERIODS) {
            throw new NotEnoughDataForCalculationException("Not enough data for Wilder's Smoothing calculation");
        }

        List<BigDecimal> smoothedValues = new ArrayList<>();

        // SMA
        BigDecimal sma = klines.stream()
                .limit(PERIODS)
                .map(HistoricalKline::getClosePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(PERIODS), mathContext);
        smoothedValues.add(sma);

        // 2. Smooth od Wildera
        BigDecimal previousSmoothedValue = sma;
        for (int i = PERIODS; i < klines.size(); i++) {
            BigDecimal currentValue = klines.get(i).getClosePrice();
            BigDecimal smoothedValue = previousSmoothedValue
                    .multiply(BigDecimal.valueOf(PERIODS - 1))
                    .add(currentValue)
                    .divide(BigDecimal.valueOf(PERIODS), mathContext);
            smoothedValues.add(smoothedValue);
            previousSmoothedValue = smoothedValue;
        }

        return smoothedValues;
    }

    public List<BigDecimal> calculateFromValues(List<BigDecimal> values) {
        if (values.size() < PERIODS) {
            throw new NotEnoughDataForCalculationException("Not enough data for Wilder's Smoothing calculation");
        }

        List<BigDecimal> smoothedValues = new ArrayList<>();

        // SMA
        BigDecimal sma = values.stream()
                .limit(PERIODS)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(PERIODS), mathContext);
        smoothedValues.add(sma);

        // 2. SMOOTH od  Wildera
        BigDecimal previousSmoothedValue = sma;
        for (int i = PERIODS; i < values.size(); i++) {
            BigDecimal currentValue = values.get(i);
            BigDecimal smoothedValue = previousSmoothedValue
                    .multiply(BigDecimal.valueOf(PERIODS - 1))
                    .add(currentValue)
                    .divide(BigDecimal.valueOf(PERIODS), mathContext);
            smoothedValues.add(smoothedValue);
            previousSmoothedValue = smoothedValue;
        }

        return smoothedValues;
    }

}
