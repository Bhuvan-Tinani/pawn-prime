package com.project.pawnprime.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.pawnprime.model.CloseLoan;

public interface CloseLoanRepository extends JpaRepository<CloseLoan, Long> {
	Optional<CloseLoan> findByLoanId(Long loanId);
}
