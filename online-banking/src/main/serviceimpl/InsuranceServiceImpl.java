package com.bank.serviceimpl;

import com.bank.dto.InsuranceDTO;
import com.bank.entity.Insurance;
import com.bank.entity.User;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.enums.InsuranceStatus;
import com.bank.enums.TransactionType;
import com.bank.exception.ResourceNotFoundException;
import com.bank.repository.InsuranceRepository;
import com.bank.repository.UserRepository;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.enums.AccountType;
import com.bank.service.InsuranceService;
import org.springframework.lang.NonNull;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public InsuranceServiceImpl(
            InsuranceRepository insuranceRepository,
            UserRepository userRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {

        this.insuranceRepository = insuranceRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Insurance applyInsurance(InsuranceDTO dto, String userEmail) {
        User user;
        if (dto.getAccountNumber() != null && !dto.getAccountNumber().trim().isEmpty()) {
            user = userRepository.findByAccountNumber(dto.getAccountNumber().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with account number: " + dto.getAccountNumber()));
        } else {
            user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Insurance insurance = new Insurance();
        insurance.setFullName(dto.getFullName());
        insurance.setMobileNumber(dto.getMobileNumber());
        insurance.setInsuranceType(dto.getInsuranceType());
        insurance.setAmount(dto.getAmount());
        insurance.setStatus(InsuranceStatus.PENDING);
        insurance.setUser(user);
        insurance.setCreatedAt(LocalDateTime.now());

        // Generate random policy number e.g., INS78451236
        Random random = new Random();
        int num = 10000000 + random.nextInt(90000000);
        insurance.setPolicyNumber("INS" + num);

        return insuranceRepository.save(insurance);
    }

    @Override
    public List<Insurance> getInsurancesByUser(String userEmail) {
        return insuranceRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<Insurance> getAllInsurances() {
        return insuranceRepository.findAll();
    }

    @Override
    @Transactional
    public Insurance updateInsuranceStatus(@NonNull Long insuranceId, InsuranceStatus status) {
        Insurance insurance = insuranceRepository.findById(insuranceId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance not found"));

        if (insurance.getStatus() != InsuranceStatus.PENDING) {
            throw new IllegalStateException("Insurance status can only be changed from PENDING");
        }

        insurance.setStatus(status);

        if (status == InsuranceStatus.APPROVED) {
            // Add amount to user account
            User user = insurance.getUser();
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
                account.setBalance(BigDecimal.valueOf(initialDeposit != null ? initialDeposit.doubleValue() : 0.0));
                
                account = accountRepository.save(account);
                accounts = List.of(account);
            }

            // Deposit to first account
            Account account = accounts.get(0);
            account.setBalance(account.getBalance().add(insurance.getAmount()));
            accountRepository.save(account);

            // Also update User.initialDeposit so user profile/dashboard displays the correct updated balance
            Double depositVal = user.getInitialDeposit();
            double currentDeposit = (depositVal != null) ? depositVal.doubleValue() : 0.0;
            user.setInitialDeposit(currentDeposit + insurance.getAmount().doubleValue());
            userRepository.save(user);

            // Create transaction log
            Transaction transaction = new Transaction();
            transaction.setFromAccount("INSURANCE");
            transaction.setToAccount(account.getAccountNumber());
            transaction.setAmount(insurance.getAmount());
            transaction.setTransactionType(TransactionType.DEPOSIT);
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);
        }

        return insuranceRepository.save(insurance);
    }
}
