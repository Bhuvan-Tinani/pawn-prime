package com.project.pawnprime.security;

import com.project.pawnprime.model.Admin;
import com.project.pawnprime.model.Agent;
import com.project.pawnprime.repo.AdminRepository;
import com.project.pawnprime.repo.AgentRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final AgentRepository agentRepository;

    public CustomUserDetailsService(AdminRepository adminRepository, AgentRepository agentRepository) {
        this.adminRepository = adminRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        

        // First check Admin
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            return User.withUsername(admin.getUsername())
                    .password(admin.getPassword())
                    .roles(admin.getRole())   // e.g. "ADMIN"
                    .build();
        }

        // Then check Agent
        Agent agent = agentRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Agent not found with email: " + username));

        if (agent != null) {
            return User.withUsername(agent.getEmail())
                    .password(agent.getPassword())
                    .roles("AGENT")   // e.g. "AGENT"
                    .build();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
