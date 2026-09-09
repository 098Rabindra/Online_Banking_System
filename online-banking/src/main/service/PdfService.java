package com.bank.service;

public interface PdfService {

    byte[] generateStatement(String accountNumber);
}