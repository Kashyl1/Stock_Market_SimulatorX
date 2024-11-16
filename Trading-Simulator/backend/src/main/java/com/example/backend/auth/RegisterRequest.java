package com.example.backend.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for user registration")
public class RegisterRequest {

    @JsonProperty("firstname")
    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "John")
    private String firstname;

    @JsonProperty("lastname")
    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Doe")
    private String lastname;

    @JsonProperty("email")
    @NotBlank(message = "Email is required")
    @Email(message = "The email address is invalid")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Schema(description = "User's password", example = "Password123!")
    private String password;
}
