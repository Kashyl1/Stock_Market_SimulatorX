package com.example.backend.admin;

import lombok.Data;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request to create a new admin user")
public class CreateAdminRequest {
    @Schema(description = "First name of the admin", example = "John")
    private String firstname;

    @Schema(description = "Last name of the admin", example = "Doe")
    private String lastname;

    @Schema(description = "Email address of the admin", example = "admin@example.com")
    private String email;

    @Schema(description = "Password for the admin account", example = "StrongP@ssw0rd")
    private String password;

    @Schema(description = "Initial balance for the admin account", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "Reserved balance for the admin account", example = "0.00")
    private BigDecimal reservedBalance;
}
