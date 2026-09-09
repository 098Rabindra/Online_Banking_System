package com.bank.service;

public interface OtpService {

    String generateOtp(String email);

    String generateOtp(String email, String type);

    boolean verifyOtp(String email, String otp);
}