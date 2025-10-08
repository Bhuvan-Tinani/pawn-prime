package com.project.pawnprime.repo;

import com.project.pawnprime.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Find loans by Customer ID
    List<Loan> findByCustomerId(Long customerId);
    List<Loan> findByAgentId(Long agentId);
    List<Loan> findByLoanStatus(String loanStatus);
    Loan findByLoanStatusAndId(String loanStatus, Long id);
    List<Loan> findByCustomerAadharNo(String aadharNo);

}
