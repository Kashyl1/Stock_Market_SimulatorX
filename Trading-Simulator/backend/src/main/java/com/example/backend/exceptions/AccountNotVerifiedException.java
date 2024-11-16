package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an operation requires an account to be verified, but it is not.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccountNotVerifiedException extends AppException {
    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
