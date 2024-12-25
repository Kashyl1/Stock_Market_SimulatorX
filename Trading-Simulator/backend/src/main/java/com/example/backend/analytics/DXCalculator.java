package com.example.backend.analytics;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class DXCalculator implements DualInputIndicatorCalculator<List<BigDecimal>> {
    MathContext mathContext = new MathContext(20, RoundingMode.HALF_UP);

    @Override
    public List<BigDecimal> calculate(List<BigDecimal> positiveDI, List<BigDecimal> negativeDI) {
        List<BigDecimal> dxValues = new ArrayList<>();
        for (int i = 0; i < positiveDI.size(); i++) {
            BigDecimal diff = positiveDI.get(i).subtract(negativeDI.get(i)).abs().setScale(5, RoundingMode.HALF_UP);
            BigDecimal sum = positiveDI.get(i).add(negativeDI.get(i)).setScale(5, RoundingMode.HALF_UP);
            BigDecimal dx = diff.divide(sum, mathContext).multiply(BigDecimal.valueOf(100)).setScale(5, RoundingMode.HALF_UP);
            dxValues.add(dx);
        }
        return dxValues;
    }
}
