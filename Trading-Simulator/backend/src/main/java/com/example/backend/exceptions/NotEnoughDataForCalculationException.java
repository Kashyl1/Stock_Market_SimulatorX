package com.example.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughDataForCalculationException extends AppException {
    public NotEnoughDataForCalculationException(String message) {
        super(message);
    }
}
