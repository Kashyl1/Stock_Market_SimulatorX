package com.example.backend.usersetting;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.exceptions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSettingController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSettingService userSettingService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void changePassword_Success() throws Exception {
        mockMvc.perform(post("/api/user-settings/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\": \"currentPass\", \"newPassword\": \"NewPass1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Password changed successfully")));

        verify(userSettingService).changePassword(any(ChangePasswordRequest.class));
    }

    @Test
    public void changePassword_InvalidCurrentPassword_ShouldReturnBadRequest() throws Exception {
        doThrow(new InvalidPasswordException("Current password is incorrect"))
                .when(userSettingService).changePassword(any(ChangePasswordRequest.class));

        mockMvc.perform(post("/api/user-settings/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\": \"wrongPass\", \"newPassword\": \"NewPass1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Current password is incorrect")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    public void deleteAccount_Success() throws Exception {
        mockMvc.perform(post("/api/user-settings/delete-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmText\": \"Delete test@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User account and associated data have been deleted."));

        verify(userSettingService).deleteUserAccount("Delete test@example.com");
    }

    @Test
    public void deleteAccount_ConfirmationTextMismatch_ShouldReturnBadRequest() throws Exception {
        doThrow(new ConfirmationTextMismatchException("Confirmation text is incorrect."))
                .when(userSettingService).deleteUserAccount(anyString());

        mockMvc.perform(post("/api/user-settings/delete-account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"confirmText\": \"Delete wrong@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Confirmation text is incorrect.")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    public void changeEmail_Success() throws Exception {
        ChangeEmailResponse response = ChangeEmailResponse.builder()
                .message("Email changed successfully. Please verify your new email. You will be redirected to main page in 5 seconds...")
                .build();

        when(userSettingService.changeEmail(any(ChangeEmailRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/user-settings/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\": \"currentPass\", \"newEmail\": \"newemail@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(response.getMessage())));

        verify(userSettingService).changeEmail(any(ChangeEmailRequest.class));
    }

    @Test
    public void changeEmail_InvalidCurrentPassword_ShouldReturnBadRequest() throws Exception {
        doThrow(new InvalidPasswordException("Current password is incorrect"))
                .when(userSettingService).changeEmail(any(ChangeEmailRequest.class));

        mockMvc.perform(post("/api/user-settings/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\": \"wrongPass\", \"newEmail\": \"newemail@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Current password is incorrect")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    public void changeEmail_EmailSendingFailed_ShouldReturnInternalServerError() throws Exception {
        doThrow(new EmailSendingException("Failed to send verification email"))
                .when(userSettingService).changeEmail(any(ChangeEmailRequest.class));

        mockMvc.perform(post("/api/user-settings/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\": \"currentPass\", \"newEmail\": \"newemail@example.com\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Failed to send verification email")))
                .andExpect(jsonPath("$.status", is(500)));
    }
}
