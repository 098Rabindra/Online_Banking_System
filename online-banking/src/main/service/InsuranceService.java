package com.bank.service;

import com.bank.dto.InsuranceDTO;
import com.bank.entity.Insurance;
import com.bank.enums.InsuranceStatus;
import org.springframework.lang.NonNull;
import java.util.List;

public interface InsuranceService {

    Insurance applyInsurance(InsuranceDTO dto, String userEmail);

    List<Insurance> getInsurancesByUser(String userEmail);

    List<Insurance> getAllInsurances();

    Insurance updateInsuranceStatus(@NonNull Long insuranceId, InsuranceStatus status);
}
