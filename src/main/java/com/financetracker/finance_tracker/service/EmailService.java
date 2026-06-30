package com.financetracker.finance_tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    private void sendEmail(String to, String subject, String html) {

        String url = "https://api.resend.com/emails";

        Map<String, Object> body = new HashMap<>();
        body.put("from", fromEmail);
        body.put("to", new String[]{to});
        body.put("subject", subject);
        body.put("html", html);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("Email failed: " + e.getMessage());
        }
    }

    // ================= OTP EMAIL =================
    public void sendOtpEmail(String toEmail, String otp) {

        String html = """
                <div style="font-family:Arial;padding:10px">
                    <h2>🔐 OTP Verification</h2>
                    <p>Your OTP is:</p>
                    <h1 style="color:#2d6cdf">%s</h1>
                    <p>This OTP is valid for 10 minutes.</p>
                </div>
                """.formatted(otp);

        sendEmail(toEmail, "Your OTP for Finance Tracker", html);
    }

    // ================= WELCOME EMAIL =================
    public void sendWelcomeEmail(String toEmail, String username) {

        String html = """
                <h2>🎉 Welcome %s!</h2>
                <p>Welcome to Finance Tracker 💰</p>
                <ul>
                    <li>Track income & expenses</li>
                    <li>Monitor budget limits</li>
                    <li>Get alerts instantly</li>
                </ul>
                """.formatted(username);

        sendEmail(toEmail, "Welcome to Finance Tracker", html);
    }

    // ================= BUDGET ALERT =================
    public void sendBudgetAlert(String toEmail,
                                String category,
                                Double spent,
                                Double limit) {

        String html = """
                <h2>⚠️ Budget Alert</h2>
                <p>Category: <b>%s</b></p>
                <p>Spent: ₹%.2f</p>
                <p>Limit: ₹%.2f</p>
                <h3 style="color:red">Over: ₹%.2f</h3>
                """.formatted(category, spent, limit, (spent - limit));

        sendEmail(toEmail,
                "Budget Alert - " + category,
                html);
    }
}
