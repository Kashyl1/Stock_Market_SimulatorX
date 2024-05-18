package com.example.backend.MailVerification;

import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.mail.MessagingException;
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
            return ResponseEntity.badRequest().body("Invalid verification token");
        }

        User user = userOptional.get();
        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok("Verification successful. You will be redirected to login in few seconds.");
        } else {
            return ResponseEntity.ok("Account is already verified. You will be redirected to login.");
        }
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestBody User userRequest) {
        Optional<User> userOptional = userRepository.findByEmail(userRequest.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email not found");
        }

        User user = userOptional.get();
        if (user.isVerified()) {
            return ResponseEntity.badRequest().body("Account is already verified");
        }

        if (!verificationService.canResendEmail(user.getEmail())) {
            return ResponseEntity.status(429).body("Please wait before requesting another verification email.");
        }

        String verificationToken = verificationService.verificationToken();
        user.setVerificationToken(verificationToken);
        userRepository.save(user);

        try {
            verificationService.sendVerificationEmail(user, verificationToken);
            return ResponseEntity.ok().body("{\"success\": true}");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Failed to send verification email");
        }
    }
}