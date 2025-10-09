package com.project.pawnprime.repo;

import com.project.pawnprime.model.RepaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepaymentTransactionRepository extends JpaRepository<RepaymentTransaction, Long> {
    List<RepaymentTransaction> findByLoanId(Long loanId);
    List<RepaymentTransaction> findByAgentId(Long agentId);
}
