package com.project.pawnprime.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.pawnprime.model.CloseLoan;

public interface CloseLoanRepository extends JpaRepository<CloseLoan, Long> {
}
