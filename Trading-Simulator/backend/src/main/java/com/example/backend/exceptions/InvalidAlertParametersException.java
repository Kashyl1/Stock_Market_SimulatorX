package com.example.backend.exceptions;

public class InvalidAlertParametersException extends RuntimeException {
    public InvalidAlertParametersException(String message) {
        super(message);
    }
}
