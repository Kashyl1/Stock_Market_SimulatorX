package com.example.backend.auth;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request object for initiating password reset")
public class ForgotPasswordRequest {
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;
}
