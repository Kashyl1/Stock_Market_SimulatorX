package com.example.backend.alert.trade;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeAlertDTO {
    private Integer tradeAlertid;
    private Integer userid;
    private Integer portfolioid;
    private String userEmail;
    private Integer currencyid;
    private String currencySymbol;
    private TradeAlertType tradeAlertType;
    private BigDecimal initialPrice;
    private BigDecimal conditionValue;
    private BigDecimal tradeAmount;
    private boolean active;
}
