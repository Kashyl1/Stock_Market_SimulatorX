package com.example.backend.admin;

import com.example.backend.user.Role;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request to update a user's information")
public class UpdateUserRequest {
    @Schema(description = "First name of the user", example = "Jane")
    private String firstname;

    @Schema(description = "Last name of the user", example = "Smith")
    private String lastname;

    @Schema(description = "Verification status of the user", example = "true")
    private boolean verified;

    @Schema(description = "Role of the user", example = "ROLE_USER")
    private Role role;
}
