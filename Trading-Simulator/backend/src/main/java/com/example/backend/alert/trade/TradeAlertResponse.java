package com.example.backend.alert.trade;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TradeAlertResponse {
    private Integer tradeAlertId;
    private Integer portfolioId;
    private String portfolioName;
    private Integer currencyId;
    private String currencyName;
    private TradeAlertType tradeAlertType;
    private AlertConditionType conditionType;
    private BigDecimal conditionValue;
    private BigDecimal tradeAmount;
    private boolean active;
    private BigDecimal initialPrice;

    public static TradeAlertResponse fromTradeAlert(TradeAlert tradeAlert) {
        return TradeAlertResponse.builder()
                .tradeAlertId(tradeAlert.getTradeAlertId())
                .portfolioId(tradeAlert.getPortfolio().getPortfolioid())
                .portfolioName(tradeAlert.getPortfolio().getName())
                .currencyId(tradeAlert.getCurrency().getCurrencyid())
                .currencyName(tradeAlert.getCurrency().getName())
                .tradeAlertType(tradeAlert.getTradeAlertType())
                .conditionType(tradeAlert.getConditionType())
                .conditionValue(tradeAlert.getConditionValue())
                .tradeAmount(tradeAlert.getTradeAmount())
                .active(tradeAlert.isActive())
                .initialPrice(tradeAlert.getInitialPrice())
                .build();
    }
}
