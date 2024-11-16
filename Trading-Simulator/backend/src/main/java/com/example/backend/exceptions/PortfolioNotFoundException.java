package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested portfolio is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PortfolioNotFoundException extends AppException {
    public PortfolioNotFoundException(String message) {
        super(message);
    }
}
