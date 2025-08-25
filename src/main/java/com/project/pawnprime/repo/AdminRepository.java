package com.project.pawnprime.repo;

import com.project.pawnprime.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Custom query methods (Spring Data JPA will auto-implement these)
    Admin findByUsername(String username);

    Admin findByEmail(String email);
}
