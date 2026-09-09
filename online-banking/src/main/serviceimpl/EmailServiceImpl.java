package com.bank.serviceimpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bank.dto.EmailDTO;
import com.bank.service.EmailService;

import jakarta.mail.internet.MimeMessage;

@Service
@SuppressWarnings("all")
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(
            JavaMailSender mailSender) {

        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(
            EmailDTO emailDTO) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailDTO.getTo());
            helper.setSubject(emailDTO.getSubject());

            String text = emailDTO.getMessage();
            boolean isHtml = text.trim().startsWith("<!DOCTYPE") 
                          || text.trim().startsWith("<html") 
                          || text.contains("</html>") 
                          || text.contains("</td>");

            helper.setText(text, isHtml);
            mailSender.send(message);

        } catch (jakarta.mail.MessagingException | org.springframework.mail.MailException e) {
            System.err.println("Error sending MIME message, falling back to SimpleMailMessage: " + e.getMessage());
            try {
                SimpleMailMessage message =
                        new SimpleMailMessage();

                message.setFrom(fromEmail);
                message.setTo(emailDTO.getTo());
                message.setSubject(emailDTO.getSubject());
                message.setText(emailDTO.getMessage());

                mailSender.send(message);
            } catch (org.springframework.mail.MailException ex) {
                System.err.println("\n========================================================");
                System.err.println("⚠️ ERROR: SMTP Mail Server connection failed! Message could not be sent.");
                System.err.println("To: " + emailDTO.getTo());
                System.err.println("Subject: " + emailDTO.getSubject());
                System.err.println("Error: " + ex.getMessage());
                System.err.println("========================================================\n");
            }
        }
    }

    @Override
    public void sendCustomEmail(
            String to,
            String subject,
            String messageText) {

        try {
            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(messageText);

            mailSender.send(message);
        } catch (org.springframework.mail.MailException e) {
            System.err.println("\n========================================================");
            System.err.println("⚠️ ERROR: Failed to send custom email to: " + to);
            System.err.println("Subject: " + subject);
            System.err.println("Message Content: " + messageText);
            System.err.println("========================================================\n");
            System.err.println("Details: " + e.getMessage());
        }
    }

    @Override
    public void sendTransactionAlert(
            String email,
            String accountNumber,
            String messageText) {

        try {
            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(email);

            message.setSubject(
                    "KBI Bank Transaction Alert");

            String body = """
                    Dear Customer,
                    
                    Account Number : %s
                    
                    %s
                    
                    Thank You,
                    KBI Bank
                    """.formatted(accountNumber, messageText);
            message.setText(body);

            mailSender.send(message);
        } catch (org.springframework.mail.MailException e) {
            System.err.println("\n========================================================");
            System.err.println("⚠️ ERROR: Failed to send transaction alert email to: " + email);
            System.err.println("Account Number: " + accountNumber);
            System.err.println("Message Text: " + messageText);
            System.err.println("========================================================\n");
            System.err.println("Details: " + e.getMessage());
        }
    }

    @Override
    public void sendOtp(
            String email,
            String otp) {

        try {
            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(email);

            message.setSubject(
                    "KBI Internet Banking OTP");

            String body = """
                    Dear Customer,
                    
                    Your OTP for Online Banking Login is:
                    
                    %s
                    
                    This OTP is valid for 5 minutes.
                    
                    Do not share this OTP with anyone.
                    
                    Regards,
                    KBI Bank
                    """.formatted(otp);
            message.setText(body);

            mailSender.send(message);
        } catch (org.springframework.mail.MailException e) {
            System.err.println("\n========================================================");
            System.err.println("⚠️ ERROR: Failed to send OTP email to: " + email);
            System.err.println("OTP Value: " + otp);
            System.err.println("========================================================\n");
            System.err.println("Details: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachment(
            String to,
            String subject,
            String messageText,
            byte[] attachmentBytes,
            String attachmentName) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(messageText, false);

            helper.addAttachment(attachmentName, new org.springframework.core.io.ByteArrayResource(attachmentBytes));

            mailSender.send(message);
        } catch (jakarta.mail.MessagingException | org.springframework.mail.MailException e) {
            System.err.println("⚠️ ERROR: Failed to send email with attachment to: " + to + ". Error: " + e.getMessage());
            System.err.println("Details: " + e.getMessage());
        }
    }
}