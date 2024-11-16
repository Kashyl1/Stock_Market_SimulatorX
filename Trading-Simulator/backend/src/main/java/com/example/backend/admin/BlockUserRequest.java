package com.example.backend.admin;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request to block or unblock a user")
public class BlockUserRequest {
    @Schema(description = "Blocked status", example = "true")
    private boolean blocked;
}
