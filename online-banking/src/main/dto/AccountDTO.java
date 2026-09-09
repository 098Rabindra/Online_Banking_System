package com.bank.dto;

import java.math.BigDecimal;

import com.bank.enums.AccountType;

public class AccountDTO {

    private Long userId;

    private AccountType accountType;

    private BigDecimal balance;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}