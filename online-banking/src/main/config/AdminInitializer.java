package com.bank.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bank.entity.Role;
import com.bank.entity.User;
import com.bank.repository.UserRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByEmail("dakuaraja074@gmail.com")) {

                User admin = new User();

                admin.setFirstName("AdminRaja");
                admin.setLastName("Dakua");

                admin.setUsername("99Admin");

                admin.setFatherName("Rabindra Dakua");

                admin.setDob(
                        LocalDate.of(1990, 1, 1));

                admin.setEmail("dakuaraja074@gmail.com");

                admin.setMobile("9789562321");

                admin.setAddressLine1(
                        "KBI Head Office");

                admin.setAddressLine2(
                        "Bhubaneswar");

                admin.setCountry("India");
                admin.setState("Odisha");
                admin.setCity("Bhubaneswar");
                admin.setPincode("751001");

                admin.setAadhaar(
                        "999988887777");

                admin.setPan(
                        "ABCDE1234F");

                admin.setAccountType(
                        "SAVINGS");

                admin.setNomineeName(
                        "Rabindra Admin");

                admin.setInitialDeposit(
                        50000.0);

                admin.setPassword(
                        passwordEncoder.encode(
                                "Admin@123"));

                admin.setRole(
                        Role.ROLE_ADMIN);

                admin.setCifNumber(
                        "KBI1000000");

                admin.setAccountNumber(
                        "14941010036604");

                userRepository.save(admin);

                System.out.println(
                        "Admin Created Successfully");
            }
        };
    }
}