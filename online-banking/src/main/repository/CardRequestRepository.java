package com.bank.repository;

import com.bank.entity.CardRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CardRequestRepository extends JpaRepository<CardRequest, Long> {
    List<CardRequest> findByUserEmail(String email);
    List<CardRequest> findByUserId(Long userId);
    List<CardRequest> findByAccountNumber(String accountNumber);
}
