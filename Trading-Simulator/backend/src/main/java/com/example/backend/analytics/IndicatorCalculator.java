package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;

import java.math.BigDecimal;
import java.util.List;

public interface IndicatorCalculator {
    BigDecimal calculate(List<HistoricalKline> klines);
}

