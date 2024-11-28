package com.example.backend.auth;

import com.example.backend.mailVerification.VerificationService;
import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.exceptions.AccountNotVerifiedException;
import com.example.backend.exceptions.AuthenticationFailedException;
import com.example.backend.exceptions.EmailAlreadyExistsException;
import com.example.backend.exceptions.GlobalExceptionHandler;
import com.example.backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private VerificationService verificationService;

    @Test
    public void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john.doe@example.com", "Password1");
        AuthenticationResponse response = AuthenticationResponse.builder()
                .message("Registered successfully. Please verify your email.")
                .build();

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"firstname\": \"John\", \"lastname\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"Password1\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Registered successfully. Please verify your email.")));
    }

    @Test
    public void register_EmailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john.doe@example.com", "Password1");

        doThrow(new EmailAlreadyExistsException("Email already exists"))
                .when(authenticationService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"firstname\": \"John\", \"lastname\": \"Doe\", \"email\": \"john.doe@example.com\", \"password\": \"Password1\" }"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Email already exists")))
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    public void authenticate_Success() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "Password1");
        AuthenticationResponse response = AuthenticationResponse.builder()
                .token("jwtToken")
                .build();

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"john.doe@example.com\", \"password\": \"Password1\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("jwtToken")));
    }

    @Test
    public void authenticate_UserNotVerified() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "Password1");

        doThrow(new AccountNotVerifiedException("User is not verified"))
                .when(authenticationService).authenticate(any(AuthenticationRequest.class));

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"john.doe@example.com\", \"password\": \"Password1\" }"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("User is not verified")))
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    public void authenticate_InvalidCredentials() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("john.doe@example.com", "WrongPassword");

        doThrow(new AuthenticationFailedException("Invalid email or password"))
                .when(authenticationService).authenticate(any(AuthenticationRequest.class));

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"john.doe@example.com\", \"password\": \"WrongPassword\" }"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid email or password")))
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    public void authenticate_UserNotFound() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("nonexistent@example.com", "Password1");

        doThrow(new UsernameNotFoundException("User not found"))
                .when(authenticationService).authenticate(any(AuthenticationRequest.class));

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"nonexistent@example.com\", \"password\": \"Password1\" }"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found")))
                .andExpect(jsonPath("$.status", is(404)));
    }
}
