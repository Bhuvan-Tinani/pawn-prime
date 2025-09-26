package com.project.pawnprime.controller;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.pawnprime.dto.adminDTO.LoginRequestAdmin;
import com.project.pawnprime.dto.adminDTO.LoginResponseAdmin;
import com.project.pawnprime.dto.agentDTO.LoginRequestAgentDTO;
import com.project.pawnprime.dto.agentDTO.LoginResponseAgentDTO;
import com.project.pawnprime.model.Admin;
import com.project.pawnprime.model.Agent;
import com.project.pawnprime.security.JwtUtil;
import com.project.pawnprime.service.AdminService;
import com.project.pawnprime.service.AgentService;

import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    // Simple in-memory storage for OTP (use Redis or DB in production)
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(1000 + random.nextInt(9000));
    }

    // 🔹 Admin Login
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequestAdmin request) {
        System.out.print("hello");
        Admin admin = adminService.validateAdminLogin(request.getUsername(), request.getPassword());

        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole());
        return ResponseEntity.ok(new LoginResponseAdmin(token, admin.getUsername(), admin.getRole()));
    }

    // 🔹 Agent Login - Sends OTP after credential validation
    @PostMapping("/agent/login")
    public ResponseEntity<?> agentLogin(@RequestBody LoginRequestAgentDTO request) {
        Agent agent = agentService.validateAgentLogin(request.getUsername(), request.getPassword());

        if (agent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
        if (!agent.isStatus()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Agent is Blocked"));
        }

        // Generate an	d store OTP
        String otp = generateOtp();
        otpStorage.put(agent.getEmail(), otp);

        // Send OTP email
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(agent.getEmail());
            helper.setSubject("Pawn Prime - Your OTP for Login");
            helper.setText(
                "<h2>Pawn Prime Agent Login</h2>" +
                "<p>Your One-Time Password (OTP) for login is: <b>" + otp + "</b></p>" +
                "<p>This OTP is valid for 5 minutes. Please do not share it with anyone.</p>" +
                "<p>If you did not request this OTP, please ignore this email or contact support.</p>" +
                "<p>Best regards,<br>Pawn Prime Team</p>",
                true
            );
            mailSender.send(message);
            System.out.println("OTP sent to: " + agent.getEmail() + ", OTP: " + otp);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send OTP email"));
        }

        // Return success without token (OTP sent)
        return ResponseEntity.ok(Map.of("success", true, "message", "OTP sent to your email"));
    }

    // 🔹 Agent Verify OTP
    @PostMapping("/agent/verify-otp")
    public ResponseEntity<?> agentVerifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String inputOtp = request.get("otp");

        if (email == null || inputOtp == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Email and OTP are required"));
        }

        String storedOtp = otpStorage.get(email);
        if (storedOtp == null || !storedOtp.equals(inputOtp)) {
            // Remove invalid attempt if exists
            if (storedOtp != null) {
                otpStorage.remove(email);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid OTP"));
        }

        // OTP valid, remove from storage
        otpStorage.remove(email);

        // Fetch agent by email (assumes getAgentByEmail exists in AgentService)
        Agent agent = agentService.getAgentByEmail(email);
        if (agent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Agent not found"));
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(email, "AGENT");
        return ResponseEntity.ok(new LoginResponseAgentDTO(token, email, "AGENT", agent.getId()));
    }
}