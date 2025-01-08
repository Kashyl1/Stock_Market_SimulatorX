package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class AtrCalculator implements IndicatorCalculator<BigDecimal> {

    private final TrueRangeCalculator trueRangeCalculator;
    private final WilderSmoothingCalculator wilderSmoothingCalculator;

    public AtrCalculator(TrueRangeCalculator trueRangeCalculator,
                         WilderSmoothingCalculator wilderSmoothingCalculator) {
        this.trueRangeCalculator = trueRangeCalculator;
        this.wilderSmoothingCalculator = wilderSmoothingCalculator;
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        List<BigDecimal> trValues = trueRangeCalculator.calculate(klines);

        if (trValues.size() < 14) {
            throw new NotEnoughDataForCalculationException("Not enough data to calculate ATR");
        }

        List<BigDecimal> atrValues = wilderSmoothingCalculator.calculate(trValues);

        return atrValues.get(atrValues.size() - 1);
    }
}
