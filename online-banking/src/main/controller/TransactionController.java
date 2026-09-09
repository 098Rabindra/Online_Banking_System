package com.bank.controller;


import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bank.dto.TransactionDTO;
import com.bank.entity.Transaction;
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public Transaction transfer(
            @RequestBody TransactionDTO dto) {

        return transactionService.transfer(dto);
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {

        return transactionService.getAllTransactions();
    }

    @GetMapping("/{accountNumber}")
    public List<Transaction> getTransactions(
            @PathVariable String accountNumber) {

        return transactionService
                .getTransactionsByAccount(accountNumber);
    }
}
