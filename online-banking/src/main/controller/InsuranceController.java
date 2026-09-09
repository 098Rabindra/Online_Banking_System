package com.bank.controller;

import com.bank.dto.InsuranceDTO;
import com.bank.entity.Insurance;
import com.bank.enums.InsuranceStatus;
import com.bank.service.InsuranceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/insurances")
public class InsuranceController {

    private final InsuranceService insuranceService;

    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @PostMapping
    public ResponseEntity<?> applyInsurance(
            @Valid @RequestBody InsuranceDTO dto,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Unauthorized"));
        }

        Insurance insurance = insuranceService.applyInsurance(
                dto,
                authentication.getName());

        return ResponseEntity.ok(insurance);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Insurance>> getMyInsurances(
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        List<Insurance> insurances = insuranceService.getInsurancesByUser(
                authentication.getName());

        return ResponseEntity.ok(insurances);
    }

    @GetMapping
    public ResponseEntity<List<Insurance>> getAllInsurances() {
        return ResponseEntity.ok(insuranceService.getAllInsurances());
    }

    @PutMapping("/{insuranceId}/{status}")
    public ResponseEntity<?> updateStatus(
            @PathVariable @NonNull Long insuranceId,
            @PathVariable InsuranceStatus status) {

        try {
            Insurance insurance = insuranceService.updateInsuranceStatus(
                    insuranceId,
                    status);

            return ResponseEntity.ok(insurance);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
