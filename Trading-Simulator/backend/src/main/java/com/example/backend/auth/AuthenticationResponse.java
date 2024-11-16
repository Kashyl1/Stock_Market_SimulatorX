package com.example.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response object for authentication operations")
public class AuthenticationResponse {
    @Schema(description = "JWT token for authenticated user", example = "eyJhbGewrtregty54erfdDFSGFHGVCJ9...")
    private String token;

    @Schema(description = "Message related to the authentication response", example = "Registered successfully. Please verify your email.")
    private String message;

    @Schema(description = "Flag indicating if the verification email should be resent", example = "false")
    private Boolean resend;
}
