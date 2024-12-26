package com.example.backend.analytics;

import com.example.backend.exceptions.NotEnoughDataForCalculationException;
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

        if (positiveDI.size() != negativeDI.size()) {
            throw new NotEnoughDataForCalculationException("PositiveDI and NegativeDI lists must be of the same size.");
        }

        for (int i = 0; i < positiveDI.size(); i++) {
            BigDecimal pos = positiveDI.get(i);
            BigDecimal neg = negativeDI.get(i);

            BigDecimal sum = pos.add(neg).setScale(5, RoundingMode.HALF_UP);

            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                dxValues.add(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP));
                continue;
            }

            BigDecimal diff = pos.subtract(neg).abs().setScale(5, RoundingMode.HALF_UP);
            BigDecimal dx = diff.divide(sum, mathContext).multiply(BigDecimal.valueOf(100)).setScale(5, RoundingMode.HALF_UP);
            dxValues.add(dx);
        }
        return dxValues;
    }
}
