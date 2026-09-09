package com.bank.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // PERSONAL DETAILS
    // =========================

    @NotBlank(message = "First Name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "First Name can contain only letters")
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Last Name can contain only letters")
    @Column(nullable = false)
    private String lastName;

    @NotBlank(message = "Username is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z][a-z]{3,}$",
            message = "Username format should be like 09Rabindra")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Father Name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Father Name can contain only letters")
    @Column(nullable = false)
    private String fatherName;

    @NotNull(message = "Date of Birth is required")
    @Column(nullable = false)
    private LocalDate dob;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter valid email")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Enter valid 10 digit mobile number")
    @Column(unique = true, nullable = false)
    private String mobile;

    // =========================
    // ADDRESS DETAILS
    // =========================

    @NotBlank(message = "Address Line 1 is required")
    @Column(length = 500, nullable = false)
    private String addressLine1;

    @Column(length = 500)
    private String addressLine2;

    @NotBlank(message = "Country is required")
    @Column(nullable = false)
    private String country;

    @NotBlank(message = "State is required")
    @Column(nullable = false)
    private String state;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "Pincode must be 6 digits")
    @Column(nullable = false)
    private String pincode;

    // =========================
    // BANKING DETAILS
    // =========================

    @NotBlank(message = "Aadhaar Number is required")
    @Pattern(
            regexp = "^[0-9]{12}$",
            message = "Aadhaar must be 12 digits")
    @Column(unique = true, nullable = false, length = 12)
    private String aadhaar;

    @NotBlank(message = "PAN Number is required")
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$",
            message = "Enter valid PAN Number")
    @Column(unique = true, nullable = false, length = 10)
    private String pan;

    @NotBlank(message = "Account Type is required")
    @Column(nullable = false)
    private String accountType;

    // =========================
    // SECURITY
    // =========================

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    
    @Column(nullable = false)
    private Double initialDeposit;
    
    
    @Column(unique = true, nullable = false)
    private String cifNumber;

    @Column(unique = true, nullable = false)
    private String accountNumber;
    
    @Column
    private String nomineeName;

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan != null ? pan.toUpperCase() : null;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public Double getInitialDeposit() {
        return initialDeposit;
    }

    public void setInitialDeposit(Double initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
    public String getCifNumber() {
        return cifNumber;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getNomineeName() {
        return nomineeName;
    }

    public void setNomineeName(String nomineeName) {
        this.nomineeName = nomineeName;
    }
    
}