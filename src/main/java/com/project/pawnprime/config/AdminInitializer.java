package com.project.pawnprime.config;

import com.project.pawnprime.model.Admin;
import com.project.pawnprime.repo.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(AdminRepository adminRepository) {
        return args -> {
        	
            if (adminRepository.findByUsername("admin")==null) {
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword("admin123"); // 🔹 store hashed password in real apps
                admin.setEmail("admin@pawnprime.com");
                admin.setRole("ADMIN");	
                admin.setActive(true);

                adminRepository.save(admin);
                System.out.println("✅ Default admin user created: username=admin, password=admin123");
            } else {
                System.out.println("ℹ️ Default admin already exists. Skipping creation.");
            }
        };
    }
}
