package com.project.pawnprime.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {


    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String whatsappNumber;
    
    @Value("${twilio.sms.number}")
    private String smsNumber;

    private final ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
    
    public String generateOtp() {
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    public boolean sendOtp(String phoneNumber) {
        try {
            String otp = generateOtp();
            phoneNumber = phoneNumber.trim();
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+" + phoneNumber;      // add + if missing
            }
            otpStore.put(phoneNumber, otp); // store OTP temporarily
            System.out.println("OTP sent to: " + phoneNumber + " OTP: " + otp);
            
            Message message = Message.creator(
            	    new PhoneNumber(phoneNumber),    // ✅ SMS, not WhatsApp
            	    new PhoneNumber(smsNumber),   // your SMS-enabled Twilio number
            	    "Your OTP is: " + otp
            	).create();

            
//            Message message=Message.creator(
//                    new PhoneNumber("whatsapp:" + phoneNumber),
//                    new PhoneNumber(whatsappNumber),
//                    "Your OTP is: " + otp
//            ).create();
            System.out.println("OTP generated: " + otp);
            System.out.println("Attempting to send OTP to: " + phoneNumber);
            System.out.println("Twilio SID: " + message.getSid());
            System.out.println("Twilio Status: " + message.getStatus()); // queued/sent/failed
            System.out.println("Twilio To: " + message.getTo());
            System.out.println("Twilio From: " + message.getFrom());
            System.out.println("Twilio Error Code: " + message.getErrorCode());
            System.out.println("Twilio Error Message: " + message.getErrorMessage());

            System.out.println("OTP sent to: " + phoneNumber + " OTP: " + otp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        System.out.println("Verifying OTP...");
        String Number=phoneNumber;
        System.out.println("Input phoneNumber: '" + Number + "'");
        System.out.println("Input OTP: '" + otp + "'");
        
        String storedOtp = otpStore.get(Number);
        System.out.println("Stored OTP for this number: '" + storedOtp + "'");

        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStore.remove(phoneNumber);
            System.out.println("OTP matched. Verification successful!");
            return true;
        }

        System.out.println("OTP verification failed!");
        return false;
    }

}
