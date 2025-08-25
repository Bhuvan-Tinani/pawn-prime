package com.project.pawnprime.service;

import org.springframework.stereotype.Service;
import com.project.pawnprime.model.Agent;
import com.project.pawnprime.repo.AgentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AgentService {

    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Agent createAgent(Agent agent) {
        if (agentRepository.existsByEmail(agent.getEmail())) {
            throw new RuntimeException("Agent with email already exists");
        }
        return agentRepository.save(agent);
    }

    public List<Agent> getAllAgents() {
        return agentRepository.findAll();
    }

    public Optional<Agent> getAgentById(Long id) {
        return agentRepository.findById(id);
    }

    public void deleteAgent(Long id) {
        agentRepository.deleteById(id);
    }

    public Agent updateAgent(Long id, Agent agentDetails) {
        Agent existing = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found with id " + id));

        existing.setName(agentDetails.getName());
        existing.setEmail(agentDetails.getEmail());
        existing.setPhoneNumber(agentDetails.getPhoneNumber());
        existing.setPassword(agentDetails.getPassword());

        return agentRepository.save(existing);
    }
    
    public Agent getAgentByEmail(String email) {
        return agentRepository.findByEmail(email).orElse(null);
    }
    
    public Agent validateAgentLogin(String email, String password) {
        return agentRepository.findByEmail(email)
                .filter(agent -> agent.getPassword().equals(password)) // can replace with BCrypt check
                .orElse(null);
    }

}
