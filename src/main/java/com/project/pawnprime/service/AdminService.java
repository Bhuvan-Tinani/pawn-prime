package com.project.pawnprime.service;

import com.project.pawnprime.model.Admin;
import com.project.pawnprime.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // 🔹 Create or Update Admin
    public Admin saveAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // 🔹 Get all Admins
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // 🔹 Get Admin by ID
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    // 🔹 Delete Admin
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    // 🔹 Find by Username
    public Admin getAdminByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    // 🔹 Find by Email
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
    
    public Admin validateAdminLogin(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            return admin;
        }
        return null;
    }
}

