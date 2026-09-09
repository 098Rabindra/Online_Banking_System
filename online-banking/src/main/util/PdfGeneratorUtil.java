package com.bank.util;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.bank.entity.Transaction;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

public class PdfGeneratorUtil {

    private PdfGeneratorUtil() {
    }

    private static Image loadLogoImage() {
        try {
            try (java.io.InputStream logoStream = PdfGeneratorUtil.class.getResourceAsStream("/static/images/kuumca-logo.png")) {
                if (logoStream != null) {
                    byte[] logoBytes = logoStream.readAllBytes();
                    Image logo = Image.getInstance(logoBytes);
                    logo.scaleToFit(70, 70);
                    logo.setAlignment(Element.ALIGN_CENTER);
                    return logo;
                }
            }
        } catch (com.lowagie.text.DocumentException | java.io.IOException e) {
            // ignore
        }
        try {
            java.io.File file = new java.io.File("src/main/resources/static/images/kuumca-logo.png");
            if (file.exists()) {
                Image logo = Image.getInstance(file.getAbsolutePath());
                logo.scaleToFit(70, 70);
                logo.setAlignment(Element.ALIGN_CENTER);
                return logo;
            }
        } catch (com.lowagie.text.DocumentException | java.io.IOException e) {
            // ignore
        }
        return null;
    }

    public static byte[] generateStatement(
            String accountNumber,
            List<Transaction> transactions) {
        return generateStatement(accountNumber, transactions, null);
    }

    public static byte[] generateStatement(
            String accountNumber,
            List<Transaction> transactions,
            com.bank.entity.User user) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Document document = new Document(PageSize.A4, 50, 50, 50, 50)) {
            
            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Password protection
            if (user != null) {
                String password = generateStatementPdfPassword(user);
                System.out.println("PdfGeneratorUtil: PDF password generated for account " + user.getAccountNumber() + " is: [" + password + "]");
                writer.setEncryption(
                        password.getBytes(),
                        "admin123".getBytes(),
                        PdfWriter.ALLOW_PRINTING,
                        PdfWriter.STANDARD_ENCRYPTION_128);
            } else {
                System.out.println("PdfGeneratorUtil: Warning - User object is null. PDF statement will not be password-encrypted.");
            }

            document.open();

            // Fonts & Styling
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new java.awt.Color(91, 36, 135));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, java.awt.Color.DARK_GRAY);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, java.awt.Color.BLACK);
            Font boldTextFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, java.awt.Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, java.awt.Color.WHITE);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, java.awt.Color.GRAY);

            // Add Logo
            Image logo = loadLogoImage();
            if (logo != null) {
                logo.setSpacingAfter(10);
                document.add(logo);
            }

            // Title
            Paragraph title = new Paragraph("KUUMCA BANK OF INDIA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Paragraph subtitle = new Paragraph("ACCOUNT STATEMENT", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // Line separator
            Paragraph line = new Paragraph("------------------------------------------------------------------------------------------------------------------------", subtitleFont);
            line.setSpacingAfter(15);
            document.add(line);

            // Metadata info
            Paragraph info = new Paragraph();
            info.add(new Chunk("Account Number: ", boldTextFont));
            info.add(new Chunk(accountNumber, textFont));
            if (user != null) {
                info.add(new Chunk(" | CIF: ", boldTextFont));
                info.add(new Chunk(user.getCifNumber() != null ? user.getCifNumber() : "N/A", textFont));
                info.add(new Chunk(" | Holder: ", boldTextFont));
                info.add(new Chunk(user.getFirstName() + " " + user.getLastName(), textFont));
            }
            info.setSpacingAfter(20);
            document.add(info);

            // Transactions Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(5);
            table.setSpacingAfter(20);
            table.setWidths(new float[]{25f, 15f, 40f, 20f});

            // Headers
            PdfPCell cell1 = new PdfPCell(new Phrase("Date", tableHeaderFont));
            cell1.setBackgroundColor(new java.awt.Color(91, 36, 135));
            cell1.setPadding(8);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Ref ID", tableHeaderFont));
            cell2.setBackgroundColor(new java.awt.Color(91, 36, 135));
            cell2.setPadding(8);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Description", tableHeaderFont));
            cell3.setBackgroundColor(new java.awt.Color(91, 36, 135));
            cell3.setPadding(8);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Amount", tableHeaderFont));
            cell4.setBackgroundColor(new java.awt.Color(91, 36, 135));
            cell4.setPadding(8);
            table.addCell(cell4);

            // Rows
            if (transactions != null && !transactions.isEmpty()) {
                for (Transaction t : transactions) {
                    String dateStr = t.getTransactionDate() != null 
                        ? t.getTransactionDate().format(java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm")) 
                        : "N/A";
                    
                    String type = t.getTransactionType() != null ? t.getTransactionType().name() : "TRANSFER";
                    
                    String descStr = switch (type) {
                        case "TRANSFER" -> {
                            if (accountNumber.equals(t.getFromAccount())) {
                                yield "Transfer to " + t.getToAccount();
                            } else {
                                yield "Transfer from " + t.getFromAccount();
                            }
                        }
                        case "DEPOSIT" -> "Cash Deposit / Insurance Premium";
                        case "WITHDRAW" -> "Cash Withdrawal";
                        case "LOAN_CREDIT" -> "Loan Credit";
                        case "INTEREST_CREDIT" -> "Interest Credit";
                        case "CHARGE_DEBIT" -> "Service Charge";
                        default -> type;
                    };

                    boolean isDebit = false;
                    if ("TRANSFER".equals(type) && accountNumber.equals(t.getFromAccount())) {
                        isDebit = true;
                    } else if ("WITHDRAW".equals(type) || "CHARGE_DEBIT".equals(type)) {
                        isDebit = true;
                    }

                    String sign = isDebit ? "-" : "+";
                    java.awt.Color amtColor = isDebit ? new java.awt.Color(180, 0, 0) : new java.awt.Color(0, 120, 0);
                    Font amtFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, amtColor);

                    String formattedAmount = String.format("%sRs.%,.2f", sign, t.getAmount());

                    PdfPCell dateCell = new PdfPCell(new Phrase(dateStr, textFont));
                    dateCell.setPadding(6);
                    table.addCell(dateCell);

                    PdfPCell refCell = new PdfPCell(new Phrase(t.getId().toString(), textFont));
                    refCell.setPadding(6);
                    table.addCell(refCell);

                    PdfPCell descCell = new PdfPCell(new Phrase(descStr, textFont));
                    descCell.setPadding(6);
                    table.addCell(descCell);

                    PdfPCell amtCell = new PdfPCell(new Phrase(formattedAmount, amtFont));
                    amtCell.setPadding(6);
                    table.addCell(amtCell);
                }
            } else {
                PdfPCell empty = new PdfPCell(new Phrase("No transactions found.", textFont));
                empty.setColspan(4);
                empty.setPadding(8);
                empty.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(empty);
            }

            document.add(table);

            Paragraph signature = new Paragraph("Authorized Signatory\nKUUMCA Bank of India", boldTextFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            signature.setSpacingBefore(30);
            document.add(signature);

            Paragraph disclaimer = new Paragraph(
                    "\n\n* This is a password-protected, computer-generated document and requires no physical signature.",
                    footerFont);
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            document.add(disclaimer);

            document.close();
            return out.toByteArray();

        } catch (com.lowagie.text.DocumentException | java.io.IOException e) {
            throw new RuntimeException("PDF Generation Failed", e);
        }
    }

    private static String generateStatementPdfPassword(com.bank.entity.User user) {
        return generatePdfPassword(user);
    }

    private static String generatePdfPassword(com.bank.entity.User user) {
        if (user == null) {
            return "USER0101";
        }
        String firstName = "USER";
        if (user.getFirstName() != null) {
            firstName = user.getFirstName().trim().toUpperCase().replaceAll("\\s+", "");
        }
        String namePart = firstName.length() >= 4 ? firstName.substring(0, 4) : firstName;

        String dobPart = "0101";
        if (user.getDob() != null) {
            dobPart = user.getDob().format(java.time.format.DateTimeFormatter.ofPattern("ddMM"));
        }

        return namePart + dobPart;
    }

    private static void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label != null ? label : "", labelFont));
        cell1.setPadding(6);
        cell1.setBackgroundColor(new java.awt.Color(245, 245, 245));
        cell1.setBorderColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(value != null ? value : "N/A", valueFont));
        cell2.setPadding(6);
        cell2.setBorderColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(cell2);
    }

    public static byte[] generateAccountDetailsPdf(com.bank.entity.User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Document document = new Document(PageSize.A4, 50, 50, 50, 50)) {

            PdfWriter writer = PdfWriter.getInstance(document, out);

            // Password protection
            String password = generatePdfPassword(user);
            writer.setEncryption(
                    password.getBytes(),
                    "admin123".getBytes(),
                    PdfWriter.ALLOW_PRINTING,
                    PdfWriter.STANDARD_ENCRYPTION_128);

            document.open();

            // Styles & Colors
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, java.awt.Color.DARK_GRAY);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, java.awt.Color.GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new java.awt.Color(194, 24, 139));
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, java.awt.Color.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 11, java.awt.Color.DARK_GRAY);
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, java.awt.Color.LIGHT_GRAY);

            // Header
            Paragraph title = new Paragraph("KUUMCA BANK OF INDIA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Official Account Confirmation Certificate", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Draw a separator line
            Paragraph line = new Paragraph(
                    "------------------------------------------------------------------------------------------------------------------------",
                    subtitleFont);
            line.setSpacingAfter(15);
            document.add(line);

            // Document reference details
            Paragraph refDetails = new Paragraph();
            refDetails.add(new Chunk("Date of Issue: ", labelFont));
            refDetails.add(new Chunk(
                    java.time.LocalDate.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    valueFont));
            refDetails.add(new Chunk(" | Ref No: ", labelFont));
            refDetails.add(new Chunk("REF-" + System.currentTimeMillis() % 100000000L, valueFont));
            refDetails.setSpacingAfter(25);
            document.add(refDetails);

            // Section: Personal Details
            Paragraph sec1 = new Paragraph("PERSONAL DETAILS", sectionFont);
            sec1.setSpacingAfter(10);
            document.add(sec1);

            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(100);
            table1.setSpacingBefore(5);
            table1.setSpacingAfter(20);

            addRow(table1, "First Name", user.getFirstName(), labelFont, valueFont);
            addRow(table1, "Last Name", user.getLastName(), labelFont, valueFont);

            String dobStr = "N/A";
            if (user.getDob() != null) {
                dobStr = user.getDob().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }
            addRow(table1, "Date of Birth", dobStr, labelFont, valueFont);
            addRow(table1, "Father's Name", user.getFatherName(), labelFont, valueFont);
            addRow(table1, "Email Address", user.getEmail(), labelFont, valueFont);
            addRow(table1, "Mobile Number", user.getMobile(), labelFont, valueFont);

            String addressLine1 = user.getAddressLine1() != null ? user.getAddressLine1() : "";
            String addressLine2 = user.getAddressLine2() != null ? user.getAddressLine2() : "";
            String city = user.getCity() != null ? user.getCity() : "";
            String state = user.getState() != null ? user.getState() : "";
            String country = user.getCountry() != null ? user.getCountry() : "";
            String pincode = user.getPincode() != null ? user.getPincode() : "";

            String fullAddress = String.format("%s, %s, %s, %s, %s, Pincode: %s",
                    addressLine1,
                    addressLine2,
                    city,
                    state,
                    country,
                    pincode).replaceAll(", ,", ",").trim();
            if (fullAddress.startsWith(",")) {
                fullAddress = fullAddress.substring(1).trim();
            }
            if (fullAddress.endsWith(",")) {
                fullAddress = fullAddress.substring(0, fullAddress.length() - 1).trim();
            }
            addRow(table1, "Permanent Address", fullAddress.isEmpty() ? "N/A" : fullAddress, labelFont, valueFont);

            document.add(table1);

            // Section: Banking Details
            Paragraph sec2 = new Paragraph("BANK ACCOUNT DETAILS", sectionFont);
            sec2.setSpacingAfter(10);
            document.add(sec2);

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);
            table2.setSpacingBefore(5);
            table2.setSpacingAfter(15);

            addRow(table2, "Account Number", user.getAccountNumber(), labelFont, valueFont);
            addRow(table2, "CIF Number", user.getCifNumber(), labelFont, valueFont);
            addRow(table2, "Account Type", user.getAccountType(), labelFont, valueFont);

            String aadhaar = user.getAadhaar();
            String formattedAadhaar = "N/A";
            if (aadhaar != null && !aadhaar.trim().isEmpty()) {
                aadhaar = aadhaar.trim();
                if (aadhaar.length() >= 4) {
                    formattedAadhaar = "XXXX-XXXX-" + aadhaar.substring(aadhaar.length() - 4);
                } else {
                    formattedAadhaar = aadhaar;
                }
            }
            addRow(table2, "Aadhaar Number", formattedAadhaar, labelFont, valueFont);
            addRow(table2, "PAN Number", user.getPan(), labelFont, valueFont);
            addRow(table2, "Nominee Name", user.getNomineeName(), labelFont, valueFont);

            Double initialDeposit = user.getInitialDeposit();
            double balance = (initialDeposit != null) ? initialDeposit : 0.0;
            String formattedBalance = String.format("Rs. %,.2f", balance);
            addRow(table2, "Current Balance / Initial Deposit", formattedBalance, labelFont, valueFont);

            document.add(table2);

            // Footer spacer
            Paragraph fSpacer = new Paragraph(" ");
            fSpacer.setSpacingAfter(40);
            document.add(fSpacer);

            // Signature info
            Paragraph signature = new Paragraph("Authorized Signatory\nKUUMCA Bank of India", labelFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

            Paragraph disclaimer = new Paragraph(
                    "\n\n* This is a password-protected, computer-generated document and requires no physical signature.",
                    footerFont);
            disclaimer.setAlignment(Element.ALIGN_CENTER);
            document.add(disclaimer);

            document.close();
            return out.toByteArray();

        } catch (com.lowagie.text.DocumentException | java.io.IOException e) {
            System.err.println("PDF generation error: " + e.getMessage());
            throw new RuntimeException("Account Details PDF Generation Failed: " + e.getMessage(), e);
        }
    }
}