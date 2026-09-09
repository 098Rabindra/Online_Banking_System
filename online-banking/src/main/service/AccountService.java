package com.bank.service;

import java.math.BigDecimal;
import java.util.List;

import com.bank.dto.AccountDTO;
import com.bank.entity.Account;

public interface AccountService {

    Account createAccount(AccountDTO accountDTO);

    Account getAccountByNumber(String accountNumber);

    List<Account> getAccountsByUser(Long userId);

    BigDecimal getBalance(String accountNumber);

    Account deposit(String accountNumber, BigDecimal amount);

    Account withdraw(String accountNumber, BigDecimal amount);
}