package com.bank.service;

import com.bank.dto.EmailDTO;

public interface EmailService {

    void sendEmail(EmailDTO emailDTO);

    void sendTransactionAlert(
            String email,
            String accountNumber,
            String messageText);

    void sendOtp(
            String email,
            String otp);

    void sendCustomEmail(
            String to,
            String subject,
            String message);

    void sendEmailWithAttachment(
            String to,
            String subject,
            String messageText,
            byte[] attachmentBytes,
            String attachmentName);
}