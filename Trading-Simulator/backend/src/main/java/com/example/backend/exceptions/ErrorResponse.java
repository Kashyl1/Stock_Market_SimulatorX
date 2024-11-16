package com.example.backend.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Error response containing details about the error")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2023-10-01T12:34:56")
    private String timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error message", example = "Invalid verification token")
    private String message;
}
