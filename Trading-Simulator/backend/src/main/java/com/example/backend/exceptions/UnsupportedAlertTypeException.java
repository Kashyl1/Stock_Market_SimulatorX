package com.example.backend.exceptions;

public class UnsupportedAlertTypeException extends RuntimeException {
    public UnsupportedAlertTypeException(String message) {
        super(message);
    }
}
