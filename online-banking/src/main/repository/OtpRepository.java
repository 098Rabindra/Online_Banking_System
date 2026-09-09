package com.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bank.entity.Otp;


public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmail(String email);

    Optional<Otp> findByEmailAndOtp(
            String email,
            String otp);


    @Modifying
    @Transactional
    @Query("delete from Otp o where o.email = :email")
    void deleteByEmail(
            @Param("email") String email);

}