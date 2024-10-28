package com.example.backend.exceptions;

public class PriceNotAvailableException extends RuntimeException {
    public PriceNotAvailableException(String message) {
        super(message);
    }
}
