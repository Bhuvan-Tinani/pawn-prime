package com.project.pawnprime.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.pawnprime.dto.AdminDTO;
import com.project.pawnprime.dto.adminDTO.AdminRequestDTO;
import com.project.pawnprime.mapper.AdminMapper;
import com.project.pawnprime.model.Admin;
import com.project.pawnprime.service.AdminService;

@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Create Admin
    @PostMapping
    public ResponseEntity<AdminDTO> createAdmin(@RequestBody AdminRequestDTO adminRequestDTO) {
    	System.out.print("hello");
        Admin admin = AdminMapper.toEntity(adminRequestDTO);
        return ResponseEntity.ok(AdminMapper.toDTO(adminService.saveAdmin(admin)));
    }

    // Get All Admins
    @GetMapping
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        return ResponseEntity.ok(
                adminService.getAllAdmins().stream()
                        .map(AdminMapper::toDTO)
                        .toList()
        );
    }

    // Get Admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id)
                .map(AdminMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update Admin
    @PutMapping("/{id}")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminRequestDTO adminRequestDTO) {
        return adminService.getAdminById(id)
                .map(existingAdmin -> {
                    existingAdmin.setUsername(adminRequestDTO.getUsername());
                    existingAdmin.setPassword(adminRequestDTO.getPassword());
                    existingAdmin.setEmail(adminRequestDTO.getEmail());
                    existingAdmin.setRole(adminRequestDTO.getRole());
                    existingAdmin.setActive(adminRequestDTO.isActive());
                    return ResponseEntity.ok(AdminMapper.toDTO(adminService.saveAdmin(existingAdmin)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete Admin
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Admin deleted successfully");
    }
}
