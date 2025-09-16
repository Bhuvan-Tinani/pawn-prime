package com.project.pawnprime.repo;

import com.project.pawnprime.model.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long> {
    List<LoanTransaction> findByLoanId(Long loanId);
    List<LoanTransaction> findByCustomerId(Long customerId);
    List<LoanTransaction> findByAgentId(Long agentId);
}
