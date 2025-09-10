package com.project.pawnprime.mapper;

import com.project.pawnprime.dto.AgentDTO;
import com.project.pawnprime.model.Agent;

public class AgentMapper {

    public static AgentDTO toDTO(Agent agent) {
        AgentDTO dto = new AgentDTO();
        dto.setId(agent.getId());
        dto.setName(agent.getName());
        dto.setEmail(agent.getEmail());
        dto.setPassword(agent.getPassword());
        dto.setPhoneNumber(agent.getPhoneNumber());
        dto.setStatus(agent.isStatus());
        return dto;
    }

    public static Agent toEntity(AgentDTO dto) {
        Agent agent = new Agent();
        agent.setId(dto.getId());
        agent.setName(dto.getName());
        agent.setEmail(dto.getEmail());
        agent.setPassword(dto.getPassword());
        agent.setPhoneNumber(dto.getPhoneNumber());
        agent.setStatus(dto.isStatus());
        return agent;
    }
}
