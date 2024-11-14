package com.example.backend.exceptions;

public class TransactionNotFoundException extends RuntimeException {
    public  TransactionNotFoundException(String message) {
        super(message);
    }
}
