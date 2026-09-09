package com.bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import com.bank.entity.User;
import com.bank.repository.UserRepository;

import com.bank.entity.Loan;
import com.bank.enums.LoanStatus;
import com.bank.service.LoanService;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {

    private final LoanService loanService;
    private final UserRepository userRepository;

    public AdminController(
            LoanService loanService,
            UserRepository userRepository) {

        this.loanService = loanService;
        this.userRepository = userRepository;
    }

    @PutMapping("/users/update-aadhaar")
    public ResponseEntity<?> adminUpdateAadhaar(
            @RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String newAadhaar = payload.get("newAadhaar");

        if (accountNumber == null || accountNumber.trim().isEmpty() ||
            newAadhaar == null || newAadhaar.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number and new Aadhaar number are required."));
        }

        if (!newAadhaar.matches("\\d{12}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Aadhaar number must be exactly 12 digits."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        if (userRepository.existsByAadhaar(newAadhaar.trim()) && !newAadhaar.trim().equals(user.getAadhaar())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Aadhaar number already exists."));
        }

        try {
            user.setAadhaar(newAadhaar.trim());
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Aadhaar updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update Aadhaar: " + e.getMessage()));
        }
    }

    @PutMapping("/users/update-address1")
    public ResponseEntity<?> adminUpdateAddress1(
            @RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String newAddress1 = payload.get("newAddress1");

        if (accountNumber == null || accountNumber.trim().isEmpty() ||
            newAddress1 == null || newAddress1.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number and new Address 1 are required."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        try {
            user.setAddressLine1(newAddress1.trim());
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Address 1 updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update Address 1: " + e.getMessage()));
        }
    }

    @PutMapping("/users/update-address2")
    public ResponseEntity<?> adminUpdateAddress2(
            @RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String newAddress2 = payload.get("newAddress2");

        if (accountNumber == null || accountNumber.trim().isEmpty() ||
            newAddress2 == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number and new Address 2 are required."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        try {
            user.setAddressLine2(newAddress2.trim());
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Address 2 updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update Address 2: " + e.getMessage()));
        }
    }

    @PutMapping("/users/update-mobile")
    public ResponseEntity<?> adminUpdateMobile(
            @RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String newMobile = payload.get("newMobile");

        if (accountNumber == null || accountNumber.trim().isEmpty() ||
            newMobile == null || newMobile.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number and new mobile number are required."));
        }

        if (!newMobile.matches("^[6-9][0-9]{9}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Enter a valid 10-digit mobile number."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        if (userRepository.existsByMobile(newMobile.trim()) && !newMobile.trim().equals(user.getMobile())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mobile number already exists."));
        }

        try {
            user.setMobile(newMobile.trim());
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Mobile number updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update mobile number: " + e.getMessage()));
        }
    }

    @PutMapping("/users/update-email")
    public ResponseEntity<?> adminUpdateEmail(
            @RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String newEmail = payload.get("newEmail");

        if (accountNumber == null || accountNumber.trim().isEmpty() ||
            newEmail == null || newEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number and new email are required."));
        }

        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Enter a valid email address."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        if (userRepository.existsByEmail(newEmail.trim().toLowerCase()) && !newEmail.trim().equalsIgnoreCase(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email address already exists."));
        }

        try {
            user.setEmail(newEmail.trim().toLowerCase());
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Email updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update email: " + e.getMessage()));
        }
    }

    @PutMapping("/users/update-nominee")
    public ResponseEntity<?> adminUpdateNominee(
            @RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String newNominee = payload.get("newNominee");

        if (accountNumber == null || accountNumber.trim().isEmpty() ||
            newNominee == null || newNominee.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number and new nominee name are required."));
        }

        String nomineeName = newNominee.trim().replaceAll("\\s+", " ").toUpperCase();
        if (!nomineeName.matches("^[A-Z]+( [A-Z]+)+$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please enter a valid nominee full name."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        try {
            user.setNomineeName(nomineeName);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Nominee updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update nominee: " + e.getMessage()));
        }
    }


    @GetMapping("/loans/{userId}")
    public List<Loan> getUserLoans(
            @PathVariable Long userId) {

        return loanService
                .getLoansByUser(userId);
    }

    @GetMapping("/loans")
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @PutMapping(
            "/loan/{loanId}/approve")
    public Loan approveLoan(
            @PathVariable Long loanId) {

        return loanService
                .updateLoanStatus(
                        loanId,
                        LoanStatus.APPROVED);
    }

    @PutMapping(
            "/loan/{loanId}/reject")
    public Loan rejectLoan(
            @PathVariable Long loanId) {

        return loanService
                .updateLoanStatus(
                        loanId,
                        LoanStatus.REJECTED);
    }

    @DeleteMapping("/users/{accountNumber}")
    public ResponseEntity<?> adminDeleteUser(
            @PathVariable String accountNumber) {

        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number is required."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        try {
            userRepository.delete(user);
            return ResponseEntity.ok(Map.of("message", "User permanently deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to permanently delete user: " + e.getMessage()));
        }
    }
}