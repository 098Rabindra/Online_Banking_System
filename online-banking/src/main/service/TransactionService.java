package com.bank.service;

import java.util.List;

import com.bank.dto.TransactionDTO;
import com.bank.entity.Transaction;

public interface TransactionService {

    Transaction transfer(TransactionDTO transactionDTO);

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByAccount(String accountNumber);
}