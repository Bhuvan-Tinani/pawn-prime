package com.project.pawnprime.security;

import com.project.pawnprime.model.Admin;
import com.project.pawnprime.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Build Spring Security UserDetails object
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword()) // password should ideally be hashed
                .roles(admin.getRole())        // role stored in DB
                .build();
    }
}
