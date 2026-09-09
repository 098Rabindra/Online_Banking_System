package com.bank.controller;

import org.springframework.web.bind.annotation.*;

import com.bank.dto.OtpRequest;
import com.bank.dto.VerifyOtpRequest;
import com.bank.service.OtpService;

@RestController
@RequestMapping("/otp")
@CrossOrigin(origins = "*")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public String generateOtp(
            @RequestBody OtpRequest request) {

        return otpService.generateOtp(
                request.getEmail());
    }

    @PostMapping("/verify")
    public boolean verifyOtp(
            @RequestBody VerifyOtpRequest request) {

        return otpService.verifyOtp(
                request.getEmail(),
                request.getOtp());
    }
}