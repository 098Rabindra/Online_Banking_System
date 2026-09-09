package com.bank.util;

public class EmailTemplateUtil {

    private EmailTemplateUtil() {
    }

    public static String buildHtmlOtpEmail(String otp, String title, String description) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>OTP Verification</title>
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Arial, sans-serif; background-color: #f4f7f6; color: #333333;">
                <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%%" style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); overflow: hidden;">
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #002855, #004e92); padding: 30px 40px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 26px; font-weight: 700; letter-spacing: 1px;">KBI Bank</h1>
                            <p style="color: #dbe7ff; margin: 5px 0 0 0; font-size: 14px; font-weight: 500; text-transform: uppercase; letter-spacing: 1px;">Secure Online Banking</p>
                        </td>
                    </tr>
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 40px 30px 40px;">
                            <h2 style="color: #002855; margin-top: 0; margin-bottom: 20px; font-size: 20px; font-weight: 600;">%s</h2>
                            <p style="font-size: 15px; line-height: 1.6; color: #555555; margin-bottom: 25px;">
                                Dear Customer,<br><br>
                                You have requested a one-time password for <strong>%s</strong>. Use the secure OTP code below to proceed:
                            </p>
                            <!-- OTP Box -->
                            <div style="background-color: #eef5ff; border: 2px dashed #0056b3; border-radius: 10px; padding: 20px; text-align: center; margin-bottom: 30px;">
                                <span style="font-size: 32px; font-weight: 700; letter-spacing: 8px; color: #0056b3; font-family: 'Courier New', monospace;">%s</span>
                            </div>
                            <!-- Warning / Info -->
                            <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #fff8e1; border-left: 4px solid #ffb300; border-radius: 4px; margin-bottom: 25px;">
                                <tr>
                                    <td style="padding: 12px 15px;">
                                        <p style="margin: 0; font-size: 13px; line-height: 1.5; color: #b78103; font-weight: 500;">
                                            <strong>Important Security Warning:</strong><br>
                                            • This OTP is valid for <strong>5 minutes</strong>.<br>
                                            • Do not share this OTP, password, or any confidential details with anyone. KBI Bank representatives will never ask for your OTP.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                            <p style="font-size: 14px; line-height: 1.6; color: #777777; margin-bottom: 0;">
                                If you did not request this OTP, please contact our 24/7 customer support immediately.
                            </p>
                        </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 25px 40px; text-align: center; border-top: 1px solid #eeeeee;">
                            <p style="margin: 0; font-size: 12px; color: #888888; line-height: 1.5;">
                                &copy; 2026 KBI Bank. All rights reserved.<br>
                                This is an automated system email. Please do not reply directly to this mail.
                            </p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(title, description, otp);
    }


    public static String buildOtpEmail(
            String customerName,
            String otp) {

        return """
                Dear %s,

                Welcome to Online Banking.

                Your One Time Password (OTP) is:

                %s

                This OTP is valid for 5 minutes.

                Do not share this OTP with anyone.

                Regards,
                Online Banking Team
                """
                .formatted(customerName, otp);
    }

    public static String buildTransactionEmail(
            String accountNumber,
            String amount,
            String transactionType) {

        return """
                Transaction Alert

                Account Number : %s
                Transaction Type : %s
                Amount : ₹%s

                Thank You,
                Online Banking Team
                """
                .formatted(
                        accountNumber,
                        transactionType,
                        amount);
    }
}