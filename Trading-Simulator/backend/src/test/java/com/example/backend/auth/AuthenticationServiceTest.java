package com.example.backend.auth;

import com.example.backend.Exceptions.EmailAlreadyExistsException;
import com.example.backend.MailVerification.VerificationService;
import com.example.backend.config.JwtService;
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

    @InjectMocks
    private AuthenticationService authenticationService;



    @Test
    void register_ShouldRegisterUserAndSendVerificationEmail() throws MessagingException {
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
    void register_ShouldThrowException_WhenEmailAlreadyExists() throws MessagingException {
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
    void authenticate_ShouldReturnErrorMessage_WhenUserIsNotVerified() {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "Password1");

        User user = User.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.ROLE_USER)
                .verified(false)
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertThat(response.getMessage()).isEqualTo("User is not verified!");
        assertThat(response.getResend()).isTrue();

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
}