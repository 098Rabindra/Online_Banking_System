package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.entity.Role;
import com.bank.entity.User;

public interface UserRepository
        extends JpaRepository<User, Long> {

    List<User> findByRole(Role role);

    Optional<User> findTopByOrderByIdDesc();

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByAccountNumber(String accountNumber);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByUsername(String username);

    boolean existsByAadhaar(String aadhaar);

    boolean existsByPan(String pan);
}