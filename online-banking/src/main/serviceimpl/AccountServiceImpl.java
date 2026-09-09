package com.bank.serviceimpl;


import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.dto.AccountDTO;
import com.bank.entity.Account;
import com.bank.entity.User;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.UserRepository;
import com.bank.service.AccountService;
import com.bank.util.AccountNumberGenerator;

@Service
@SuppressWarnings("null")
public class AccountServiceImpl
        implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountServiceImpl(
            AccountRepository accountRepository,
            UserRepository userRepository) {

        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Account createAccount(
            AccountDTO dto) {

        User user =
                userRepository.findById(
                        dto.getUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"));

        Account account = new Account();

        account.setAccountNumber(
                AccountNumberGenerator
                        .generateAccountNumber());

        account.setAccountType(
                dto.getAccountType());

        account.setBalance(
                dto.getBalance());

        account.setUser(user);

        return accountRepository.save(account);
    }

    @Override
    public Account getAccountByNumber(
            String accountNumber) {

        return accountRepository
                .findByAccountNumber(
                        accountNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found"));
    }

    @Override
    public List<Account> getAccountsByUser(
            Long userId) {

        return accountRepository
                .findByUserId(userId);
    }

    @Override
    public BigDecimal getBalance(
            String accountNumber) {

        return getAccountByNumber(
                accountNumber)
                .getBalance();
    }

    @Override
    public Account deposit(
            String accountNumber,
            BigDecimal amount) {

        Account account =
                getAccountByNumber(
                        accountNumber);

        account.setBalance(
                account.getBalance()
                        .add(amount));

        return accountRepository
                .save(account);
    }

    @Override
    public Account withdraw(
            String accountNumber,
            BigDecimal amount) {

        Account account =
                getAccountByNumber(
                        accountNumber);

        if (account.getBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient Balance");
        }

        account.setBalance(
                account.getBalance()
                        .subtract(amount));

        return accountRepository
                .save(account);
    }
}