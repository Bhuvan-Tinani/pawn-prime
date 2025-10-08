package com.project.pawnprime.service;

import com.project.pawnprime.dto.transaction.*;
import com.project.pawnprime.model.*;
import com.project.pawnprime.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepaymentTransactionService {

    private final RepaymentTransactionRepository repaymentRepo;
    private final LoanRepository loanRepo;
    private final AgentRepository agentRepo;

    public RepaymentTransactionService(RepaymentTransactionRepository repaymentRepo,
                                       LoanRepository loanRepo,
                                       AgentRepository agentRepo) {
        this.repaymentRepo = repaymentRepo;
        this.loanRepo = loanRepo;
        this.agentRepo = agentRepo;
    }

    public RepaymentTransaction addRepayment(RepaymentTransactionDTO dto) {
        Loan loan = loanRepo.findById(dto.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        Agent agent = null;
        if (dto.getType() == PaymentType.CASH && dto.getAgentId() != null) {
            agent = agentRepo.findById(dto.getAgentId())
                    .orElseThrow(() -> new RuntimeException("Agent not found"));
        }

        RepaymentTransaction txn = new RepaymentTransaction();
        txn.setLoan(loan);
        txn.setAgent(agent);
        txn.setPrincipalAmt(dto.getPrincipalAmt());
        txn.setInterestAmt(dto.getInterestAmt());
        txn.setTotalAmt(dto.getPrincipalAmt() + dto.getInterestAmt());
        txn.setDate(dto.getDate()); // scheduled/due date can be set differently
        txn.setPaidDate(LocalDateTime.now());
        txn.setType(dto.getType());

        return repaymentRepo.save(txn);
    }

    public List<RepaymentTransaction> getRepaymentsByLoan(Long loanId) {
        return repaymentRepo.findByLoanId(loanId);
    }
    
    public List<RepaymentTransaction> getAllRepayments() {
        return repaymentRepo.findAll();
    }
    
    public long getRepaymentCountForLoan(Long loanId) {
        return repaymentRepo.findByLoanId(loanId).size();
    }
    
    public List<RepaymentTransaction> getRepaymentsByAgent(Long agentId) {
        return repaymentRepo.findByAgentId(agentId);
    }

}
