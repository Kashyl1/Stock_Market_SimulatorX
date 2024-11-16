package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a user attempts to operate on an asset they do not own.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AssetNotOwnedException extends AppException {
    public AssetNotOwnedException(String message) {
        super(message);
    }
}
