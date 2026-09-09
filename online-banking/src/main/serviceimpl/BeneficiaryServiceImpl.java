package com.bank.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bank.entity.Beneficiary;
import com.bank.repository.BeneficiaryRepository;
import com.bank.service.BeneficiaryService;

@Service
@SuppressWarnings("null")
public class BeneficiaryServiceImpl
        implements BeneficiaryService {

    private final BeneficiaryRepository repository;

    public BeneficiaryServiceImpl(
            BeneficiaryRepository repository) {

        this.repository = repository;
    }

    @Override
    public Beneficiary addBeneficiary(
            Beneficiary beneficiary) {

        return repository.save(beneficiary);
    }

    @Override
    public List<Beneficiary> getBeneficiaries(
            Long userId) {

        return repository.findByUserId(userId);
    }

    @Override
    public void deleteBeneficiary(Long id) {

        repository.deleteById(id);
    }
}