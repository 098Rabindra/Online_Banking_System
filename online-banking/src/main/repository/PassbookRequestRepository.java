package com.bank.repository;

import com.bank.entity.PassbookRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PassbookRequestRepository extends JpaRepository<PassbookRequest, Long> {
    List<PassbookRequest> findByUserEmail(String email);
    List<PassbookRequest> findByUserId(Long userId);
}
