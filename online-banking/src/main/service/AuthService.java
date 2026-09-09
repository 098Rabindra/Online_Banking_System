package com.bank.service;

import com.bank.dto.LoginRequest;
import com.bank.dto.RegisterRequest;

public interface AuthService {

    String register(RegisterRequest request);

    String validateLogin(LoginRequest request);

    String verifyLoginOtp(
            String email,
            String otp);

    void resendOtp(String email);

    void sendForgotPasswordOtp(String email);

    boolean verifyForgotPasswordOtp(String email, String otp);

    void resetPassword(String email, String password);
}