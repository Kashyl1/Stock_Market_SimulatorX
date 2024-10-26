package com.example.backend.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddFundsRequest {
    private BigDecimal amount;
}
