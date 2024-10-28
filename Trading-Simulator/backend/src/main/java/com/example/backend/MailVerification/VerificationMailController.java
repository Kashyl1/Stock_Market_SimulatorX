package com.example.backend.MailVerification;

import com.example.backend.exceptions.AccountAlreadyVerifiedException;
import com.example.backend.exceptions.EmailNotFoundException;
import com.example.backend.exceptions.InvalidVerificationTokenException;
import com.example.backend.exceptions.ResendEmailCooldownException;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class VerificationMailController {

    private final UserRepository userRepository;
    private final VerificationService verificationService;

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            throw new InvalidVerificationTokenException("Invalid verification token");
        }

        User user = userOptional.get();
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok("Verification successful. You will be redirected to login in few seconds.");
        } else {
            throw new AccountAlreadyVerifiedException("Account is already verified");
        }
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestBody User userRequest) {
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

        ResendVerificationResponse response = new ResendVerificationResponse(true);
        return ResponseEntity.ok(response);
    }
}