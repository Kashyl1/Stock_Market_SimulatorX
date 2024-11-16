package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an invalid amount is provided, such as a negative or zero value.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAmountException extends AppException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
