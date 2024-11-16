package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an email address is not found in the system.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends AppException {
    public EmailNotFoundException(String message) {
        super(message);
    }
}
