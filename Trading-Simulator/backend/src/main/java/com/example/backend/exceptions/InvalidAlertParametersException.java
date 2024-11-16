package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when invalid parameters are provided for creating or updating an alert.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAlertParametersException extends AppException {
    public InvalidAlertParametersException(String message) {
        super(message);
    }
}
