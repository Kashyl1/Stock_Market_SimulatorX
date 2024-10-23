package com.example.backend.usersetting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-settings")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserSettingService userSettingService;

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            ChangePasswordResponse response = userSettingService.changePassword(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(ChangePasswordResponse.builder()
                    .message("User is not authenticated")
                    .build());
        }
    }
    @PostMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequest request) {
        try {
            String response = userSettingService.deleteUserAccount(request.getConfirmText());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("User is not authenticated or deletion failed.");
        }
    }
}
