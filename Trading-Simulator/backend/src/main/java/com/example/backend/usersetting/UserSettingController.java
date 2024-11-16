package com.example.backend.usersetting;

import com.example.backend.exceptions.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/user-settings")
@RequiredArgsConstructor
@Tag(name = "User Settings Controller", description = "Endpoints for managing user settings")
public class UserSettingController {

    private final UserSettingService userSettingService;

    @PostMapping("/change-password")
    @Operation(summary = "Change user password", description = "Allows the user to change their password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Request to change the user's password", required = true,
            content = @Content(schema = @Schema(implementation = ChangePasswordRequest.class)))
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        userSettingService.changePassword(request);
        return ResponseEntity.ok(ChangePasswordResponse.builder()
                .message("Password changed successfully")
                .build());
    }

    @PostMapping("/delete-account")
    @Operation(summary = "Delete user account", description = "Allows the user to delete their account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid confirmation text", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Request to delete the user's account", required = true,
            content = @Content(schema = @Schema(implementation = DeleteAccountRequest.class)))
    public ResponseEntity<String> deleteAccount(
            @RequestBody DeleteAccountRequest request) {
        userSettingService.deleteUserAccount(request.getConfirmText());
        return ResponseEntity.ok("User account and associated data have been deleted.");
    }

    @PostMapping("/change-email")
    @Operation(summary = "Change user email", description = "Allows the user to change their email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @RequestBody(description = "Request to change the user's email", required = true,
            content = @Content(schema = @Schema(implementation = ChangeEmailRequest.class)))
    public ResponseEntity<ChangeEmailResponse> changeEmail(
            @RequestBody ChangeEmailRequest request) {
        userSettingService.changeEmail(request);
        return ResponseEntity.ok(ChangeEmailResponse.builder()
                .message("Email changed successfully. Please verify your new email. You will be redirected to the main page in 5 seconds...")
                .build());
    }
}
