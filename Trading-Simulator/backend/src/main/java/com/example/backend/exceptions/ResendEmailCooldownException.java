package com.example.backend.exceptions;

public class ResendEmailCooldownException extends RuntimeException {
    public ResendEmailCooldownException(String message) {
        super(message);
    }
}
