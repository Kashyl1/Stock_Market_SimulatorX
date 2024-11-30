package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an attempt is made to verify an account that is already verified.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class AccountAlreadyVerifiedException extends AppException {
    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
}
