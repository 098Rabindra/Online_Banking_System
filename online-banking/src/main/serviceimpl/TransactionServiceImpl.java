package com.bank.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.dto.TransactionDTO;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.enums.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.service.TransactionService;

@Service
public class TransactionServiceImpl
        implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction transfer(
            TransactionDTO dto) {

        Account sender =
                accountRepository
                .findByAccountNumber(
                        dto.getFromAccount())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Sender account not found"));

        Account receiver =
                accountRepository
                .findByAccountNumber(
                        dto.getToAccount())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Receiver account not found"));

        sender.setBalance(
                sender.getBalance()
                        .subtract(dto.getAmount()));

        receiver.setBalance(
                receiver.getBalance()
                        .add(dto.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        Transaction transaction =
                new Transaction();

        transaction.setFromAccount(
                dto.getFromAccount());

        transaction.setToAccount(
                dto.getToAccount());

        transaction.setAmount(
                dto.getAmount());

        transaction.setTransactionType(
                TransactionType.TRANSFER);

        transaction.setTransactionDate(
                LocalDateTime.now());

        return transactionRepository
                .save(transaction);
    }

    @Override
    public List<Transaction>
    getAllTransactions() {

        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction>
    getTransactionsByAccount(
            String accountNumber) {

        return transactionRepository
                .findByFromAccountOrToAccount(
                        accountNumber,
                        accountNumber);
    }
}