package com.example.backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Data Transfer Object for user information")
public class UserDTO {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Integer id;

    @Schema(description = "First name of the user", example = "John")
    private String firstname;

    @Schema(description = "Last name of the user", example = "Doe")
    private String lastname;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Verification status of the account", example = "true")
    private boolean verified;

    @Schema(description = "Role of the user", example = "ROLE_USER")
    private Role role;

    @Schema(description = "Timestamp when the account was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the account was last updated", example = "2023-10-05T15:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "User's account balance", example = "500.00")
    private BigDecimal balance;

    @Schema(description = "User's reserved balance", example = "0.00")
    private BigDecimal reservedBalance;

    @Schema(description = "Account blocked status", example = "false")
    private boolean blocked;
}
