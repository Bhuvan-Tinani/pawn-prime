package com.project.pawnprime.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AgentService agentService;

    @Autowired
    private JwtUtil jwtUtil;

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

    // 🔹 Agent Login
    @PostMapping("/agent/login")
    public ResponseEntity<?> agentLogin(@RequestBody LoginRequestAgentDTO request) {
        Agent agent = agentService.validateAgentLogin(request.getUsername(), request.getPassword());

        if (agent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        String token = jwtUtil.generateToken(agent.getEmail(), "AGENT");
        return ResponseEntity.ok(new LoginResponseAgentDTO(token, agent.getEmail(), "AGENT",agent.getId()));
    }
}

