package com.example.backend.analytics;

import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectionalIndicatorCalculator implements DualInputIndicatorCalculator<List<BigDecimal>> {
    MathContext mathContext = new MathContext(20, RoundingMode.HALF_UP);

    @Override
    public List<BigDecimal> calculate(List<BigDecimal> dmValues, List<BigDecimal> smoothedTR) {
        if (dmValues.size() != smoothedTR.size()) {
            throw new NotEnoughDataForCalculationException("Lists dmValues and smoothedTR must have the same size.");
        }

        List<BigDecimal> directionalIndicators = new ArrayList<>();
        for (int i = 0; i < smoothedTR.size(); i++) {
            if (smoothedTR.get(i).compareTo(BigDecimal.ZERO) == 0) {
                directionalIndicators.add(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP));
                continue;
            }
            BigDecimal indicator = dmValues.get(i)
                    .divide(smoothedTR.get(i), mathContext)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(5, RoundingMode.HALF_UP);
            directionalIndicators.add(indicator);
        }
        return directionalIndicators;
    }
}
