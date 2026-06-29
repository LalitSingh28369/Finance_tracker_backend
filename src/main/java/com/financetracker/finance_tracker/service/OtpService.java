package com.financetracker.finance_tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    // Store OTPs in memory
    // Key = email, Value = OTP
    private final Map<String, String> otpStore = new HashMap<>();

    @Autowired
    private EmailService emailService;

    // Generate and send OTP
    public void generateAndSendOtp(String email) {
        // Generate 6 digit OTP
        String otp = String.format("%06d",
                new Random().nextInt(999999));

        // Store it
        otpStore.put(email, otp);

        System.out.println("OTP for " + email + " is: " + otp);

        // Send via email
        emailService.sendOtpEmail(email, otp);
    }

    // Verify OTP
    public boolean verifyOtp(String email, String otp) {
        String stored = otpStore.get(email);
        if (stored != null && stored.equals(otp)) {
            otpStore.remove(email); // delete after use
            return true;
        }
        return false;
    }
}