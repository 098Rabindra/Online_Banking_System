package com.bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import com.bank.service.EmailService;
import com.bank.service.PdfService;
import com.bank.util.PdfGeneratorUtil;

@RestController
@RequestMapping("/statement")
@SuppressWarnings("null")
public class StatementController {

    private final PdfService pdfService;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    public StatementController(
            PdfService pdfService,
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            EmailService emailService) {

        this.pdfService = pdfService;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<byte[]> downloadStatement(
            @PathVariable String accountNumber) {

        byte[] pdf =
                pdfService.generateStatement(
                        accountNumber);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=statement.pdf")
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/send-email")
    public ResponseEntity<?> sendStatementEmail(
            @RequestParam String email,
            @RequestParam(required = false) String duration,
            @RequestParam(required = false) String financialYear,
            @RequestParam(required = false) String format,
            @RequestParam(defaultValue = "false") boolean includeSummary,
            @RequestParam(defaultValue = "false") boolean includeNominee) {

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(
                user.getAccountNumber(), user.getAccountNumber());

        // Generate statement PDF encrypted with user details password
        byte[] pdfBytes = PdfGeneratorUtil.generateStatement(
                user.getAccountNumber(), transactions, user);

        String subject = "Your Password-Protected Account Statement";
        String messageText = "Dear " + user.getFirstName() + " " + user.getLastName() + ",\n\n"
                + "Please find attached your password-protected account statement.\n\n"
                + "The PDF is secured. Your password is the first 4 letters of your First Name in UPPERCASE followed by the DDMM of your Date of Birth.\n"
                + "Example: If first name is RABINDRA and Date of Birth is 31-08-2001, the password is RABI3108.\n\n"
                + "Thank you for banking with KUUMCA Bank of India.\n\n"
                + "Best regards,\n"
                + "KUUMCA Bank of India Support Team";

        emailService.sendEmailWithAttachment(
                user.getEmail(),
                subject,
                messageText,
                pdfBytes,
                "statement.pdf"
        );

        return ResponseEntity.ok(Map.of("message", "Statement sent successfully to your registered email. Please check your Gmail."));
    }
}
