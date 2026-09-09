package com.bank.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TransactionUtil {

    private TransactionUtil() {
    }

    public static String generateTransactionReference() {

        String timestamp =
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern(
                                "yyyyMMddHHmmss"));

        String uuid =
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        return "TXN-" + timestamp + "-" + uuid;
    }
}