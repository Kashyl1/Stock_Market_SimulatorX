package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when the price of a currency is not available.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class PriceNotAvailableException extends AppException {
    public PriceNotAvailableException(String message) {
        super(message);
    }
}
