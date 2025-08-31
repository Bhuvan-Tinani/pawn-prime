package com.project.pawnprime.controller;

import com.project.pawnprime.dto.loanDTO.LoanDTO;
import com.project.pawnprime.dto.loanDTO.LoanScheduleDTO;
import com.project.pawnprime.mapper.LoanMapper;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.service.LoanService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('AGENT')")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Create loan for a customer
    @PostMapping("/customer/{customerId}")
    public LoanDTO createLoan(@PathVariable Long customerId, @RequestBody LoanDTO loanDTO) {
    	loanDTO.setDate(LocalDate.now());
        // Always set today's date from server
        Loan loan = LoanMapper.toEntity(loanDTO); // DTO → Entity
        Loan savedLoan = loanService.createLoan(customerId, loan);

        return LoanMapper.toDTO(savedLoan);       // Entity → DTO
    }


    // Get all loans
    @GetMapping
    public List<LoanDTO> getAllLoans() {
        return loanService.getAllLoans()
                .stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get loans for a specific customer
    @GetMapping("/customer/{customerId}")
    public List<LoanDTO> getLoansByCustomer(@PathVariable Long customerId) {
        return loanService.getLoansByCustomer(customerId)
                .stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get loan by ID
    @GetMapping("/{loanId}")
    public LoanDTO getLoanById(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return LoanMapper.toDTO(loan);
    }

    // Update loan
    @PutMapping("/{loanId}")
    public LoanDTO updateLoan(@PathVariable Long loanId, @RequestBody LoanDTO loanDTO) {
        Loan loan = LoanMapper.toEntity(loanDTO);
        Loan updatedLoan = loanService.updateLoan(loanId, loan);
        return LoanMapper.toDTO(updatedLoan);
    }

    // Delete loan
    @DeleteMapping("/{loanId}")
    public String deleteLoan(@PathVariable Long loanId) {
        loanService.deleteLoan(loanId);
        return "Loan deleted successfully!";
    }
    
    @GetMapping("/{loanId}/schedule")
    public List<LoanScheduleDTO> getLoanSchedule(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);

        double principal = loan.getLoanVal();
        double annualRate = loan.getInterestRate(); // e.g., 4%
        int months = loan.getDuration();
        LocalDate startDate = loan.getDate();

        // simple interest total
        double totalInterest = (principal * annualRate * (months / 12.0)) / 100;
        double totalPayable = principal + totalInterest;

        // monthly breakup
        double monthlyPrincipal = principal / months;
        double monthlyInterest = totalInterest / months;
        double monthlyInstallment = monthlyPrincipal + monthlyInterest;

        List<LoanScheduleDTO> schedule = new ArrayList<>();
        double balance = totalPayable;

        for (int i = 1; i <= months; i++) {
            balance -= monthlyInstallment;

            LoanScheduleDTO dto = new LoanScheduleDTO();
            dto.setInstallmentNo(i);
            dto.setLoanId(loan.getId());
            dto.setPrincipalAmount(Math.round(principal * 100.0) / 100.0);
            dto.setInterestAmount(Math.round(monthlyInterest * 100.0) / 100.0);
            dto.setTotalInstallment(Math.round(monthlyInstallment * 100.0) / 100.0);
            dto.setRemainingAmount(Math.max(0, Math.round(balance * 100.0) / 100.0));
            dto.setDate(startDate.plusMonths(i));

            schedule.add(dto);
        }

        return schedule;
    }

}
