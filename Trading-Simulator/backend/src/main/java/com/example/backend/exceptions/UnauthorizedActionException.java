package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user attempts an action they are not authorized to perform.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedActionException extends AppException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
