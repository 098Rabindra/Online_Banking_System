package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.entity.Transaction;

@Repository
public interface TransactionRepository 
        extends JpaRepository<Transaction, Long> {


    List<Transaction> findByFromAccountOrToAccount(
            String fromAccount,
            String toAccount
    );

}