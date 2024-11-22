package com.example.backend.auth;

import com.example.backend.UserEvent.EventTrackingService;
import com.example.backend.UserEvent.UserEvent;
import com.example.backend.exceptions.*;
import com.example.backend.MailVerification.VerificationService;
import com.example.backend.config.JwtService;
import com.example.backend.user.Role;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Tag(name = "Authentication Service", description = "Service layer for authentication and registration")
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationService verificationService;
    private final EventTrackingService eventTrackingService;

    @Operation(summary = "Register a new user", description = "Handles user registration and sends verification email")
    public AuthenticationResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        String verificationToken = verificationService.verificationToken();

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .verified(false)
                .verificationToken(verificationToken)
                .balance(BigDecimal.ZERO)
                .build();

        userRepository.save(user);
        verificationService.sendVerificationEmail(user, verificationToken);

        eventTrackingService.logEvent(request.getEmail(), UserEvent.EventType.REGISTRATION);

        return AuthenticationResponse.builder()
                .message("Registered successfully. Please verify your email.")
                .build();
    }

    @Operation(summary = "Authenticate a user", description = "Authenticates user credentials and returns JWT token")
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    ));

        } catch (LockedException e) {
            throw new AccountBlockedException("Account is blocked");
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid email or password");
        }
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isVerified()) {
            throw new AccountNotVerifiedException("User is not verified");
        }
        var jwtToken = jwtService.generateToken(user);

        eventTrackingService.logEvent(request.getEmail(), UserEvent.EventType.LOGIN);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Cacheable(value = "currentUser", key = "#email")
    @Operation(summary = "Get current user", description = "Retrieves the current authenticated user by email")
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Operation(summary = "Get current user email", description = "Retrieves the email of the current authenticated user")
    public String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
    }

    @Operation(summary = "Reset password", description = "Resets the user's password using the provided token")
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);

        userRepository.save(user);
    }
}
