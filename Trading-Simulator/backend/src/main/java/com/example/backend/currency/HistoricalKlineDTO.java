package com.example.backend.currency;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HistoricalKlineDTO {
    private Long openTime;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal volume;
    private Long closeTime;

    public HistoricalKlineDTO(HistoricalKline kline) {
        this.openTime = kline.getOpenTime();
        this.openPrice = kline.getOpenPrice();
        this.highPrice = kline.getHighPrice();
        this.lowPrice = kline.getLowPrice();
        this.closePrice = kline.getClosePrice();
        this.volume = kline.getVolume();
        this.closeTime = kline.getCloseTime();
    }
}
