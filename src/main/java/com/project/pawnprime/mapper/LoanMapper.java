package com.project.pawnprime.mapper;

import com.project.pawnprime.dto.loanDTO.LoanDTO;
import com.project.pawnprime.model.Loan;

public class LoanMapper {

    public static LoanDTO toDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setDate(loan.getDate());
        dto.setTypeOrnament(loan.getTypeOrnament());
        dto.setNetGram(loan.getNetGram());
        dto.setPurityPercent(loan.getPurityPercent());
        dto.setPurityGram(loan.getPurityGram());
        dto.setValue(loan.getValue());
        dto.setLoanVal(loan.getLoanVal());
        dto.setInterestRate(loan.getInterestRate());
        dto.setDuration(loan.getDuration());
        dto.setCustomerId(loan.getCustomer().getId());
        return dto;
    }

    public static Loan toEntity(LoanDTO dto) {
        Loan loan = new Loan();
        loan.setId(dto.getId());
        loan.setDate(dto.getDate());
        loan.setTypeOrnament(dto.getTypeOrnament());
        loan.setNetGram(dto.getNetGram());
        loan.setPurityPercent(dto.getPurityPercent());
        loan.setPurityGram(dto.getPurityGram());
        loan.setValue(dto.getValue());
        loan.setLoanVal(dto.getLoanVal());
        loan.setInterestRate(dto.getInterestRate());
        loan.setDuration(dto.getDuration());
        // ⚠ Customer relation will be set in LoanService while saving
        return loan;
    }
}
