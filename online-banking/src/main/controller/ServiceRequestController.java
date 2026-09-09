package com.bank.controller;

import com.bank.entity.ChequeBookRequest;
import com.bank.entity.PassbookRequest;
import com.bank.entity.StopChequeRequest;
import com.bank.entity.User;
import com.bank.enums.RequestStatus;
import com.bank.repository.ChequeBookRequestRepository;
import com.bank.repository.PassbookRequestRepository;
import com.bank.repository.StopChequeRequestRepository;
import com.bank.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ServiceRequestController {

    private static final Logger log = LoggerFactory.getLogger(ServiceRequestController.class);

    private final ChequeBookRequestRepository chequeBookRequestRepository;
    private final StopChequeRequestRepository stopChequeRequestRepository;
    private final PassbookRequestRepository passbookRequestRepository;
    private final UserRepository userRepository;

    public ServiceRequestController(
            ChequeBookRequestRepository chequeBookRequestRepository,
            StopChequeRequestRepository stopChequeRequestRepository,
            PassbookRequestRepository passbookRequestRepository,
            UserRepository userRepository) {
        this.chequeBookRequestRepository = chequeBookRequestRepository;
        this.stopChequeRequestRepository = stopChequeRequestRepository;
        this.passbookRequestRepository = passbookRequestRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // USER ENDPOINTS (requires authenticated user)
    // ==========================================

    @PostMapping("/service-requests/cheque-book")
    public ResponseEntity<?> requestChequeBook(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String leaves = payload.get("leaves");
        if (leaves == null || leaves.trim().isEmpty() || leaves.equals("Select Leaves")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Please select valid leaves number"));
        }

        List<ChequeBookRequest> requests = chequeBookRequestRepository.findByUserEmail(email);
        LocalDateTime now = LocalDateTime.now();
        for (ChequeBookRequest req : requests) {
            if (req.getCreatedAt() != null && req.getCreatedAt().isAfter(now.minusHours(24))) {
                return ResponseEntity.badRequest().body(Map.of("message", "You can only request one cheque book per 24 hours. Please try again tomorrow."));
            }
        }

        ChequeBookRequest request = new ChequeBookRequest();
        request.setAccountNumber(user.getAccountNumber());
        request.setLeaves(leaves);
        request.setStatus(RequestStatus.PENDING);
        request.setUser(user);
        request.setCreatedAt(LocalDateTime.now());

        chequeBookRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/service-requests/cheque-book")
    public ResponseEntity<List<ChequeBookRequest>> getMyChequeBookRequests(
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        return ResponseEntity.ok(chequeBookRequestRepository.findByUserEmail(email));
    }

    @PostMapping("/service-requests/stop-cheque")
    public ResponseEntity<?> requestStopCheque(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String chequeNumber = payload.get("chequeNumber");
        if (chequeNumber == null || chequeNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cheque number is required"));
        }

        if (!chequeNumber.matches("^[0-9]{4}\\s?[a-zA-Z][0-9][a-zA-Z][0-9]\\s?[0-9]{4}\\s?[0-9]{4}$")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid cheque format (e.g. 4561 r1r1 2563 1252)"));
        }

        StopChequeRequest request = new StopChequeRequest();
        request.setAccountNumber(user.getAccountNumber());
        request.setChequeNumber(chequeNumber);
        request.setStatus(RequestStatus.PENDING);
        request.setUser(user);
        request.setCreatedAt(LocalDateTime.now());

        stopChequeRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/service-requests/stop-cheque")
    public ResponseEntity<List<StopChequeRequest>> getMyStopChequeRequests(
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        return ResponseEntity.ok(stopChequeRequestRepository.findByUserEmail(email));
    }

    @PostMapping("/service-requests/passbook")
    public ResponseEntity<?> requestPassbook(
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PassbookRequest> requests = passbookRequestRepository.findByUserEmail(email);
        LocalDateTime now = LocalDateTime.now();
        for (PassbookRequest req : requests) {
            if (req.getCreatedAt() != null && req.getCreatedAt().isAfter(now.minusHours(24))) {
                return ResponseEntity.badRequest().body(Map.of("message", "You can only request one passbook per 24 hours. Please try again tomorrow."));
            }
        }

        PassbookRequest request = new PassbookRequest();
        request.setAccountNumber(user.getAccountNumber());
        request.setStatus(RequestStatus.PENDING);
        request.setUser(user);
        request.setCreatedAt(LocalDateTime.now());

        passbookRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/service-requests/passbook")
    public ResponseEntity<List<PassbookRequest>> getMyPassbookRequests(
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        return ResponseEntity.ok(passbookRequestRepository.findByUserEmail(email));
    }

    // ==========================================
    // ADMIN ENDPOINTS (requires ROLE_ADMIN, mapped under /admin)
    // ==========================================

    @PostMapping("/admin/service-requests/cheque-book")
    public ResponseEntity<?> createChequeBookRequestAdmin(
            @RequestBody Map<String, String> payload) {
        try {
            String accountNumber = payload.get("accountNumber");
            String leaves = payload.get("leaves");
            
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Account number is required"));
            }
            if (leaves == null || leaves.trim().isEmpty() || leaves.equals("Select Leaves")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Please select valid leaves number"));
            }
            
            User user = userRepository.findByAccountNumber(accountNumber.trim())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ChequeBookRequest request = new ChequeBookRequest();
            request.setAccountNumber(user.getAccountNumber());
            request.setLeaves(leaves);
            request.setStatus(RequestStatus.PENDING);
            request.setUser(user);
            request.setCreatedAt(LocalDateTime.now());

            chequeBookRequestRepository.save(request);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            log.error("Error creating cheque book request", e);
            return ResponseEntity.status(500).body(Map.of("message", "Server Error: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/service-requests/cheque-book")
    public ResponseEntity<List<ChequeBookRequest>> getAllChequeBookRequests() {
        return ResponseEntity.ok(chequeBookRequestRepository.findAll());
    }

    @PutMapping("/admin/service-requests/cheque-book/{id}/{status}")
    public ResponseEntity<?> updateChequeBookStatus(
            @PathVariable @NonNull Long id,
            @PathVariable RequestStatus status) {
        ChequeBookRequest request = chequeBookRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        chequeBookRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/admin/service-requests/stop-cheque")
    public ResponseEntity<?> createStopChequeRequestAdmin(
            @RequestBody Map<String, String> payload) {
        try {
            String accountNumber = payload.get("accountNumber");
            String chequeNumber = payload.get("chequeNumber");
            
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Account number is required"));
            }
            if (chequeNumber == null || chequeNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Cheque number is required"));
            }
            
            if (!chequeNumber.matches("^[0-9]{4}\\s?[a-zA-Z][0-9][a-zA-Z][0-9]\\s?[0-9]{4}\\s?[0-9]{4}$")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid cheque format (e.g. 4561 r1r1 2563 1252)"));
            }
            
            User user = userRepository.findByAccountNumber(accountNumber.trim())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            StopChequeRequest request = new StopChequeRequest();
            request.setAccountNumber(user.getAccountNumber());
            request.setChequeNumber(chequeNumber);
            request.setStatus(RequestStatus.PENDING);
            request.setUser(user);
            request.setCreatedAt(LocalDateTime.now());

            stopChequeRequestRepository.save(request);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            log.error("Error creating stop cheque request in admin", e);
            return ResponseEntity.status(500).body(Map.of("message", "Server Error: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/service-requests/stop-cheque")
    public ResponseEntity<List<StopChequeRequest>> getAllStopChequeRequests() {
        return ResponseEntity.ok(stopChequeRequestRepository.findAll());
    }

    @PutMapping("/admin/service-requests/stop-cheque/{id}/{status}")
    public ResponseEntity<?> updateStopChequeStatus(
            @PathVariable @NonNull Long id,
            @PathVariable RequestStatus status) {
        StopChequeRequest request = stopChequeRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        stopChequeRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }


    @PostMapping("/admin/service-requests/passbook")
    public ResponseEntity<?> createPassbookRequestAdmin(
            @RequestBody Map<String, String> payload) {
        try {
            String accountNumber = payload.get("accountNumber");
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Account number is required"));
            }
            User user = userRepository.findByAccountNumber(accountNumber.trim())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            PassbookRequest request = new PassbookRequest();
            request.setAccountNumber(user.getAccountNumber());
            request.setStatus(RequestStatus.PENDING);
            request.setUser(user);
            request.setCreatedAt(LocalDateTime.now());

            passbookRequestRepository.save(request);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            log.error("Error creating passbook request in admin", e);
            return ResponseEntity.status(500).body(Map.of("message", "Server Error: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/service-requests/passbook")
    public ResponseEntity<List<PassbookRequest>> getAllPassbookRequests() {
        return ResponseEntity.ok(passbookRequestRepository.findAll());
    }

    @PutMapping("/admin/service-requests/passbook/{id}/{status}")
    public ResponseEntity<?> updatePassbookStatus(
            @PathVariable @NonNull Long id,
            @PathVariable RequestStatus status) {
        PassbookRequest request = passbookRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        passbookRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }
}
