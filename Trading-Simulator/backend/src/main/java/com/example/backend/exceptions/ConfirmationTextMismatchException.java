package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a confirmation text does not match the expected value.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConfirmationTextMismatchException extends AppException {
    public ConfirmationTextMismatchException(String message) {
        super(message);
    }
}
