package com.example.backend.exceptions;

/**
 * Base class for all custom application exceptions.
 */
public abstract class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }
}
