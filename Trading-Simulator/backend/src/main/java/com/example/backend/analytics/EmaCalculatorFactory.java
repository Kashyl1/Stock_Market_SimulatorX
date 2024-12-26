package com.example.backend.analytics;

import org.springframework.stereotype.Component;

@Component
public class EmaCalculatorFactory {

    public EmaCalculator create(int periods) {
        return new EmaCalculator(periods);
    }
}
