package com.bank.repository;

import com.bank.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {

    List<Insurance> findByUserId(Long userId);

    List<Insurance> findByUserEmail(String email);
}
