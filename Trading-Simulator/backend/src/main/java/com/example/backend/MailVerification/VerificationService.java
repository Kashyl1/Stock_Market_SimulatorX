package com.example.backend.MailVerification;

import com.example.backend.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VerificationService {
    private final JavaMailSender mailSender;

    public String verificationToken() {
        return UUID.randomUUID().toString();
    }

    @Async
    public void sendVerificationEmail(User user, String verificationToken) throws MessagingException {
        String subject = "Verify your account";
        String verificationUrl = "http://localhost:3000/verify?token=" + verificationToken;
        String message = "<p>Hello, " + user.getFirstname() +
                "</p><p>Click on the link below to verify your account:</p>" +
                "<p><a href='" + verificationUrl + "'>" + verificationUrl + "</a></p>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true, "utf-8");
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(message, true);

        mailSender.send(mimeMessage);
    }
}
