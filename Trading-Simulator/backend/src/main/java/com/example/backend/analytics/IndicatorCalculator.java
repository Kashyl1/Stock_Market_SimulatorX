package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;

import java.util.List;

public interface IndicatorCalculator<T> {
    T calculate(List<HistoricalKline> klines);
}
