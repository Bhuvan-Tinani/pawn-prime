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

public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Create loan for a customer
    @PreAuthorize("hasRole('AGENT')")
    @PostMapping("/customer/{customerId}")
    public LoanDTO createLoan(@PathVariable Long customerId, @RequestBody LoanDTO loanDTO) {
    	loanDTO.setDate(LocalDate.now());
        // Always set today's date from server
        Loan loan = LoanMapper.toEntity(loanDTO); // DTO → Entity
        Loan savedLoan = loanService.createLoan(customerId, loan);

        return LoanMapper.toDTO(savedLoan);       // Entity → DTO
    }


    // Get all loans
    @PreAuthorize("hasRole('AGENT','ADMIN')")
    @GetMapping
    public List<LoanDTO> getAllLoans() {
        return loanService.getAllLoans()
                .stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get loans for a specific customer
    @PreAuthorize("hasRole('AGENT','ADMIN')")
    @GetMapping("/customer/{customerId}")
    public List<LoanDTO> getLoansByCustomer(@PathVariable Long customerId) {
        return loanService.getLoansByCustomer(customerId)
                .stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get loan by ID
    @PreAuthorize("hasRole('AGENT','ADMIN')")
    @GetMapping("/{loanId}")
    public LoanDTO getLoanById(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return LoanMapper.toDTO(loan);
    }

    // Update loan
    @PreAuthorize("hasRole('AGENT')")
    @PutMapping("/{loanId}")
    public LoanDTO updateLoan(@PathVariable Long loanId, @RequestBody LoanDTO loanDTO) {
        Loan loan = LoanMapper.toEntity(loanDTO);
        Loan updatedLoan = loanService.updateLoan(loanId, loan);
        return LoanMapper.toDTO(updatedLoan);
    }

    // Delete loan
    @PreAuthorize("hasRole('AGENT')")
    @DeleteMapping("/{loanId}")
    public String deleteLoan(@PathVariable Long loanId) {
        loanService.deleteLoan(loanId);
        return "Loan deleted successfully!";
    }
    
    @PreAuthorize("hasRole('AGENT','ADMIN')")
    @GetMapping("/{loanId}/schedule")
    public List<LoanScheduleDTO> getLoanSchedule(@PathVariable Long loanId) {
    	List<LoanScheduleDTO> schedule=loanService.getLoanSchedule(loanId);
    	return schedule;
    }

}
