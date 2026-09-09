package com.bank.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bank.dto.LoanDTO;
import com.bank.entity.Loan;
import com.bank.enums.LoanStatus;
import com.bank.service.LoanService;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(
            LoanService loanService) {

        this.loanService = loanService;
    }

    @PostMapping
    public org.springframework.http.ResponseEntity<?> applyLoan(
            @RequestBody LoanDTO dto) {

        java.util.Map<String, String> errors = new java.util.HashMap<>();

        if (dto.getUserId() == null) {
            errors.put("userId", "User ID is required");
        }

        if (dto.getAmount() == null) {
            errors.put("amount", "Loan amount is required");
        } else {
            double amt = dto.getAmount().doubleValue();
            if (amt < 10000 || amt > 1000000) {
                errors.put("amount", "Loan amount must be between 10,000 and 1,000,000");
            }
        }

        String loanType = dto.getLoanType();
        if (loanType == null || loanType.trim().isEmpty()) {
            errors.put("loanType", "Loan type is required");
        } else {
            switch (loanType) {
                case "Home Loan" -> {
                    if (dto.getMonthlyIncome() == null) {
                        errors.put("monthlyIncome", "Monthly Income is required");
                    } else {
                        double inc = dto.getMonthlyIncome().doubleValue();
                        if (inc < 10000 || inc > 1000000) {
                            errors.put("monthlyIncome", "Monthly income must be between 10,000 and 1,000,000");
                        }
                    }
                }
                case "Personal Loan" -> {
                    if (dto.getMonthlySalary() == null) {
                        errors.put("monthlySalary", "Monthly Salary is required");
                    } else {
                        double sal = dto.getMonthlySalary().doubleValue();
                        if (sal < 10000 || sal > 1000000) {
                            errors.put("monthlySalary", "Monthly salary must be between 10,000 and 1,000,000");
                        }
                    }
                }
                case "Education Loan" -> {
                    if (dto.getStudentName() == null || dto.getStudentName().trim().isEmpty()) {
                        errors.put("studentName", "Student Name is required");
                    } else if (!dto.getStudentName().matches("^[A-Za-z]+([ .]+[A-Za-z]+)+$")) {
                        errors.put("studentName", "Enter a valid student name (e.g., Rabindra Dakua, G.Pooja)");
                    }
                    if (dto.getCollegeName() == null || dto.getCollegeName().trim().isEmpty()) {
                        errors.put("collegeName", "College Name is required");
                    } else if (!dto.getCollegeName().matches("^[A-Za-z ]+$")) {
                        errors.put("collegeName", "College Name can contain only letters and spaces");
                    }
                    if (dto.getCityName() == null || dto.getCityName().trim().isEmpty()) {
                        errors.put("cityName", "City is required");
                    } else if (!dto.getCityName().matches("^[A-Za-z ]+$")) {
                        errors.put("cityName", "City can contain only letters");
                    }
                }
                case "Vehicle Loan" -> {
                    if (dto.getVehicleType() == null || dto.getVehicleType().trim().isEmpty()) {
                        errors.put("vehicleType", "Vehicle Type is required");
                    }
                    String company = dto.getVehicleCompany();
                    if (company == null || company.trim().isEmpty()) {
                        errors.put("vehicleCompany", "Vehicle Company is required");
                    } else if ("Other".equalsIgnoreCase(company)) {
                        String otherC = dto.getOtherCompany();
                        if (otherC == null || otherC.trim().isEmpty()) {
                            errors.put("otherCompany", "Please specify vehicle company");
                        } else if (!otherC.matches(".*[A-Za-z].*")) {
                            errors.put("otherCompany", "Only numbers are not allowed; must contain letters");
                        }
                    }
                    String model = dto.getVehicleModel();
                    if (model == null || model.trim().isEmpty()) {
                        errors.put("vehicleModel", "Vehicle Model is required");
                    } else if ("Other".equalsIgnoreCase(model)) {
                        String otherM = dto.getOtherModel();
                        if (otherM == null || otherM.trim().isEmpty()) {
                            errors.put("otherModel", "Please specify vehicle model");
                        } else if (!otherM.matches(".*[A-Za-z].*")) {
                            errors.put("otherModel", "Only numbers are not allowed; must contain letters");
                        }
                    }
                    
                    double price = 0;
                    if (dto.getVehiclePrice() == null) {
                        errors.put("vehiclePrice", "Vehicle Price is required");
                    } else {
                        price = dto.getVehiclePrice().doubleValue();
                        if (price < 10000 || price > 1000000) {
                            errors.put("vehiclePrice", "Vehicle Price must be between 10,000 and 1,000,000");
                        }
                    }
                    
                    if (dto.getAmount() != null) {
                        double amt = dto.getAmount().doubleValue();
                        if (price > 0 && amt > price) {
                            errors.put("amount", "Loan Amount cannot exceed vehicle price");
                        }
                    }
                }
                case "Gold Loan" -> {
                    if (dto.getGoldWeight() == null) {
                        errors.put("goldWeight", "Gold Weight is required");
                    } else if (dto.getGoldWeight() <= 0) {
                        errors.put("goldWeight", "Gold Weight must be a positive number");
                    }
                }
                case "Business Loan" -> {
                    if (dto.getBusinessName() == null || dto.getBusinessName().trim().isEmpty()) {
                        errors.put("businessName", "Business Name is required");
                    } else if (!dto.getBusinessName().matches("^[A-Za-z]+([ .]+[A-Za-z]+)+$")) {
                        errors.put("businessName", "Enter a valid name (e.g., Rabindra Dakua, G.Pooja)");
                    }
                }
                case "Agriculture Loan" -> {
                    if (dto.getFarmerName() == null || dto.getFarmerName().trim().isEmpty()) {
                        errors.put("farmerName", "Farmer Name is required");
                    } else if (!dto.getFarmerName().matches("^[A-Za-z]+([ .]+[A-Za-z]+)+$")) {
                        errors.put("farmerName", "Enter a valid name (e.g., Rabindra Dakua, G.Pooja)");
                    }
                }
                default -> {}
            }
        }

        if (!errors.isEmpty()) {
            return org.springframework.http.ResponseEntity.badRequest().body(errors);
        }

        Loan savedLoan = loanService.applyLoan(dto);
        return org.springframework.http.ResponseEntity.ok(savedLoan);
    }

    @GetMapping("/user/{userId}")
    public List<Loan> getLoans(
            @PathVariable Long userId) {

        return loanService.getLoansByUser(userId);
    }

    @PutMapping("/{loanId}/{status}")
    public Loan updateStatus(
            @PathVariable Long loanId,
            @PathVariable LoanStatus status) {

        return loanService.updateLoanStatus(
                loanId,
                status);
    }
}