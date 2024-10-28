package com.example.backend.mailverification;

import com.example.backend.exceptions.EmailSendingException;
import com.example.backend.MailVerification.VerificationService;
import com.example.backend.user.User;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationMailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private VerificationService verificationService;

    @Test
    void verificationToken_ShouldReturnUniqueToken() {
        String token1 = verificationService.verificationToken();
        String token2 = verificationService.verificationToken();

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void sendVerificationEmail_ShouldSendEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstname("John");
        String token = UUID.randomUUID().toString();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        verificationService.sendVerificationEmail(user, token);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendVerificationEmail_ShouldThrowEmailSendingException_WhenMessagingExceptionOccurs() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstname("John");
        String token = UUID.randomUUID().toString();

        when(mailSender.createMimeMessage()).thenThrow(new MailException("Mail server error") {});

        assertThrows(EmailSendingException.class, () -> {
            verificationService.sendVerificationEmail(user, token);
        });
    }

    @Test
    void sendVerificationEmail_ShouldThrowEmailSendingException_WhenMailExceptionOccurs() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstname("John");
        String token = UUID.randomUUID().toString();

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        doThrow(new MailException("Mail server error") {}).when(mailSender).send(any(MimeMessage.class));

        assertThrows(EmailSendingException.class, () -> {
            verificationService.sendVerificationEmail(user, token);
        });
    }

    @Test
    void canResendEmail_ShouldReturnTrue_WhenNoPreviousRequest() {
        String email = "test@example.com";
        boolean result = verificationService.canResendEmail(email);
        assertThat(result).isTrue();
    }

    @Test
    void canResendEmail_ShouldReturnFalse_WhenRequestIsMadeWithinCooldown() {
        String email = "test@example.com";
        verificationService.canResendEmail(email);
        boolean result = verificationService.canResendEmail(email);
        assertThat(result).isFalse();
    }

    @Test
    void canResendEmail_ShouldReturnTrue_WhenRequestIsMadeAfterCooldown() throws InterruptedException {
        String email = "test@example.com";
        verificationService.canResendEmail(email);
        Thread.sleep(60000);
        boolean result = verificationService.canResendEmail(email);
        assertThat(result).isTrue();
    }
}
