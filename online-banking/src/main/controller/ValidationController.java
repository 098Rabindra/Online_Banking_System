package com.bank.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ValidationController {

    private final UserRepository userRepository;

    @GetMapping("/check-user")
    public ResponseEntity<Map<String, Boolean>> checkUser(

            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String mobile,
            @RequestParam String aadhaar,
            @RequestParam String pan) {

        Map<String, Boolean> result = new HashMap<>();

        result.put(
                "emailExists",
                userRepository.existsByEmail(email));

        result.put(
                "usernameExists",
                userRepository.existsByUsername(username));

        result.put(
                "mobileExists",
                userRepository.existsByMobile(mobile));

        result.put(
                "aadhaarExists",
                userRepository.existsByAadhaar(aadhaar));

        result.put(
                "panExists",
                userRepository.existsByPan(pan));

        return ResponseEntity.ok(result);
    }
}