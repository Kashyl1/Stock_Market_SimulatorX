package com.example.backend.usersetting;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserSettingService userSettingService;

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userSettingService.changePassword(request);
        return ResponseEntity.ok(ChangePasswordResponse.builder()
                .message("Password changed successfully")
                .build());
    }

    @PostMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequest request) {
        userSettingService.deleteUserAccount(request.getConfirmText());
        return ResponseEntity.ok("User account and associated data have been deleted.");
    }

    @PostMapping("/change-email")
    public ResponseEntity<ChangeEmailResponse> changeEmail(@RequestBody ChangeEmailRequest request) {
        userSettingService.changeEmail(request);
        return ResponseEntity.ok(ChangeEmailResponse.builder()
                .message("Email changed successfully. Please verify your new email. You will be redirected to main page in 5 seconds...")
                .build());
    }
}
