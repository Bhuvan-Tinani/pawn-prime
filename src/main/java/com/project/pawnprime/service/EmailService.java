package com.project.pawnprime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail; // fetched from application.properties

    public void sendCredentials(String toEmail, String name, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Agent Account Credentials");
        message.setText("Hello " + name + ",\n\n" +
                "Your agent account has been created successfully.\n\n" +
                "Email: " + toEmail + "\n" +
                "Password: " + password + "\n\n" +
                "Please login and change your password after first login.\n\n" +
                "Regards,\nPawnPrime Team\n\n" +
                "---------------------------------------------\n" +
                "CONFIDENTIALITY NOTICE:\n" +
                "This email contains confidential login credentials. " +
                "Do not share this information with anyone. " +
                "If you are not the intended recipient, please delete this email immediately.\n" +
                "---------------------------------------------");

        mailSender.send(message);
    }
}
