package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an unsupported alert type is specified.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedAlertTypeException extends AppException {
    public UnsupportedAlertTypeException(String message) {
        super(message);
    }
}
