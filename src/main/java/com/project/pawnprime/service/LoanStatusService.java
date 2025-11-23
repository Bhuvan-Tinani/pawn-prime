package com.project.pawnprime.service;

import com.project.pawnprime.dto.LoanStatusDTO;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.model.RepaymentTransaction;
import com.project.pawnprime.repo.LoanRepository;
import com.project.pawnprime.repo.RepaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoanStatusService {

    private final LoanRepository loanRepo;
    private final RepaymentTransactionRepository repaymentRepo;

    public LoanStatusService(LoanRepository loanRepo,
                             RepaymentTransactionRepository repaymentRepo) {
        this.loanRepo = loanRepo;
        this.repaymentRepo = repaymentRepo;
    }

    @Transactional(readOnly = true)
    public LoanStatusDTO getLoanStatus(Long loanId) {
        // 1. Fetch loan
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + loanId));

        // 2. Fetch all repayments for this loan
        List<RepaymentTransaction> repayments = repaymentRepo.findByLoanId(loanId);

        // 3. Sum principal paid so far
        double principalPaid = repayments.stream()
                .mapToDouble(rt -> rt.getPrincipalAmt() != null ? rt.getPrincipalAmt() : 0.0)
                .sum();

        // 4. Remaining principal
        double principalRemaining = (loan.getLoanVal() != null ? loan.getLoanVal() : 0.0) - principalPaid;
        if (principalRemaining < 0) {
            principalRemaining = 0.0;  // safety – don’t go negative
        }

        // 5. This month interest (simple interest on remaining principal)
        double annualRate = loan.getInterestRate() != null ? loan.getInterestRate() : 0.0;
        double thisMonthInterest = principalRemaining * annualRate / 100.0 / 12.0;

        // 6. Total PAID so far from repayment_transaction (sum of totalAmt)
        double totalPaid = repayments.stream()
                .mapToDouble(rt -> rt.getTotalAmt() != null ? rt.getTotalAmt() : 0.0)
                .sum();

        // 7. Build DTO
        LoanStatusDTO dto = new LoanStatusDTO();
        dto.setLoanId(loanId);
        dto.setTotalPrincipal(round2(principalRemaining));      // remaining principal
        dto.setTotalInterest(round2(thisMonthInterest));        // this month interest (or change as per your rule)
        dto.setTotalPaid(round2(totalPaid));                    // ✅ total paid from repayments

        return dto;
    }


    private Double round2(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
