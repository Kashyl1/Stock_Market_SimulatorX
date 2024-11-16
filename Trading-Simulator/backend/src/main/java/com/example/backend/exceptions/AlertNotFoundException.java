package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested alert is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AlertNotFoundException extends AppException {
    public AlertNotFoundException(String message) {
        super(message);
    }
}
