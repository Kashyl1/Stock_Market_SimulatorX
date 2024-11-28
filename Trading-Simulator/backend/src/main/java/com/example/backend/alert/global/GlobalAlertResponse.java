package com.example.backend.alert.global;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GlobalAlertResponse {

    private String message;

    private LocalDateTime scheduledFor;
}
