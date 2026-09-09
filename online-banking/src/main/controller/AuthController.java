package com.bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.dto.LoginOtpRequest;
import com.bank.dto.LoginRequest;
import com.bank.dto.LoginResponse;
import com.bank.dto.RegisterRequest;
import com.bank.dto.VerifyOtpRequest;
import com.bank.dto.ResetPasswordRequest;
import com.bank.entity.User;
import com.bank.repository.UserRepository;
import com.bank.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(
            AuthService authService,
            UserRepository userRepository) {

        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        try {

            String role = authService.validateLogin(request);

            return ResponseEntity.ok(role);

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/verify-login-otp")
    public ResponseEntity<LoginResponse> verifyLoginOtp(
            @RequestBody LoginOtpRequest request) {

        try {

            String token =
                    authService.verifyLoginOtp(
                            request.getEmail(),
                            request.getOtp());

            User user =
                    userRepository
                    .findByEmail(
                            request.getEmail()
                                   .trim()
                                   .toLowerCase())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "User not found"));

            LoginResponse response =
                    new LoginResponse(
                            token,
                            user.getRole().name());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(
            @RequestParam String email) {

        try {

            authService.resendOtp(email);

            return ResponseEntity.ok(
                    "OTP_RESENT");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendForgotPasswordOtp(
            @RequestParam String email) {

        try {

            authService.sendForgotPasswordOtp(email);

            return ResponseEntity.ok("OTP_SENT");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyForgotPasswordOtp(
            @RequestBody VerifyOtpRequest request) {

        try {

            boolean verified = authService.verifyForgotPasswordOtp(
                    request.getEmail(),
                    request.getOtp());

            if (verified) {
                return ResponseEntity.ok("VERIFIED");
            } else {
                return ResponseEntity.badRequest()
                        .body("INVALID_OTP");
            }

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        try {

            authService.resetPassword(
                    request.getEmail(),
                    request.getPassword());

            return ResponseEntity.ok("PASSWORD_RESET_SUCCESS");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}