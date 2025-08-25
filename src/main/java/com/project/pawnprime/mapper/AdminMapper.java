package com.project.pawnprime.mapper;

import com.project.pawnprime.dto.AdminDTO;
import com.project.pawnprime.dto.adminDTO.AdminRequestDTO;
import com.project.pawnprime.model.Admin;

public class AdminMapper {

    public static AdminDTO toDTO(Admin admin) {
        return new AdminDTO(
                admin.getId(),
                admin.getUsername(),
                admin.getEmail(),
                admin.getRole(),
                admin.isActive()
        );
    }

    public static Admin toEntity(AdminRequestDTO dto) {
        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(dto.getPassword()); // later we can hash this
        admin.setEmail(dto.getEmail());
        admin.setRole(dto.getRole());
        admin.setActive(dto.isActive());
        return admin;
    }
}
