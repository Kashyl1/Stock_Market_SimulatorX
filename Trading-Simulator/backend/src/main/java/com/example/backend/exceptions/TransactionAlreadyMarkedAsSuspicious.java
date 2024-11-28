package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TransactionAlreadyMarkedAsSuspicious extends RuntimeException{
    public TransactionAlreadyMarkedAsSuspicious(String message) { super(message); }
}
