package com.bank.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.LoanDTO;
import com.bank.entity.Account;
import com.bank.entity.Loan;
import com.bank.entity.Transaction;
import com.bank.entity.User;
import com.bank.enums.AccountType;
import com.bank.enums.LoanStatus;
import com.bank.enums.TransactionType;
import com.bank.exception.ResourceNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.LoanRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import com.bank.service.LoanService;

@Service
@SuppressWarnings("null")
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public LoanServiceImpl(
            LoanRepository loanRepository,
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Loan applyLoan(LoanDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        Loan loan = new Loan();

        loan.setAmount(dto.getAmount());
        loan.setLoanType(dto.getLoanType());
        loan.setStatus(LoanStatus.PENDING);
        loan.setUser(user);

        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Loan updateLoanStatus(
            Long loanId,
            LoanStatus status) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new IllegalStateException("Loan status can only be changed from PENDING");
        }

        // If loan status changes to APPROVED, credit amount to user balance
        if (loan.getStatus() != LoanStatus.APPROVED && status == LoanStatus.APPROVED) {
            User user = loan.getUser();
            if (user != null) {
                List<Account> accounts = accountRepository.findByUserId(user.getId());
                if (accounts.isEmpty()) {
                    Account account = new Account();
                    account.setUser(user);
                    account.setAccountNumber(user.getAccountNumber());
                    
                    AccountType accType = AccountType.SAVINGS;
                    if (user.getAccountType() != null) {
                        try {
                            accType = AccountType.valueOf(user.getAccountType().toUpperCase());
                        } catch (Exception e) {
                            // ignore and use SAVINGS
                        }
                    }
                    account.setAccountType(accType);
                    Double initialDeposit = user.getInitialDeposit();
                    account.setBalance(BigDecimal.valueOf(initialDeposit != null ? initialDeposit : 0.0));
                    
                    account = accountRepository.save(account);
                    accounts = List.of(account);
                }

                BigDecimal loanAmount = loan.getAmount() != null ? loan.getAmount() : BigDecimal.ZERO;

                // Deposit to first account
                Account account = accounts.get(0);
                account.setBalance(account.getBalance().add(loanAmount));
                accountRepository.save(account);

                // Update User.initialDeposit so user profile/dashboard displays the correct updated balance
                Double depositVal = user.getInitialDeposit();
                double currentDeposit = (depositVal != null) ? depositVal : 0.0;
                user.setInitialDeposit(currentDeposit + loanAmount.doubleValue());
                userRepository.save(user);

                // Create transaction log
                Transaction transaction = new Transaction();
                transaction.setFromAccount("LOAN");
                transaction.setToAccount(account.getAccountNumber());
                transaction.setAmount(loanAmount);
                transaction.setTransactionType(TransactionType.LOAN_CREDIT);
                transaction.setTransactionDate(LocalDateTime.now());
                transactionRepository.save(transaction);
            }
        }

        loan.setStatus(status);

        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}