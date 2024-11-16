package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested currency is not found in the system.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CurrencyNotFoundException extends AppException {
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}
