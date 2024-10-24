package com.example.backend.MailVerification;

import com.example.backend.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VerificationService {
    private final JavaMailSender mailSender;
    private final Map<String, Instant> resendCooldowns = new HashMap<>();
    @Value("${app.base-url}")
    private String baseUrl;
    public String verificationToken() {
        return UUID.randomUUID().toString();
    }

    @Async
    public void sendVerificationEmail(User user, String verificationToken) throws MessagingException, UnsupportedEncodingException {
        String subject = "Activate Your Account at Royal Coin";
        String verificationUrl = baseUrl + "/verify?token=" + verificationToken;

        String textMessage = "Hello " + user.getFirstname() + ",\n\n" +
                "Thank you for registering at Royal Coin!\n" +
                "To activate your account, please click the link below:\n" +
                verificationUrl + "\n\n" +
                "If you did not register at our application, please ignore this email.\n\n" +
                "Best regards,\nRoyal Coin Team";

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<p>Hello " + user.getFirstname() + ",</p>" +
                "<p>Thank you for registering at <strong>Royal Coin</strong>!</p>" +
                "<p>To activate your account, please click the button below:</p>" +
                "<p style='text-align: center;'>" +
                "<a href='" + verificationUrl + "' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Activate Account</a>" +
                "</p>" +
                "<p>If the button above does not work, copy and paste the following link into your browser:</p>" +
                "<p><a href='" + verificationUrl + "'>" + verificationUrl + "</a></p>" +
                "<p>If you did not register at our application, please ignore this email.</p>" +
                "<br>" +
                "<p>Best regards,<br>Royal Coin Team</p>" +
                "<hr>" +
                "<p style='font-size: small;'>If you have any questions, feel free to contact us at RoyalCoinSupport@gmail.com.</p>" +
                "</body>" +
                "</html>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

        try {
            helper.setFrom("kamilsmtp@gmail.com", "Royal coin");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(textMessage, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Failed to send email to: " + user.getEmail(), e);
        }
        mailSender.send(mimeMessage);
    }

    public boolean canResendEmail(String email) {
        Instant now = Instant.now();
        Instant lastSent = resendCooldowns.get(email);
        if (lastSent == null || now.isAfter(lastSent.plusSeconds(60))) {
            resendCooldowns.put(email, now);
            return true;
        }
        return false;
    }
}