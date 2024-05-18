package com.example.backend.MailVerification;

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
}