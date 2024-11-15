package com.example.backend.alert.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAlertDTO {
    private Integer emailAlertid;
    private Integer userid;
    private String userEmail;
    private Integer currencyid;
    private String currencySymbol;
    private EmailAlertType emailAlertType;
    private BigDecimal initialPrice;
    private BigDecimal targetPrice;
    private BigDecimal percentageChange;
    private boolean active;
}
