package com.example.backend.mailverification;

import com.example.backend.mailVerification.VerificationMailController;
import com.example.backend.mailVerification.VerificationService;
import com.example.backend.userEvent.UserEventTrackingService;
import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.exceptions.*;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VerificationMailController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class VerificationMailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @SpyBean
    private VerificationService verificationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserEventTrackingService userEventTrackingService;

    @MockBean
    private JavaMailSender mailSender;
    @Test
    public void verifyAccount_InvalidToken_ShouldReturnBadRequest() throws Exception {
        when(userRepository.findByVerificationToken("invalidToken")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "invalidToken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid verification token"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    public void verifyAccount_AlreadyVerified_ShouldReturnConflict() throws Exception {
        User user = new User();
        user.setVerified(true);

        when(userRepository.findByVerificationToken("validToken")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "validToken"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Account is already verified"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    public void verifyAccount_Success() throws Exception {
        User user = new User();
        user.setVerified(false);

        when(userRepository.findByVerificationToken("validToken")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "validToken"))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification successful. You will be redirected to login in few seconds."));

        verify(userRepository).save(user);
        assertTrue(user.isVerified());
    }

    @Test
    public void resendVerificationEmail_EmailNotFound_ShouldReturnNotFound() throws Exception {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        String jsonRequest = "{\"email\":\"nonexistent@example.com\"}";

        mockMvc.perform(post("/api/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Email not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    public void resendVerificationEmail_AccountAlreadyVerified_ShouldReturnConflict() throws Exception {
        User user = new User();
        user.setEmail("verified@example.com");
        user.setVerified(true);

        when(userRepository.findByEmail("verified@example.com")).thenReturn(Optional.of(user));

        String jsonRequest = "{\"email\":\"verified@example.com\"}";

        mockMvc.perform(post("/api/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Account is already verified"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    public void resendVerificationEmail_CooldownNotExpired_ShouldReturnTooManyRequests() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setVerified(false);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(verificationService.canResendEmail("user@example.com")).thenReturn(false);

        String jsonRequest = "{\"email\":\"user@example.com\"}";

        mockMvc.perform(post("/api/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Please wait before requesting another verification email."))
                .andExpect(jsonPath("$.status").value(429));
    }

    @Test
    public void resendVerificationEmail_EmailSendingFailed_ShouldReturnInternalServerError() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setVerified(false);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(verificationService.canResendEmail("user@example.com")).thenReturn(true);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailException("Mail server error") {})
                .when(mailSender).send(any(MimeMessage.class));

        String jsonRequest = "{\"email\":\"user@example.com\"}";

        mockMvc.perform(post("/api/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to send email to: user@example.com"))
                .andExpect(jsonPath("$.status").value(500));
    }


    @Test
    public void resendVerificationEmail_Success() throws Exception {
        User user = new User();
        user.setEmail("user@example.com");
        user.setVerified(false);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(verificationService.canResendEmail("user@example.com")).thenReturn(true);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doNothing().when(mailSender).send(any(MimeMessage.class));

        String jsonRequest = "{\"email\":\"user@example.com\"}";

        mockMvc.perform(post("/api/auth/resend-verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"success\":true}"));

        verify(verificationService).sendVerificationEmail(eq(user), anyString());
    }

}
