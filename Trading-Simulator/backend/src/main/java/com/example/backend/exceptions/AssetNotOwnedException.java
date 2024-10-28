package com.example.backend.exceptions;

public class AssetNotOwnedException extends RuntimeException {
    public AssetNotOwnedException(String message) {
        super(message);
    }
}
