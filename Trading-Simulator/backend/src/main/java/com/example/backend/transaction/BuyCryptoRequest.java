package com.example.backend.transaction;

import lombok.Data;

@Data
public class BuyCryptoRequest {
    private String symbol;
    private Double amountInUsd;
}