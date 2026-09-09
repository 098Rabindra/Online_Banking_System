package com.bank.controller;


import org.springframework.web.bind.annotation.*;

import com.bank.dto.EmailDTO;
import com.bank.service.EmailService;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(
            EmailService emailService) {

        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendEmail(
            @RequestBody EmailDTO dto) {

        emailService.sendEmail(dto);

        return "Email Sent Successfully";
    }
}
