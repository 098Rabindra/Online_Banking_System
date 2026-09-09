package com.bank.controller;


import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bank.entity.Beneficiary;
import com.bank.service.BeneficiaryService;

@RestController
@RequestMapping("/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService service;

    public BeneficiaryController(
            BeneficiaryService service) {

        this.service = service;
    }

    @PostMapping
    public Beneficiary addBeneficiary(
            @RequestBody Beneficiary beneficiary) {

        return service.addBeneficiary(beneficiary);
    }

    @GetMapping("/{userId}")
    public List<Beneficiary> getBeneficiaries(
            @PathVariable Long userId) {

        return service.getBeneficiaries(userId);
    }

    @DeleteMapping("/{id}")
    public void deleteBeneficiary(
            @PathVariable Long id) {

        service.deleteBeneficiary(id);
    }
}
