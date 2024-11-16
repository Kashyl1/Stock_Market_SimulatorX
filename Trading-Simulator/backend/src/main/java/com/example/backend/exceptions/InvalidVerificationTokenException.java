package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an email verification token is invalid or expired.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidVerificationTokenException extends AppException {
    public InvalidVerificationTokenException(String message) {
        super(message);
    }
}
