package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user tries to transact an amount of asset exceeding their holdings.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientAssetAmountException extends AppException {
    public InsufficientAssetAmountException(String message) {
        super(message);
    }
}
