package com.example.backend.auth;

import com.example.backend.analytics.EventTrackingService;
import com.example.backend.exceptions.AccountNotVerifiedException;
import com.example.backend.exceptions.EmailAlreadyExistsException;
import com.example.backend.MailVerification.VerificationService;
import com.example.backend.config.JwtService;
import com.example.backend.exceptions.InvalidTokenException;
import com.example.backend.user.Role;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private VerificationService verificationService;

    @Mock
    private EventTrackingService eventTrackingService;

    @InjectMocks
    private AuthenticationService authenticationService;



    @Test
    void register_ShouldRegisterUserAndSendVerificationEmail() throws MessagingException, UnsupportedEncodingException {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john.doe@example.com", "Password1");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(verificationService.verificationToken()).thenReturn("testVerificationToken");
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        AuthenticationResponse response = authenticationService.register(request);

        assertThat(response.getMessage()).isEqualTo("Registered successfully. Please verify your email.");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getFirstname()).isEqualTo("John");
        assertThat(savedUser.getLastname()).isEqualTo("Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(savedUser.isVerified()).isFalse();
        assertThat(savedUser.getVerificationToken()).isEqualTo("testVerificationToken");

        verify(verificationService).sendVerificationEmail(any(User.class), eq("testVerificationToken"));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() throws MessagingException, UnsupportedEncodingException {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john.doe@example.com", "Password1");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () -> authenticationService.register(request));

        verify(userRepository, never()).save(any(User.class));
        verify(verificationService, never()).sendVerificationEmail(any(User.class), anyString());
    }

    @Test
    void authenticate_ShouldReturnJwtToken_WhenCredentialsAreValid() {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "Password1");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .verified(true)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertThat(response.getToken()).isEqualTo("jwtToken");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_ShouldThrowAccountNotVerifiedException_WhenUserIsNotVerified() {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "Password1");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .verified(false)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        assertThrows(AccountNotVerifiedException.class, () -> {
            authenticationService.authenticate(request);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void authenticate_ShouldThrowException_WhenUserNotFound() {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "Password1");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.authenticate(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        String token = "valid-token";
        String newPassword = "NewSecurePassword123!";
        User user = User.builder()
                .id(1)
                .email("test@example.com")
                .password("oldPassword")
                .passwordResetToken(token)
                .build();

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        authenticationService.resetPassword(token, newPassword);

        assertEquals("encodedNewPassword", user.getPassword());
        assertNull(user.getPasswordResetToken());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void resetPassword_InvalidToken() {
        String token = "invalid-token";
        String newPassword = "NewSecurePassword123!";

        when(userRepository.findByPasswordResetToken(token)).thenReturn(Optional.empty());

        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () ->
                authenticationService.resetPassword(token, newPassword));

        assertEquals("Invalid password reset token", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}