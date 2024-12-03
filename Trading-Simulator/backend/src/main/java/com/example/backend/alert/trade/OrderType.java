package com.example.backend.alert.trade;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of order types")
public enum OrderType {
    LIMIT,
    STOP
}
