package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user is not found in the system.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends AppException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
