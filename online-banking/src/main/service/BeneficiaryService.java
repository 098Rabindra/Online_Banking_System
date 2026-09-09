package com.bank.service;

import java.util.List;

import com.bank.entity.Beneficiary;

public interface BeneficiaryService {

    Beneficiary addBeneficiary(Beneficiary beneficiary);

    List<Beneficiary> getBeneficiaries(Long userId);

    void deleteBeneficiary(Long beneficiaryId);
}