package com.bank.controller;

import com.bank.entity.CardRequest;
import com.bank.entity.User;
import com.bank.enums.RequestStatus;
import com.bank.repository.CardRequestRepository;
import com.bank.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@CrossOrigin(origins = "*")
public class CardRequestController {

    private final CardRequestRepository cardRequestRepository;
    private final UserRepository userRepository;

    public CardRequestController(CardRequestRepository cardRequestRepository, UserRepository userRepository) {
        this.cardRequestRepository = cardRequestRepository;
        this.userRepository = userRepository;
    }

    // USER ENDPOINTS

    @PostMapping("/card-requests")
    public ResponseEntity<?> requestCard(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accountNumber = payload.get("accountNumber");
        String fullName = payload.get("fullName");
        String mobileNumber = payload.get("mobileNumber");
        String address = payload.get("address");
        String cardType = payload.get("cardType");

        if (accountNumber == null || accountNumber.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty() ||
            mobileNumber == null || mobileNumber.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            cardType == null || cardType.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required."));
        }

        CardRequest request = new CardRequest();
        request.setAccountNumber(accountNumber.trim());
        request.setFullName(fullName.trim());
        request.setMobileNumber(mobileNumber.trim());
        request.setAddress(address.trim());
        request.setCardType(cardType.trim());
        request.setStatus(RequestStatus.PENDING);
        request.setUser(user);
        request.setCreatedAt(LocalDateTime.now());

        cardRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/card-requests/my")
    public ResponseEntity<List<CardRequest>> getMyCardRequests(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        return ResponseEntity.ok(cardRequestRepository.findByUserEmail(email));
    }

    // ADMIN ENDPOINTS

    @PostMapping("/admin/card-requests")
    public ResponseEntity<?> createCardRequestAdmin(
            @RequestBody Map<String, String> payload) {
        try {
            String accountNumber = payload.get("accountNumber");
            String fullName = payload.get("fullName");
            String mobileNumber = payload.get("mobileNumber");
            String address = payload.get("address");
            String cardType = payload.get("cardType");

            if (accountNumber == null || accountNumber.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                mobileNumber == null || mobileNumber.trim().isEmpty() ||
                address == null || address.trim().isEmpty() ||
                cardType == null || cardType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "All fields are required."));
            }

            User user = userRepository.findByAccountNumber(accountNumber.trim())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CardRequest request = new CardRequest();
            request.setAccountNumber(user.getAccountNumber());
            request.setFullName(fullName.trim());
            request.setMobileNumber(mobileNumber.trim());
            request.setAddress(address.trim());
            request.setCardType(cardType.trim());
            request.setStatus(RequestStatus.PENDING);
            request.setUser(user);
            request.setCreatedAt(LocalDateTime.now());

            cardRequestRepository.save(request);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Server Error: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/card-requests")
    public ResponseEntity<List<CardRequest>> getAllCardRequests() {
        return ResponseEntity.ok(cardRequestRepository.findAll());
    }

    @PutMapping("/admin/card-requests/{id}/{status}")
    public ResponseEntity<?> updateCardRequestStatus(
            @PathVariable @NonNull Long id,
            @PathVariable RequestStatus status) {
        CardRequest request = cardRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card request not found"));

        request.setStatus(status);
        if (status == RequestStatus.APPROVED) {
            // Generate a random card number starting with 5239
            request.setCardNumber(generateRandomCardNumber());
            if (request.getDailyLimit() == null) {
                request.setDailyLimit(50000.0);
            }
            if (request.getAtmLimit() == null) {
                request.setAtmLimit(20000.0);
            }
        }
        cardRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/card-requests/limits/{cardType}")
    public ResponseEntity<?> getCardLimits(
            @PathVariable String cardType,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        CardRequest card = cardRequestRepository.findByUserEmail(email)
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType) && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No active " + cardType + " found."));
        }

        if (card.getDailyLimit() == null) card.setDailyLimit(50000.0);
        if (card.getAtmLimit() == null) card.setAtmLimit(20000.0);
        
        return ResponseEntity.ok(Map.of(
            "accountNumber", card.getAccountNumber(),
            "dailyLimit", card.getDailyLimit(),
            "atmLimit", card.getAtmLimit()
        ));
    }

    @PutMapping("/card-requests/limits/{cardType}")
    public ResponseEntity<?> updateCardLimits(
            @PathVariable String cardType,
            @RequestBody Map<String, Object> payload,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        
        Object dailyObj = payload.get("dailyLimit");
        Object atmObj = payload.get("atmLimit");
        
        if (dailyObj == null || atmObj == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Daily limit and ATM limit are required."));
        }
        
        Double dailyLimit = Double.valueOf(dailyObj.toString());
        Double atmLimit = Double.valueOf(atmObj.toString());

        CardRequest card = cardRequestRepository.findByUserEmail(email)
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType) && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No active " + cardType + " found."));
        }

        card.setDailyLimit(dailyLimit);
        card.setAtmLimit(atmLimit);
        cardRequestRepository.save(card);

        return ResponseEntity.ok(Map.of(
            "message", "Card limits updated successfully.",
            "dailyLimit", card.getDailyLimit(),
            "atmLimit", card.getAtmLimit()
        ));
    }

    @GetMapping("/admin/card-requests/limits")
    public ResponseEntity<?> getAdminCardLimits(
            @RequestParam String accountNumber,
            @RequestParam String cardType) {
        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        CardRequest card = cardRequestRepository.findByUserId(user.getId())
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType.trim()) && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No approved " + cardType + " found for this account."));
        }

        if (card.getDailyLimit() == null) card.setDailyLimit(50000.0);
        if (card.getAtmLimit() == null) card.setAtmLimit(20000.0);

        return ResponseEntity.ok(Map.of(
            "accountNumber", card.getAccountNumber(),
            "fullName", card.getFullName(),
            "dailyLimit", card.getDailyLimit(),
            "atmLimit", card.getAtmLimit()
        ));
    }

    @PutMapping("/admin/card-requests/limits")
    public ResponseEntity<?> updateAdminCardLimits(
            @RequestBody Map<String, Object> payload) {
        Object accObj = payload.get("accountNumber");
        Object cardTypeObj = payload.get("cardType");
        Object dailyObj = payload.get("dailyLimit");
        Object atmObj = payload.get("atmLimit");

        if (accObj == null || cardTypeObj == null || dailyObj == null || atmObj == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Account number, card type, daily limit, and ATM limit are required."));
        }

        String accountNumber = accObj.toString().trim();
        String cardType = cardTypeObj.toString().trim();
        Double dailyLimit = Double.valueOf(dailyObj.toString());
        Double atmLimit = Double.valueOf(atmObj.toString());

        User user = userRepository.findByAccountNumber(accountNumber)
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        CardRequest card = cardRequestRepository.findByUserId(user.getId())
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType) && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No approved " + cardType + " found for this account."));
        }

        card.setDailyLimit(dailyLimit);
        card.setAtmLimit(atmLimit);
        cardRequestRepository.save(card);

        return ResponseEntity.ok(Map.of(
            "message", "Card limits updated successfully.",
            "dailyLimit", card.getDailyLimit(),
            "atmLimit", card.getAtmLimit()
        ));
    }

    @GetMapping("/admin/card-requests/card-details")
    public ResponseEntity<?> getAdminCardDetails(
            @RequestParam String accountNumber,
            @RequestParam String cardType) {
        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        CardRequest card = cardRequestRepository.findByUserId(user.getId())
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType.trim()) && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No approved " + cardType + " found for this account."));
        }

        return ResponseEntity.ok(Map.of(
            "accountNumber", card.getAccountNumber(),
            "fullName", card.getFullName(),
            "cardNumber", card.getCardNumber() != null ? card.getCardNumber() : "N/A",
            "isBlocked", card.getIsBlocked() != null ? card.getIsBlocked() : false
        ));
    }

    @PutMapping("/admin/card-requests/block-unblock")
    public ResponseEntity<?> adminBlockUnblockCard(
            @RequestBody Map<String, String> payload) {
        String cardType = payload.get("cardType");
        String accountNumber = payload.get("accountNumber");
        String cardNumber = payload.get("cardNumber");
        String action = payload.get("action"); // "Block" or "Unblock"

        if (cardType == null || cardType.trim().isEmpty() ||
            accountNumber == null || accountNumber.trim().isEmpty() ||
            cardNumber == null || cardNumber.trim().isEmpty() ||
            action == null || action.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        CardRequest card = cardRequestRepository.findByUserId(user.getId())
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType.trim()) 
                          && r.getCardNumber().equals(cardNumber.trim())
                          && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No active card matching the details was found."));
        }

        boolean blockValue = action.equalsIgnoreCase("Block");
        card.setIsBlocked(blockValue);
        cardRequestRepository.save(card);

        String msg = blockValue ? "Card blocked successfully." : "Card unblocked successfully.";
        return ResponseEntity.ok(Map.of(
            "message", msg,
            "cardType", card.getCardType(),
            "cardNumber", card.getCardNumber(),
            "isBlocked", card.getIsBlocked()
        ));
    }

    @PutMapping("/admin/card-requests/generate-pin")
    public ResponseEntity<?> adminGenerateCardPin(
            @RequestBody Map<String, String> payload) {
        String cardType = payload.get("cardType");
        String accountNumber = payload.get("accountNumber");
        String cardNumber = payload.get("cardNumber");
        String newPin = payload.get("newPin");
        String confirmPin = payload.get("confirmPin");

        if (cardType == null || cardType.trim().isEmpty() ||
            accountNumber == null || accountNumber.trim().isEmpty() ||
            cardNumber == null || cardNumber.trim().isEmpty() ||
            newPin == null || newPin.trim().isEmpty() ||
            confirmPin == null || confirmPin.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required."));
        }

        if (!newPin.equals(confirmPin)) {
            return ResponseEntity.badRequest().body(Map.of("message", "PIN and confirm PIN do not match."));
        }

        if (!newPin.matches("\\d{4,6}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "PIN must be between 4 and 6 digits."));
        }

        User user = userRepository.findByAccountNumber(accountNumber.trim())
                .orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No user found for account: " + accountNumber));
        }

        CardRequest card = cardRequestRepository.findByUserId(user.getId())
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType.trim()) 
                          && r.getCardNumber().equals(cardNumber.trim()) 
                          && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No active card matching the details was found."));
        }

        card.setPin(newPin.trim());
        cardRequestRepository.save(card);

        return ResponseEntity.ok(Map.of("message", "PIN generated successfully."));
    }

    @PutMapping("/card-requests/generate-pin")
    public ResponseEntity<?> generateCardPin(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String email = authentication.getName();
        
        String cardType = payload.get("cardType");
        String accountNumber = payload.get("accountNumber");
        String newPin = payload.get("newPin");
        String confirmPin = payload.get("confirmPin");

        if (cardType == null || cardType.trim().isEmpty() ||
            accountNumber == null || accountNumber.trim().isEmpty() ||
            newPin == null || newPin.trim().isEmpty() ||
            confirmPin == null || confirmPin.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required."));
        }

        if (!newPin.equals(confirmPin)) {
            return ResponseEntity.badRequest().body(Map.of("message", "PIN and confirm PIN do not match."));
        }

        if (!newPin.matches("\\d{4,6}")) {
            return ResponseEntity.badRequest().body(Map.of("message", "PIN must be between 4 and 6 digits."));
        }

        CardRequest card = cardRequestRepository.findByUserEmail(email)
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType.trim()) 
                          && r.getAccountNumber().equals(accountNumber.trim()) 
                          && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No active card matching the details was found."));
        }

        card.setPin(newPin.trim());
        cardRequestRepository.save(card);

        return ResponseEntity.ok(Map.of("message", "PIN generated successfully."));
    }

    @PutMapping("/card-requests/block-unblock")
    public ResponseEntity<?> blockUnblockCard(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }
        String email = authentication.getName();
        
        String cardType = payload.get("cardType");
        String accountNumber = payload.get("accountNumber");
        String cardNumber = payload.get("cardNumber");
        String action = payload.get("action"); // "Block" or "Unblock"

        if (cardType == null || cardType.trim().isEmpty() ||
            accountNumber == null || accountNumber.trim().isEmpty() ||
            cardNumber == null || cardNumber.trim().isEmpty() ||
            action == null || action.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required."));
        }

        CardRequest card = cardRequestRepository.findByUserEmail(email)
                .stream()
                .filter(r -> r.getCardType().equalsIgnoreCase(cardType.trim()) 
                          && r.getAccountNumber().equals(accountNumber.trim()) 
                          && r.getCardNumber().equals(cardNumber.trim())
                          && r.getStatus() == RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);

        if (card == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No active card matching the details was found."));
        }

        boolean blockValue = action.equalsIgnoreCase("Block");
        card.setIsBlocked(blockValue);
        cardRequestRepository.save(card);

        String msg = blockValue ? "Card blocked successfully." : "Card unblocked successfully.";
        return ResponseEntity.ok(Map.of(
            "message", msg,
            "cardType", card.getCardType(),
            "cardNumber", card.getCardNumber(),
            "isBlocked", card.getIsBlocked()
        ));
    }

    private String generateRandomCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("5239");
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        String raw = sb.toString();
        return raw.substring(0, 4) + " " + raw.substring(4, 8) + " " + raw.substring(8, 12) + " " + raw.substring(12, 16);
    }
}
