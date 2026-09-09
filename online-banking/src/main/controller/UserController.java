package com.bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.bank.dto.AadhaarRequest;
import com.bank.entity.Role;
import com.bank.entity.User;
import com.bank.repository.UserRepository;
import com.bank.dto.AddressRequest;
import com.bank.dto.MobileRequest;
import com.bank.dto.EmailRequest;
import com.bank.dto.NomineeRequest;
import com.bank.dto.PanRequest;
import com.bank.dto.PasswordChangeRequest;


@RestController
@RequestMapping("/users")
@SuppressWarnings("null")
public class UserController {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;


	public UserController(
	        UserRepository repository,
	        PasswordEncoder passwordEncoder) {

	    this.repository = repository;
	    this.passwordEncoder = passwordEncoder;
	}
	
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(repository.findByRole(Role.ROLE_USER));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("/account-summary/{email}")
    public ResponseEntity<User> getAccountSummary(
            @PathVariable String email) {

        User user = repository.findByEmail(
                email.trim().toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    @GetMapping("/by-account/{accountNumber}")
    public ResponseEntity<User> getUserByAccountNumber(
            @PathVariable String accountNumber) {

        User user = repository.findByAccountNumber(accountNumber.trim())
                .orElseThrow(() ->
                        new RuntimeException("User not found for account: " + accountNumber));

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update-aadhaar")
    public ResponseEntity<?> updateAadhaar(
            @RequestBody AadhaarRequest request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "User not authenticated"));
        }

        String email = authentication.getName();

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setAadhaar(request.getAadhaarNumber());

        repository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Aadhaar updated successfully"));
    }
    
    @PutMapping("/update-address1")
    public ResponseEntity<?> updateAddress1(
            @RequestBody AddressRequest request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "User not authenticated"));
        }

        String email = authentication.getName();

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setAddressLine1(request.getAddress());

        repository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Address Line 1 updated successfully"));
    }
    @PutMapping("/update-address2")
    public ResponseEntity<?> updateAddress2(
            @RequestBody AddressRequest request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "User not authenticated"));
        }

        String email = authentication.getName();

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setAddressLine2(request.getAddress());

        repository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Address Line 2 updated successfully"));
    }
    @PutMapping("/update-mobile")
    public ResponseEntity<?> updateMobile(
            @RequestBody MobileRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String mobile = request.getMobile();

        // Mobile validation
        if (mobile == null || !mobile.matches("^[6-9][0-9]{9}$")) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Enter a valid 10-digit mobile number"));
        }

        // Duplicate check
        if (repository.existsByMobile(mobile)
                && !mobile.equals(user.getMobile())) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Mobile number already exists"));
        }

        user.setMobile(mobile);

        repository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Mobile number updated successfully"));
    }
    
    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(
            @RequestBody EmailRequest request,
            Authentication authentication) {

        if (authentication == null) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "User not authenticated"));
        }

        String currentEmail = authentication.getName();

        User user = repository.findByEmail(currentEmail)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String newEmail = request.getEmail();

        // Email validation
        if (newEmail == null ||
                !newEmail.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Enter a valid email address"));
        }

        // Same email check
        if (newEmail.equalsIgnoreCase(user.getEmail())) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Please enter a different email address"));
        }

        // Duplicate email check
        if (repository.existsByEmail(newEmail)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email address already exists"));
        }

        user.setEmail(newEmail.toLowerCase());

        repository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Email updated successfully"));
    }
    
    @PutMapping("/update-nominee")
    public ResponseEntity<?> updateNominee(
            @RequestBody NomineeRequest request,
            Authentication authentication) {

        if (authentication == null) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "User not authenticated"));
        }

        String email = authentication.getName();

        User user = repository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String nomineeName =
                request.getNomineeName()
                       .trim()
                       .replaceAll("\\s+", " ")
                       .toUpperCase();

        if (!nomineeName.matches("^[A-Z]+( [A-Z]+)+$")) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Please enter a valid full name"));
        }

        user.setNomineeName(nomineeName);

        repository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Nominee updated successfully"));
    }
    @PutMapping("/update-pan")
    public ResponseEntity<?> updatePan(
            @RequestBody PanRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        String pan = request.getPan()
                .trim()
                .toUpperCase();

        // PAN Validation
        if (!pan.matches("^[A-Z]{5}[0-9]{4}[A-Z]{1}$")) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Enter valid PAN number"));
        }

        User user = repository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));

        // Check duplicate PAN
        if (!user.getPan().equalsIgnoreCase(pan)
                && repository.existsByPan(pan)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "PAN number already exists"));
        }

        user.setPan(pan);

        repository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "PAN updated successfully"));
    }
    
    
    @PutMapping("/change-login-password")
    public ResponseEntity<?> changeLoginPassword(
            @RequestBody PasswordChangeRequest request,
            Authentication authentication) {


        if(authentication == null){

            return ResponseEntity.status(401)
                    .body(Map.of(
                        "message",
                        "User not authenticated"));
        }


        String email = authentication.getName();


        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                    new RuntimeException(
                        "User not found"));



        // old password check

        if(!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {


            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "message",
                        "Current password incorrect"));
        }



        // confirm password

        if(!request.getNewPassword()
                .equals(request.getConfirmPassword())) {


            return ResponseEntity.badRequest()
                    .body(Map.of(
                        "message",
                        "New password and confirm password not match"));
        }



        // encode new password

        user.setPassword(
            passwordEncoder.encode(
                request.getNewPassword()
            )
        );


        repository.save(user);



        return ResponseEntity.ok(
            Map.of(
                "message",
                "Login password changed successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Duplicate checks
        String newEmail = updatedUser.getEmail().trim().toLowerCase();
        if (!user.getEmail().equalsIgnoreCase(newEmail) 
                && repository.existsByEmail(newEmail)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
        }
        
        String newMobile = updatedUser.getMobile().trim();
        if (!user.getMobile().equals(newMobile) 
                && repository.existsByMobile(newMobile)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mobile number already exists"));
        }
        
        String newAadhaar = updatedUser.getAadhaar().trim();
        if (!user.getAadhaar().equals(newAadhaar) 
                && repository.existsByAadhaar(newAadhaar)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Aadhaar number already exists"));
        }
        
        String newPan = updatedUser.getPan().trim().toUpperCase();
        if (!user.getPan().equalsIgnoreCase(newPan) 
                && repository.existsByPan(newPan)) {
            return ResponseEntity.badRequest().body(Map.of("message", "PAN number already exists"));
        }

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setFatherName(updatedUser.getFatherName());
        user.setDob(updatedUser.getDob());
        user.setEmail(newEmail);
        user.setMobile(newMobile);
        user.setAddressLine1(updatedUser.getAddressLine1());
        user.setAddressLine2(updatedUser.getAddressLine2());
        user.setCountry(updatedUser.getCountry());
        user.setState(updatedUser.getState());
        user.setCity(updatedUser.getCity());
        user.setPincode(updatedUser.getPincode());
        user.setAadhaar(newAadhaar);
        user.setPan(newPan);
        user.setAccountType(updatedUser.getAccountType());
        user.setNomineeName(updatedUser.getNomineeName());

        return ResponseEntity.ok(repository.save(user));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getAccountDetailsPdf(
            @PathVariable Long id) {

        User user = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        byte[] pdfBytes = com.bank.util.PdfGeneratorUtil.generateAccountDetailsPdf(user);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Account_Summary_" + user.getAccountNumber() + ".pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
    }
}