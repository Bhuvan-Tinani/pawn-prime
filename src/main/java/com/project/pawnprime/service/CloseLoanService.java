package com.project.pawnprime.service;

import com.project.pawnprime.dto.CloseLoanDTO;
import com.project.pawnprime.model.CloseLoan;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.repo.CloseLoanRepository;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class CloseLoanService {
    private final CloseLoanRepository closeLoanRepository;
    private final LoanService loanService;

    public CloseLoanService(CloseLoanRepository closeLoanRepository, LoanService loanService) {
        this.closeLoanRepository = closeLoanRepository;
        this.loanService = loanService;
    }

    public CloseLoanDTO closeLoan(CloseLoanDTO dto) {
        // Fetch loan
        Loan loan = loanService.getLoanById(dto.getLoanId());

        // Update loan status
        loanService.changeLoanStatus(dto.getLoanId(), "closed");

        // Map DTO to entity
        CloseLoan closeLoan = new CloseLoan();
        closeLoan.setLoan(loan);
        closeLoan.setTotalPrincipal(dto.getTotalPrincipal());
        closeLoan.setTotalInterest(dto.getTotalInterest());
        closeLoan.setTotalPaid(dto.getTotalPaid());
        closeLoan.setClosureDate(dto.getClosureDate() != null ? dto.getClosureDate() : LocalDateTime.now());
        closeLoan.setClosedBy(dto.getClosedBy());
        closeLoan.setRemarks(dto.getRemarks());

        // Save entity
        CloseLoan saved = closeLoanRepository.save(closeLoan);

        // Map entity back to DTO
        CloseLoanDTO responseDTO = new CloseLoanDTO();
        responseDTO.setLoanId(saved.getLoan().getId());
        responseDTO.setTotalPrincipal(saved.getTotalPrincipal());
        responseDTO.setTotalInterest(saved.getTotalInterest());
        responseDTO.setTotalPaid(saved.getTotalPaid());
        responseDTO.setClosureDate(saved.getClosureDate());
        responseDTO.setClosedBy(saved.getClosedBy());
        responseDTO.setRemarks(saved.getRemarks());

        return responseDTO;
    }

}
