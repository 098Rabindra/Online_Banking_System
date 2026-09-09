package com.bank.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

    // =========================
    // PERSONAL INFORMATION
    // =========================

    @NotBlank(message = "First Name is required")
    @Pattern(
        regexp = "^[A-Za-z ]+$",
        message = "First Name can contain only letters"
    )
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Pattern(
        regexp = "^[A-Za-z ]+$",
        message = "Last Name can contain only letters"
    )
    private String lastName;

    @NotBlank(message = "Username is required")
    @Pattern(
        regexp = "^[0-9]{2,3}[A-Za-z]{5,}$",
        message = "Username must contain 2-3 digits followed by at least 5 letters (Example: 09Rabindra)"
    )
    private String username;

    @NotBlank(message = "Father Name is required")
    @Pattern(
        regexp = "^[A-Za-z ]+$",
        message = "Father Name can contain only letters"
    )
    private String fatherName;

    @NotNull(message = "Date of Birth is required")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter valid email")
    private String email;

    @NotBlank(message = "Mobile Number is required")
    @Pattern(
        regexp = "^[6-9][0-9]{9}$",
        message = "Enter valid 10 digit mobile number"
    )
    private String mobile;

    // =========================
    // ADDRESS INFORMATION
    // =========================

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Pincode is required")
    @Pattern(
        regexp = "^[0-9]{6}$",
        message = "Pincode must be 6 digits"
    )
    private String pincode;

    // =========================
    // BANKING INFORMATION
    // =========================

    @NotBlank(message = "Aadhaar Number is required")
    @Pattern(
        regexp = "^[0-9]{12}$",
        message = "Aadhaar must be 12 digits"
    )
    private String aadhaar;

    @NotBlank(message = "PAN Number is required")
    @Pattern(
        regexp = "^[A-Za-z]{5}[0-9]{4}[A-Za-z]$",
        message = "Enter valid PAN Number"
    )
    private String pan;

    @NotBlank(message = "Account Type is required")
    private String accountType;

    // =========================
    // SECURITY
    // =========================

    @NotBlank(message = "Password is required")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,20}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number, one special character and be 8-20 characters long"
    )
    private String password;
    
    
    @NotNull(message = "Initial Deposit is required")
    private Double initialDeposit;
    
    @NotBlank(message = "Nominee Name is required")
    private String nomineeName;
    

    public RegisterRequest() {
    }

    // =========================
    // GETTERS AND SETTERS
    // =========================

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username.trim() : null;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName != null ? fatherName.trim() : null;
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
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile != null ? mobile.trim() : null;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1 != null ? addressLine1.trim() : null;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2 != null ? addressLine2.trim() : null;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country != null ? country.trim() : null;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state != null ? state.trim() : null;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city != null ? city.trim() : null;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode != null ? pincode.trim() : null;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar != null ? aadhaar.trim() : null;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan != null ? pan.trim().toUpperCase() : null;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType != null ? accountType.trim() : null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Double getInitialDeposit() {
        return initialDeposit;
    }
    

    public void setInitialDeposit(Double initialDeposit) {
        this.initialDeposit = initialDeposit;
    }
    public String getNomineeName() {
        return nomineeName;
    }

    public void setNomineeName(String nomineeName) {
        this.nomineeName = nomineeName;
    }
}