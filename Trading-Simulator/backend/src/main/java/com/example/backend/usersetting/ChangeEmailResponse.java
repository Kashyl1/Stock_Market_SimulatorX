package com.example.backend.usersetting;

import lombok.Builder;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "Response object after changing the email address")
public class ChangeEmailResponse {

    @Schema(description = "Message indicating the result of the operation", example = "Email changed successfully. Please verify your new email.")
    private String message;
}
