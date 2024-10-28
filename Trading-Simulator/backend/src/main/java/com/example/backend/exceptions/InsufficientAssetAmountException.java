package com.example.backend.exceptions;

public class InsufficientAssetAmountException extends RuntimeException {
    public InsufficientAssetAmountException(String message) {
        super(message);
    }
}
