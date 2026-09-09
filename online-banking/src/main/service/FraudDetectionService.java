package com.bank.service;

import com.bank.dto.TransactionDTO;

public interface FraudDetectionService {

    void analyzeTransaction(
            TransactionDTO dto);

    boolean isFraudulent(
            TransactionDTO dto);
}