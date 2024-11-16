package com.example.backend.usersetting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object after changing the password")
public class ChangePasswordResponse {

    @Schema(description = "Message indicating the result of the operation", example = "Password changed successfully.")
    private String message;
}
