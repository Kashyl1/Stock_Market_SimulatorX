package com.example.backend.exceptions;

public class AccountAlreadyVerifiedException extends RuntimeException {
    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
}
