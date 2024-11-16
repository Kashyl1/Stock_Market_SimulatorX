package com.example.backend.MailVerification;

import com.example.backend.alert.global.GlobalAlert;
import com.example.backend.alert.mail.EmailAlert;
import com.example.backend.alert.mail.EmailAlertType;
import com.example.backend.alert.trade.TradeAlert;
import com.example.backend.alert.trade.TradeAlertType;
import com.example.backend.currency.Currency;
import com.example.backend.exceptions.EmailSendingException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Tag(name = "Verification Service", description = "Service for handling email verification and notifications")
public class VerificationService {
    private final JavaMailSender mailSender;

    private final Map<String, Instant> resendCooldowns = new HashMap<>();

    @Value("${app.base-url}")
    private String baseUrl;

    private final UserRepository userRepository;

    @Operation(summary = "Generate verification token", description = "Generates a unique verification token for email verification")
    public String verificationToken() {
        return UUID.randomUUID().toString();
    }

    @Operation(summary = "Generate reset token", description = "Generates a unique token for password reset")
    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }

    @Operation(summary = "Send verification email", description = "Sends a verification email to the user")
    public void sendVerificationEmail(User user, String verificationToken) {
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
        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSender.createMimeMessage();
        } catch (MailException e) {
            throw new EmailSendingException("Failed to create email message for: " + user.getEmail());
        }

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom("kamilsmtp@gmail.com", "Royal coin");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(textMessage, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendingException("Failed to construct email message for: " + user.getEmail());
        }

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send email to: " + user.getEmail());
        }
    }

    @Operation(summary = "Check resend email cooldown", description = "Checks if the user can resend the verification email")
    public boolean canResendEmail(String email) {
        Instant now = Instant.now();
        Instant lastSent = resendCooldowns.get(email);
        if (lastSent == null || now.isAfter(lastSent.plusSeconds(60))) {
            resendCooldowns.put(email, now);
            return true;
        }
        return false;
    }

    @Operation(summary = "Send password reset email", description = "Sends a password reset email to the user")
    public void sendPasswordResetEmail(User user, String resetToken) {
        String subject = "Password Reset Request";
        String resetUrl = baseUrl + "/reset-password?token=" + resetToken;

        String textMessage = "Hello " + user.getFirstname() + ",\n\n" +
                "We received a request to reset your password.\n" +
                "To reset your password, please click the link below:\n" +
                resetUrl + "\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Best regards,\nRoyal Coin Team";

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<p>Hello " + user.getFirstname() + ",</p>" +
                "<p>We received a request to reset your password.</p>" +
                "<p>To reset your password, please click the button below:</p>" +
                "<p style='text-align: center;'>" +
                "<a href='" + resetUrl + "' style='background-color: #f44336; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                "</p>" +
                "<p>If the button above does not work, copy and paste the following link into your browser:</p>" +
                "<p><a href='" + resetUrl + "'>" + resetUrl + "</a></p>" +
                "<p>If you did not request a password reset, please ignore this email.</p>" +
                "<br>" +
                "<p>Best regards,<br>Royal Coin Team</p>" +
                "<hr>" +
                "<p style='font-size: small;'>If you have any questions, feel free to contact us at RoyalCoinSupport@gmail.com.</p>" +
                "</body>" +
                "</html>";
        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSender.createMimeMessage();
        } catch (MailException e) {
            throw new EmailSendingException("Failed to create email message for: " + user.getEmail());
        }

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom("kamilsmtp@gmail.com", "Royal coin");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(textMessage, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendingException("Failed to construct email message for: " + user.getEmail());
        }

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send email to: " + user.getEmail());
        }
    }

    @Operation(summary = "Send password reset link", description = "Initiates the password reset process by sending a reset link")
    public void sendPasswordResetLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with the given email does not exist"));

        String resetToken = generateResetToken();
        user.setPasswordResetToken(resetToken);
        userRepository.save(user);

        try {
            sendPasswordResetEmail(user, resetToken);
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send password reset email");
        }
    }

    @Operation(summary = "Send alert email", description = "Sends an email alert to the user based on certain conditions")
    public void sendAlertEmail(User user, Currency currency, BigDecimal currentPrice, EmailAlert emailAlert) {
        String subject = "Price Alert for " + currency.getName();

        String alertMessage;
        if (emailAlert.getEmailAlertType() == EmailAlertType.PERCENTAGE) {
            String direction = emailAlert.getPercentageChange().compareTo(BigDecimal.ZERO) > 0 ? "increased" : "decreased";
            alertMessage = String.format("The price of %s has %s by %.2f%%.\nCurrent price: $%.2f.",
                    currency.getName(), direction, emailAlert.getPercentageChange().abs(), currentPrice);
        } else if (emailAlert.getEmailAlertType() == EmailAlertType.PRICE) {
            alertMessage = String.format("The price of %s has reached your set threshold: $%.2f.\nCurrent price: $%.2f.",
                    currency.getName(), emailAlert.getTargetPrice(), currentPrice);
        } else {
            alertMessage = "Your alert has been triggered.";
        }

        String textMessage = "Hello " + user.getFirstname() + ",\n\n" +
                alertMessage + "\n\n" +
                "Best regards,\nRoyal Coin Team";

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<p>Hello " + user.getFirstname() + ",</p>" +
                "<p>" + alertMessage.replace("\n", "<br>") + "</p>" +
                "<br>" +
                "<p>Best regards,<br>Royal Coin Team</p>" +
                "<hr>" +
                "<p style='font-size: small;'>If you have any questions, feel free to contact us at RoyalCoinSupport@gmail.com.</p>" +
                "</body>" +
                "</html>";

        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSender.createMimeMessage();
        } catch (MailException e) {
            throw new EmailSendingException("Failed to create email message for: " + user.getEmail());
        }

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom("kamilsmtp@gmail.com", "Royal Coin");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(textMessage, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendingException("Failed to construct email message for: " + user.getEmail());
        }

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send email to: " + user.getEmail());
        }
    }

    @Operation(summary = "Send trade executed email", description = "Sends an email notification when a trade is executed")
    public void sendTradeExecutedEmail(User user, Currency currency, TradeAlert tradeAlert, BigDecimal tradeAmountUSD, TradeAlertType tradeAlertType) {
        String subject = "Trade Executed: " + tradeAlertType + " " + currency.getName();

        String tradeType = tradeAlertType == TradeAlertType.BUY ? "Buy" : "Sell";
        String tradeAmountDescription = "$" + tradeAmountUSD.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();

        String textMessage = "Hello " + user.getFirstname() + ",\n\n" +
                "Your automatic trade has been executed successfully.\n" +
                "Trade Type: " + tradeType + "\n" +
                "Currency: " + currency.getName() + " (" + currency.getSymbol() + ")\n" +
                "Trade Amount: " + tradeAmountDescription + "\n" +
                "Current Price: $" + currency.getCurrentPrice().setScale(2, BigDecimal.ROUND_HALF_UP) + "\n" +
                "Timestamp: " + LocalDateTime.now() + "\n\n" +
                "Best regards,\nRoyal Coin Team";

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<p>Hello " + user.getFirstname() + ",</p>" +
                "<p>Your automatic trade has been executed successfully.</p>" +
                "<ul>" +
                "<li><strong>Trade Type:</strong> " + tradeType + "</li>" +
                "<li><strong>Currency:</strong> " + currency.getName() + " (" + currency.getSymbol() + ")</li>" +
                "<li><strong>Trade Amount:</strong> " + tradeAmountDescription + "</li>" +
                "<li><strong>Current Price:</strong> $" + currency.getCurrentPrice().setScale(2, BigDecimal.ROUND_HALF_UP) + "</li>" +
                "<li><strong>Timestamp:</strong> " + LocalDateTime.now() + "</li>" +
                "</ul>" +
                "<p>Best regards,<br>Royal Coin Team</p>" +
                "<hr>" +
                "<p style='font-size: small;'>If you have any questions, feel free to contact us at RoyalCoinSupport@gmail.com.</p>" +
                "</body>" +
                "</html>";

        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSender.createMimeMessage();
        } catch (MailException e) {
            throw new EmailSendingException("Failed to create email message for: " + user.getEmail());
        }

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom("kamilsmtp@gmail.com", "Royal Coin");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(textMessage, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendingException("Failed to construct email message for: " + user.getEmail());
        }

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send email to: " + user.getEmail());
        }
    }

    @Operation(summary = "Send suspicious transaction email", description = "Sends an email alert if a suspicious transaction is detected")
    public void sendSuspiciousTransactionEmail(User user, Integer transactionId, Currency currency, BigDecimal amount, BigDecimal rate, String transactionType) {
        String subject = "Important: Suspicious Transaction Detected in Your Royal Coin Account";
        String textMessage = "Hello " + user.getFirstname() + ",\n\n" +
                "We have detected a suspicious transaction in your Royal Coin account.\n" +
                "Transaction Details:\n" +
                "Transaction ID: " + transactionId + "\n" +
                "Transaction Type: " + transactionType + "\n" +
                "Currency: " + currency.getName() + " (" + currency.getSymbol() + ")\n" +
                "Amount: $" + amount.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "Rate: $" + rate.setScale(2, RoundingMode.HALF_UP) + "\n" +
                "If you did not authorize this transaction, please contact our support team immediately.\n\n" +
                "Best regards,\nRoyal Coin Team";

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<p>Hello " + user.getFirstname() + ",</p>" +
                "<p>We have detected a <strong>suspicious transaction</strong> in your <strong>Royal Coin</strong> account.</p>" +
                "<h3>Transaction Details:</h3>" +
                "<ul>" +
                "<li><strong>Transaction ID:</strong> " + transactionId + "</li>" +
                "<li><strong>Transaction Type:</strong> " + transactionType + "</li>" +
                "<li><strong>Currency:</strong> " + currency.getName() + " (" + currency.getSymbol() + ")</li>" +
                "<li><strong>Amount:</strong> " + amount.setScale(2, RoundingMode.HALF_UP) + "</li>" +
                "<li><strong>Rate:</strong> $" + rate.setScale(8, RoundingMode.HALF_UP) + "</li>" +
                "<li><strong>Total price:</strong> $" + amount.multiply(rate).setScale(2, RoundingMode.HALF_UP) + "</li>" +
                "</ul>" +
                "<p>If you did not authorize this transaction, please contact our support team immediately.</p>" +
                "<p>Best regards,<br>Royal Coin Team</p>" +
                "<hr>" +
                "<p style='font-size: small;'>If you have any questions, feel free to contact us at <a href='mailto:RoyalCoinSupport@gmail.com'>RoyalCoinSupport@gmail.com</a>.</p>" +
                "</body>" +
                "</html>";

        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSender.createMimeMessage();
        } catch (MailException e) {
            throw new EmailSendingException("Failed to create email message for: " + user.getEmail());
        }

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom("no-reply@royalcoin.com", "Royal Coin");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(textMessage, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendingException("Failed to construct email message for: " + user.getEmail());
        }

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send email to: " + user.getEmail());
        }
    }

    @Operation(summary = "Send global alert email", description = "Sends a global alert email to all users")
    public void sendGlobalAlertEmail(User user, GlobalAlert globalAlert) {
        String subject = "Important Notification from Royal Coin";

        String message = globalAlert.getMessage();
        String scheduledDate = globalAlert.getScheduledFor() != null
                ? globalAlert.getScheduledFor().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "immediately";

        String textMessage = "Hello " + user.getFirstname() + ",\n\n" +
                message + "\n\n" +
                "This notification is scheduled for: " + scheduledDate + ".\n\n" +
                "Best regards,\nRoyal Coin Team";

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<p>Hello " + user.getFirstname() + ",</p>" +
                "<p>" + message.replace("\n", "<br>") + "</p>" +
                "<p>This notification is scheduled for: <strong>" + scheduledDate + "</strong>.</p>" +
                "<br>" +
                "<p>Best regards,<br>Royal Coin Team</p>" +
                "<hr>" +
                "<p style='font-size: small;'>If you have any questions, feel free to contact us at <a href='mailto:RoyalCoinSupport@gmail.com'>RoyalCoinSupport@gmail.com</a>.</p>" +
                "</body>" +
                "</html>";

        MimeMessage mimeMessage;
        try {
            mimeMessage = mailSender.createMimeMessage();
        } catch (MailException e) {
            throw new EmailSendingException("Failed to create email message for: " + user.getEmail());
        }

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom("no-reply@royalcoin.com", "Royal Coin");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(textMessage, htmlMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailSendingException("Failed to construct email message for: " + user.getEmail());
        }

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send email to: " + user.getEmail());
        }
    }

}