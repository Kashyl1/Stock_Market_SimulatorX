package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user attempts to resend an email before the cooldown period has expired.
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ResendEmailCooldownException extends AppException {
    public ResendEmailCooldownException(String message) {
        super(message);
    }
}
