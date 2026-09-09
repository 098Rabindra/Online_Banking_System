package com.bank.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.EmailDTO;
import com.bank.entity.Otp;
import com.bank.enums.OtpStatus;
import com.bank.exception.OtpExpiredException;
import com.bank.repository.OtpRepository;
import com.bank.service.EmailService;
import com.bank.service.OtpService;
import com.bank.util.OtpGenerator;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;

    public OtpServiceImpl(
            OtpRepository otpRepository,
            EmailService emailService) {

        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public String generateOtp(String email) {
        return generateOtp(email, "REGISTRATION");
    }

    @Override
    @Transactional
    public String generateOtp(String email, String type) {

        email = email.trim().toLowerCase();

        otpRepository.deleteByEmail(email);

        String otpCode = OtpGenerator.generateOtp();

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtp(otpCode);
        otp.setExpiryTime(
                LocalDateTime.now().plusMinutes(5));
        otp.setVerified(false);
        otp.setStatus(OtpStatus.GENERATED);

        otpRepository.save(otp);

        System.out.println("\n=========================================================================");
        System.out.println("🔑 [OTP SYSTEM] Generated OTP for: " + email);
        System.out.println("   OTP Code: " + otpCode);
        System.out.println("   Type:     " + type);
        System.out.println("=========================================================================\n");

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setTo(email);

        String subject;
        String title;
        String description;

        if ("USER_LOGIN".equalsIgnoreCase(type)) {
            subject = "KBI Bank User Login OTP";
            title = "User Login Verification";
            description = "logging into your user account";
        } else if ("ADMIN_LOGIN".equalsIgnoreCase(type)) {
            subject = "KBI Bank Admin Login OTP";
            title = "Admin Login Verification";
            description = "logging into your administrator dashboard";
        } else if ("FORGOT_PASSWORD".equalsIgnoreCase(type)) {
            subject = "KBI Bank Password Recovery OTP";
            title = "Password Recovery Verification";
            description = "recovering your account password";
        } else {
            subject = "KBI Bank Registration OTP";
            title = "Registration Verification";
            description = "registering a new online banking account";
        }

        emailDTO.setSubject(subject);
        emailDTO.setMessage(com.bank.util.EmailTemplateUtil.buildHtmlOtpEmail(otpCode, title, description));

        emailService.sendEmail(emailDTO);

        return "OTP Sent Successfully";
    }

    @Override
    @Transactional
    public boolean verifyOtp(
            String email,
            String otpValue) {

        email = email.trim().toLowerCase();

        Otp otp = otpRepository
                .findByEmailAndOtp(email, otpValue)
                .orElseThrow(() ->
                        new OtpExpiredException("Invalid OTP"));

        if (otp.isVerified()) {
            return true;
        }

        if (otp.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            otp.setStatus(OtpStatus.EXPIRED);

            otpRepository.save(otp);

            throw new OtpExpiredException(
                    "OTP Expired");
        }

        otp.setVerified(true);
        otp.setStatus(OtpStatus.VERIFIED);

        otpRepository.save(otp);

        return true;
    }
}