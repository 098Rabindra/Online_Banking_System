package com.bank.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

public class InsuranceDTO {

    @NotBlank(message = "Full name is required")
    @Pattern(
            regexp = "^[A-Za-z]+([ .]+[A-Za-z]+)+$",
            message = "Enter a valid full name (e.g., Rabindra Dakua, G.Pooja)")
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9][0-9]{9}$",
            message = "Enter a valid 10-digit mobile number")
    private String mobileNumber;

    @NotBlank(message = "Insurance type is required")
    private String insuranceType;

    @NotNull(message = "Insurance amount is required")
    @DecimalMin(value = "10000", message = "Insurance amount must be between 10,000 and 1,000,000")
    @DecimalMax(value = "1000000", message = "Insurance amount must be between 10,000 and 1,000,000")
    private BigDecimal amount;

    @NotBlank(message = "Account number is required")
    @Pattern(
            regexp = "^[0-9]{14}$",
            message = "Account number must be exactly 14 digits")
    private String accountNumber;

    public InsuranceDTO() {
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
