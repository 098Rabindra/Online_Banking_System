package com.bank.controller;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bank.dto.AccountDTO;
import com.bank.entity.Account;
import com.bank.service.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(
            AccountService accountService) {

        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(
            @RequestBody AccountDTO dto) {

        return accountService.createAccount(dto);
    }

    @GetMapping("/{accountNumber}")
    public Account getAccount(
            @PathVariable String accountNumber) {

        return accountService.getAccountByNumber(accountNumber);
    }

    @GetMapping("/user/{userId}")
    public List<Account> getUserAccounts(
            @PathVariable Long userId) {

        return accountService.getAccountsByUser(userId);
    }

    @GetMapping("/balance/{accountNumber}")
    public BigDecimal getBalance(
            @PathVariable String accountNumber) {

        return accountService.getBalance(accountNumber);
    }
}