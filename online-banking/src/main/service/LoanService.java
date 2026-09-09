package com.bank.service;

import java.util.List;

import com.bank.dto.LoanDTO;
import com.bank.entity.Loan;
import com.bank.enums.LoanStatus;

public interface LoanService {

    Loan applyLoan(LoanDTO loanDTO);

    List<Loan> getLoansByUser(Long userId);

    Loan updateLoanStatus(
            Long loanId,
            LoanStatus status);

    List<Loan> getAllLoans();
}