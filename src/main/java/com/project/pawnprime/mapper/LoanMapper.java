package com.project.pawnprime.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.project.pawnprime.dto.customerDTO.CustomerDTO;
import com.project.pawnprime.dto.loanDTO.LoanDTO;
import com.project.pawnprime.dto.loanDTO.LoanRequestStatus;
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
        dto.setAgentId(loan.getId());
        dto.setLoanStatus(loan.getLoanStatus());
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
        loan.setLoanStatus(dto.getLoanStatus());
        // ⚠ Customer relation will be set in LoanService while saving
        return loan;
    }
    
    public static List<LoanDTO> toDTOList(List<Loan> loans) {
        return loans.stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public static List<LoanRequestStatus> toAdminLoanDTOList(List<Loan> loans){
    	List<LoanRequestStatus> list=new ArrayList<>(loans.size());
    	for(Loan loan:loans) {
    		LoanRequestStatus loanRequestAdmin=new LoanRequestStatus();
    		loanRequestAdmin.setLoanId(loan.getId());
    		loanRequestAdmin.setAgentId(loan.getAgent().getId());
    		loanRequestAdmin.setAgentName(loan.getAgent().getName());
    		loanRequestAdmin.setDate(loan.getDate());
    		loanRequestAdmin.setDuration(loan.getDuration());
    		loanRequestAdmin.setInterestRate(loan.getInterestRate());
    		loanRequestAdmin.setLoanStatus(loan.getLoanStatus());
    		loanRequestAdmin.setLoanVal(loan.getLoanVal());
    		loanRequestAdmin.setNetGram(loan.getNetGram());
    		loanRequestAdmin.setPurityGram(loan.getPurityGram());
    		loanRequestAdmin.setPurityPercent(loan.getPurityPercent());
    		loanRequestAdmin.setTypeOrnament(loan.getTypeOrnament());
    		loanRequestAdmin.setValue(loan.getValue());
    		CustomerDTO customer=new CustomerDTO();
    		customer.setId(loan.getCustomer().getId());
    		customer.setAadharNo(loan.getCustomer().getAadharNo());
    		customer.setDob(loan.getCustomer().getDob());
    		customer.setFirstName(loan.getCustomer().getFirstName());
    		customer.setMiddleName(loan.getCustomer().getMiddleName());
    		customer.setLastName(loan.getCustomer().getLastName());
    		customer.setMobile(loan.getCustomer().getMobile());
    		loanRequestAdmin.setCustomer(customer);
    		list.add(loanRequestAdmin);
    	}
    	return list;
    }
}
