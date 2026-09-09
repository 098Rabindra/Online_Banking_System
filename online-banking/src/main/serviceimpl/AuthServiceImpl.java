package com.bank.serviceimpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.dto.LoginRequest;
import com.bank.dto.RegisterRequest;
import com.bank.entity.Role;
import com.bank.entity.User;
import com.bank.entity.Otp;
import com.bank.entity.Account;
import com.bank.enums.AccountType;
import com.bank.repository.UserRepository;
import com.bank.repository.OtpRepository;
import com.bank.repository.AccountRepository;
import com.bank.service.AuthService;
import java.math.BigDecimal;
import com.bank.service.OtpService;
import com.bank.util.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private final AccountRepository accountRepository;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            OtpService otpService,
            OtpRepository otpRepository,
            AccountRepository accountRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.otpRepository = otpRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public String register(RegisterRequest request) {

        if (userRepository.existsByEmail(
                request.getEmail().trim().toLowerCase())) {

            throw new RuntimeException(
                    "Email already exists");
        }

        if (userRepository.existsByMobile(
                request.getMobile().trim())) {

            throw new RuntimeException(
                    "Mobile number already exists");
        }

        if (userRepository.existsByUsername(
                request.getUsername().trim())) {

            throw new RuntimeException(
                    "Username already exists");
        }

        if (userRepository.existsByAadhaar(
                request.getAadhaar().trim())) {

            throw new RuntimeException(
                    "Aadhaar number already exists");
        }

        if (userRepository.existsByPan(
                request.getPan().trim().toUpperCase())) {

            throw new RuntimeException(
                    "PAN number already exists");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setFatherName(request.getFatherName());
        user.setDob(request.getDob());

        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());

        user.setAddressLine1(request.getAddressLine1());
        user.setAddressLine2(request.getAddressLine2());

        user.setCountry(request.getCountry());
        user.setState(request.getState());
        user.setCity(request.getCity());
        user.setPincode(request.getPincode());

        user.setAadhaar(request.getAadhaar());
        user.setPan(request.getPan());

        user.setAccountType(request.getAccountType());
        user.setNomineeName(request.getNomineeName());

        
        user.setInitialDeposit(
                request.getInitialDeposit());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        user.setRole(Role.ROLE_USER);
        user.setCifNumber(generateCifNumber());
        user.setAccountNumber(generateAccountNumber());

        User savedUser = userRepository.save(user);

        // Create and save account in AccountRepository
        Account account = new Account();
        account.setAccountNumber(savedUser.getAccountNumber());
        
        AccountType accType = AccountType.SAVINGS;
        if (savedUser.getAccountType() != null) {
            try {
                accType = AccountType.valueOf(savedUser.getAccountType().toUpperCase());
            } catch (Exception e) {
                // use SAVINGS
            }
        }
        account.setAccountType(accType);
        Double initialDeposit = savedUser.getInitialDeposit();
        account.setBalance(BigDecimal.valueOf(initialDeposit != null ? initialDeposit.doubleValue() : 0.0));
        account.setUser(savedUser);
        
        accountRepository.save(account);

        return "User Registered Successfully";
    }

    @Override
    public String validateLogin(LoginRequest request) {

        User user = userRepository
                .findByEmail(
                        request.getEmail()
                               .trim()
                               .toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("EMAIL_NOT_FOUND"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "INVALID_PASSWORD");
        }

        String otpType = (user.getRole() == Role.ROLE_ADMIN) ? "ADMIN_LOGIN" : "USER_LOGIN";
        otpService.generateOtp(user.getEmail(), otpType);
        
        return user.getRole().name();
    }

    @Override
    public String verifyLoginOtp(
            String email,
            String otp) {

        boolean verified =
                otpService.verifyOtp(email, otp);

        if (!verified) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return jwtUtil.generateToken(user.getEmail());
    }
    
    @Override
    public void resendOtp(String email) {

        System.out.println("Received email: " + email);

        User user = userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("EMAIL_NOT_FOUND"));

        System.out.println("User found: " + user.getEmail());

        String otpType = (user.getRole() == Role.ROLE_ADMIN) ? "ADMIN_LOGIN" : "USER_LOGIN";
        otpService.generateOtp(user.getEmail(), otpType);

        System.out.println("OTP resent successfully");
    }
    private String generateCifNumber() {

        var lastUser =
                userRepository.findTopByOrderByIdDesc();

        if (lastUser.isEmpty()) {
            return "KBI1004100";
        }

        String lastCif =
                lastUser.get().getCifNumber();

        if (lastCif == null || lastCif.isBlank()) {
            return "KBI1004100";
        }

        long next =
                Long.parseLong(
                        lastCif.replace("KBI", "")
                ) + 1;

        return "KBI" + next;
    }
    private String generateAccountNumber() {

        var lastUser =
                userRepository.findTopByOrderByIdDesc();

        if (lastUser.isEmpty()) {
            return "10041002423";
        }

        String lastAccount =
                lastUser.get().getAccountNumber();

        if (lastAccount == null ||
            lastAccount.isBlank()) {

            return "10041002423";
        }

        long next =
                Long.parseLong(lastAccount) + 1;

        return String.valueOf(next);
    }

    @Override
    public void sendForgotPasswordOtp(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email not found"));
        otpService.generateOtp(user.getEmail(), "FORGOT_PASSWORD");
    }

    @Override
    public boolean verifyForgotPasswordOtp(String email, String otp) {
        userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Email not found"));
        return otpService.verifyOtp(email, otp);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void resetPassword(String email, String password) {
        String cleanEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        Otp otpRecord = otpRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new RuntimeException("No OTP verification session found"));

        if (!otpRecord.isVerified()) {
            throw new RuntimeException("OTP has not been verified yet");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        otpRepository.delete(otpRecord);
    }
}