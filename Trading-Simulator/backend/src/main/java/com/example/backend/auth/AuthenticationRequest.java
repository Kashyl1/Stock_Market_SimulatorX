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
@Schema(description = "Request object for user authentication")
public class AuthenticationRequest {
     @Schema(description = "User's email address", example = "user@example.com")
     private String email;

     @Schema(description = "User's password", example = "Password123!")
     private String password;
}
