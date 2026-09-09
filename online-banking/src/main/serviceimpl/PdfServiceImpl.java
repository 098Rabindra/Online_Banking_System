package com.bank.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.entity.Transaction;
import com.bank.repository.TransactionRepository;
import com.bank.service.PdfService;
import com.bank.util.PdfGeneratorUtil;

@Service
public class PdfServiceImpl implements PdfService {

    private final TransactionRepository transactionRepository;
    private final com.bank.repository.UserRepository userRepository;

    public PdfServiceImpl(
            TransactionRepository transactionRepository,
            com.bank.repository.UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public byte[] generateStatement(
            String accountNumber) {
        String trimmedAcc = accountNumber.trim();
        System.out.println("PdfServiceImpl: Generating statement for account: [" + trimmedAcc + "]");

        List<Transaction> transactions =
                transactionRepository
                        .findByFromAccountOrToAccount(
                                trimmedAcc,
                                trimmedAcc);

        com.bank.entity.User user = userRepository.findByAccountNumber(trimmedAcc).orElse(null);
        if (user != null) {
            System.out.println("PdfServiceImpl: Found user " + user.getFirstName() + " " + user.getLastName() + " for account " + trimmedAcc);
        } else {
            System.out.println("PdfServiceImpl: Warning - user NOT found in repository for account " + trimmedAcc);
        }

        return PdfGeneratorUtil.generateStatement(
                trimmedAcc,
                transactions,
                user);
    }
}