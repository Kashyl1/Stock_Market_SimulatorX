package com.example.backend.mailVerification;

import com.example.backend.userEvent.UserEventTrackingService;
import com.example.backend.userEvent.UserEvent;
import com.example.backend.exceptions.AccountAlreadyVerifiedException;
import com.example.backend.exceptions.EmailNotFoundException;
import com.example.backend.exceptions.InvalidVerificationTokenException;
import com.example.backend.exceptions.ResendEmailCooldownException;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Verification Mail Controller", description = "Handles email verification processes")
public class VerificationMailController {

    private final UserRepository userRepository;
    private final VerificationService verificationService;
    private final UserEventTrackingService userEventTrackingService;

    @GetMapping("/verify")
    @Operation(summary = "Verify user account", description = "Verifies the user account using a token sent via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid verification token", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Account already verified", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> verifyAccount(
            @Parameter(description = "Verification token sent to the user's email") @RequestParam("token") String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            throw new InvalidVerificationTokenException("Invalid verification token");
        }

        User user = userOptional.get();
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
            try {
                Map<String, Object> details = Map.of(
                        "verificationToken", token.substring(0, 10) + "***"
                );
                userEventTrackingService.logEvent(user.getEmail(), UserEvent.EventType.ACCOUNT_VERIFIED, details);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok("Verification successful. You will be redirected to login in few seconds.");
        } else {
            throw new AccountAlreadyVerifiedException("Account is already verified");
        }
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email", description = "Resends the verification email to the user's email address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification email resent successfully", content = @Content(schema = @Schema(implementation = ResendVerificationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Email not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Account already verified", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "Resend email cooldown", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResendVerificationResponse> resendVerificationEmail(@RequestBody User userRequest) {
        Optional<User> userOptional = userRepository.findByEmail(userRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new EmailNotFoundException("Email not found");
        }

        User user = userOptional.get();
        if (user.isVerified()) {
            throw new AccountAlreadyVerifiedException("Account is already verified");
        }

        if (!verificationService.canResendEmail(user.getEmail())) {
            throw new ResendEmailCooldownException("Please wait before requesting another verification email.");
        }

        String verificationToken = verificationService.verificationToken();
        user.setVerificationToken(verificationToken);
        userRepository.save(user);

        verificationService.sendVerificationEmail(user, verificationToken);

        try {
            Map<String, Object> details = Map.of(
                    "verificationToken", verificationToken.substring(0, 10) + "***"
            );
            userEventTrackingService.logEvent(user.getEmail(), UserEvent.EventType.RESEND_VERIFICATION_EMAIL, details);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResendVerificationResponse response = new ResendVerificationResponse(true);
        return ResponseEntity.ok(response);
    }
}