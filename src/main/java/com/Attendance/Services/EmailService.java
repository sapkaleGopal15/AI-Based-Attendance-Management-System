package com.Attendance.Services;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender gmailSender;

    private final String ADMIN_EMAIL_ID = "sapkalegopal29@gmail.com";

    private final SecureRandom secureRandom = new SecureRandom();

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    public void sendOTPEmail(String email) {

        email = email.trim().toLowerCase();

        String otp = String.format("%06d",
                secureRandom.nextInt(1_000_000));

        otpStore.put(
                email,
                new OtpData(
                        otp,
                        Instant.now()
                )
        );

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(ADMIN_EMAIL_ID);
        message.setTo(email);

        message.setSubject(
                "Attendance System - OTP Verification"
        );

        message.setText(
                "Dear User,\n\n" +

                "Your OTP for Attendance Management System is: "
                + otp + "\n\n" +

                "This OTP is valid for 5 minutes.\n\n" +

                "Please do not share this OTP with anyone.\n\n" +

                "Regards,\n" +
                "AI-Based Attendance Management System"
        );

        gmailSender.send(message);
    }

    public String verifyOTP(String email, String enteredOTP) {

        if (email == null || enteredOTP == null) {
            return "Email or OTP cannot be null.";
        }

        email = email.trim().toLowerCase();
        enteredOTP = enteredOTP.trim();


        if (!enteredOTP.matches("\\d{6}")) {
            return "Invalid OTP format.";
        }


        OtpData otpData = otpStore.get(email);


        if (otpData == null) {
            return "No OTP found for the provided email. Please request a new OTP.";
        }

        Instant currentTime = Instant.now();

        long elapsedSeconds =
                Duration.between(
                        otpData.generatedAt(),
                        currentTime
                ).getSeconds();


        if (elapsedSeconds > 300) {

            otpStore.remove(email);

            return "OTP has expired. Please request a new OTP.";
        }

        if (!otpData.otp().equals(enteredOTP)) {
            return "Invalid OTP. Please try again.";
        }

        otpStore.remove(email);

        return "OTP verified successfully.";
    }

    private record OtpData(
            String otp,
            Instant generatedAt
    ) {
    }
}