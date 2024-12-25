package com.example.backend.analytics;

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
        List<BigDecimal> directionalIndicators = new ArrayList<>();
        for (int i = 0; i < smoothedTR.size(); i++) {
            BigDecimal indicator = dmValues.get(i)
                    .divide(smoothedTR.get(i), mathContext)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(5, RoundingMode.HALF_UP);
            directionalIndicators.add(indicator);
        }
        return directionalIndicators;
    }
}
