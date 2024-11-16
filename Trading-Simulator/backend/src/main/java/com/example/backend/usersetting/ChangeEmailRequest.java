package com.example.backend.usersetting;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request object for changing the user's email address")
public class ChangeEmailRequest {

    @Schema(description = "Current password of the user", example = "currentPassword123", required = true)
    private String currentPassword;

    @Schema(description = "New email address", example = "new.email@example.com", required = true)
    private String newEmail;
}
