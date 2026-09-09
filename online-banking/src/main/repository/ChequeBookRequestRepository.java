package com.bank.repository;

import com.bank.entity.ChequeBookRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChequeBookRequestRepository extends JpaRepository<ChequeBookRequest, Long> {
    List<ChequeBookRequest> findByUserEmail(String email);
    List<ChequeBookRequest> findByUserId(Long userId);
}
