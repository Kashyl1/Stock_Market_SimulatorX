package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user tries to create a portfolio that already exists.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PortfolioAlreadyExistsException extends AppException {
    public PortfolioAlreadyExistsException(String message) {
        super(message);
    }
}
