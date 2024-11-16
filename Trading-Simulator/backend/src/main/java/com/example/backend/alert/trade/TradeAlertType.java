package com.example.backend.alert.trade;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of trade alert types")
public enum TradeAlertType {
    BUY,
    SELL
}
