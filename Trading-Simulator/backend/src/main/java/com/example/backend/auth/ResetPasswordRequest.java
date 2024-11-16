package com.example.backend.auth;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request object for resetting password")
public class ResetPasswordRequest {
    @Schema(description = "Password reset token", example = "ewarsrfgeth324rwefgdDSAFGHH...")
    private String token;

    @Schema(description = "New password", example = "NewPassword123!")
    private String newPassword;
}
