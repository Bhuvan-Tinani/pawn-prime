package com.project.pawnprime.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.project.pawnprime.dto.AgentDTO;
import com.project.pawnprime.mapper.AgentMapper;
import com.project.pawnprime.model.Agent;
import com.project.pawnprime.service.AgentService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public ResponseEntity<AgentDTO> create(@RequestBody AgentDTO agentDTO) {
    	
        Agent agent = AgentMapper.toEntity(agentDTO);
        Agent saved = agentService.createAgent(agent);
        return ResponseEntity.ok(AgentMapper.toDTO(saved));
    }

    @GetMapping
    public List<AgentDTO> getAll() {
        return agentService.getAllAgents()
                .stream()
                .map(AgentMapper::toDTO)
                .collect(Collectors.toList());
    } 

    @GetMapping("/{id}")
    public ResponseEntity<AgentDTO> getById(@PathVariable Long id) {
        return agentService.getAgentById(id)
                .map(AgentMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentDTO> update(@PathVariable Long id, @RequestBody AgentDTO dto) {
        Agent agent = AgentMapper.toEntity(dto);
        Agent updated = agentService.updateAgent(id, agent);
        return ResponseEntity.ok(AgentMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/setBlock/{id}")
    public ResponseEntity<AgentDTO> setBlockStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {

        boolean status = body.get("status"); // extract from JSON body
        Agent updatedAgent = agentService.blockAgent(id, status);
        return ResponseEntity.ok(AgentMapper.toDTO(updatedAgent));
    }

}



