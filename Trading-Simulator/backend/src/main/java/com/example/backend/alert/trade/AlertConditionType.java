package com.example.backend.alert.trade;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of alert condition types")
public enum AlertConditionType {
    PERCENTAGE,
    PRICE
}
