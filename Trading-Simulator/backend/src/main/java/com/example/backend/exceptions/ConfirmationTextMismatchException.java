package com.example.backend.exceptions;

public class ConfirmationTextMismatchException extends RuntimeException {
    public ConfirmationTextMismatchException(String message) {
        super(message);
    }
}
