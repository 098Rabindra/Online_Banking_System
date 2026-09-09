package com.bank.repository;

import com.bank.entity.StopChequeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StopChequeRequestRepository extends JpaRepository<StopChequeRequest, Long> {
    List<StopChequeRequest> findByUserEmail(String email);
    List<StopChequeRequest> findByUserId(Long userId);
}
