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
@Schema(description = "Request object for deleting the user's account")
public class DeleteAccountRequest {

    @Schema(description = "Confirmation text required to delete the account", example = "Delete user@example.com", required = true)
    private String confirmText;
}
