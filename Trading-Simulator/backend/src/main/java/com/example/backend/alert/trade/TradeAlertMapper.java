package com.example.backend.alert.trade;

import org.springframework.stereotype.Component;

@Component
public class TradeAlertMapper {
    public TradeAlertDTO toDTO(TradeAlert tradeAlert) {
        return TradeAlertDTO.builder()
                .tradeAlertid(tradeAlert.getTradeAlertid())
                .userid(tradeAlert.getUser().getId())
                .portfolioid(tradeAlert.getPortfolio().getPortfolioid())
                .userEmail(tradeAlert.getUser().getEmail())
                .currencyid(tradeAlert.getCurrency().getCurrencyid())
                .currencySymbol(tradeAlert.getCurrency().getSymbol())
                .tradeAlertType(tradeAlert.getTradeAlertType())
                .initialPrice(tradeAlert.getInitialPrice())
                .conditionPrice(tradeAlert.getConditionPrice())
                .tradeAmount(tradeAlert.getTradeAmount())
                .active(tradeAlert.isActive())
                .build();
    }
}
