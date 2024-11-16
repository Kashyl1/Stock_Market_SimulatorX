package com.example.backend.MailVerification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object indicating the success status of resending verification email")
public class ResendVerificationResponse {
    private boolean success;
}
