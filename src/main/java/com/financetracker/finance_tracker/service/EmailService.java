package com.financetracker.finance_tracker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ── Budget Alert Email ────────────────────
    public void sendBudgetAlert(String toEmail,
                                String category,
                                Double spent,
                                Double limit) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("⚠️ Budget Alert - " + category + " limit crossed!");
            message.setText(
                    "Hi!\n\n" +
                            "Your spending on " + category + " has crossed your limit.\n\n" +
                            "Spent : ₹" + String.format("%.2f", spent) + "\n" +
                            "Limit : ₹" + String.format("%.2f", limit) + "\n" +
                            "Over  : ₹" + String.format("%.2f", spent - limit) + "\n\n" +
                            "Please review your expenses!\n\n" +
                            "— Finance Tracker App"
            );
            mailSender.send(message);
            System.out.println("✅ Budget alert sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Budget email failed: " + e.getMessage());
        }
    }


    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("🔐 Your OTP for Finance Tracker");
            message.setText(
                    "Hi!\n\n" +
                            "Your OTP for registration is:\n\n" +
                            "👉  " + otp + "  👈\n\n" +
                            "This OTP is valid for 10 minutes.\n" +
                            "Do not share it with anyone.\n\n" +
                            "— Finance Tracker Team"
            );
            mailSender.send(message);
            System.out.println("✅ OTP sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ OTP email failed: " + e.getMessage());
        }
    }



    // ── Welcome Email ─────────────────────────
    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("🎉 Welcome to Finance Tracker :) , " + username + "!");
            message.setText(
                    "Hi " + username + "!\n\n" +
                            "Welcome to Finance Tracker! 💰\n\n" +
                            "You can now:\n" +
                            "✅ Track income and expenses\n" +
                            "✅ See your balance in real time\n" +
                            "✅ Get alerts when you overspend\n\n" +
                            "Start tracking: http://localhost:5173\n\n" +
                            "— Finance Tracker Team"
            );
            mailSender.send(message);
            System.out.println("✅ Welcome email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Welcome email failed: " + e.getMessage());
        }
    }
}