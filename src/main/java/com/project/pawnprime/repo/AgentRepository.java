package com.project.pawnprime.repo;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pawnprime.model.Agent;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    boolean existsByEmail(String email);
    Optional<Agent> findByEmail(String email);
}
