package com.bank.util;


import java.security.SecureRandom;

public class AccountNumberGenerator {

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private AccountNumberGenerator() {
    }

    public static String generateAccountNumber() {

        long number =
                100000000000L
                        + (Math.abs(RANDOM.nextLong())
                        % 900000000000L);

        return String.valueOf(number);
    }
}
