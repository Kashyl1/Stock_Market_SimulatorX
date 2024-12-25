package com.example.backend.analytics;

import java.math.BigDecimal;
import java.util.List;

public interface DualInputIndicatorCalculator<T> {
    T calculate(List<BigDecimal> input1, List<BigDecimal> input2);
}
