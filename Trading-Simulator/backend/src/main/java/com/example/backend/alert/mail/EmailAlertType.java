package com.example.backend.alert.mail;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of email alert types")
public enum EmailAlertType {
    PERCENTAGE,
    PRICE
}
