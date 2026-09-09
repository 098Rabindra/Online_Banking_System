package com.bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthCheckController {

    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuth(
            Authentication authentication) {

        System.out.println("CHECK AUTH = " + authentication);

        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Authenticated");
        }

        return ResponseEntity.status(401).body("Not Authenticated");
    }
}