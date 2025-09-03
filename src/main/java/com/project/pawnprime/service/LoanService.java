package com.project.pawnprime.service;

import com.project.pawnprime.model.Loan;
import com.project.pawnprime.dto.loanDTO.LoanScheduleDTO;
import com.project.pawnprime.model.Customer;
import com.project.pawnprime.repo.LoanRepository;
import com.project.pawnprime.repo.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;

    public LoanService(LoanRepository loanRepository, CustomerRepository customerRepository) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
    }

    public Loan createLoan(Long customerId, Loan loan) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));

        loan.setCustomer(customer);
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getLoansByCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public Loan getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + loanId));
    }

    public Loan updateLoan(Long loanId, Loan updatedLoan) {
        Loan loan = getLoanById(loanId);

        loan.setDate(updatedLoan.getDate());
        loan.setTypeOrnament(updatedLoan.getTypeOrnament());
        loan.setNetGram(updatedLoan.getNetGram());
        loan.setPurityPercent(updatedLoan.getPurityPercent());
        loan.setPurityGram(updatedLoan.getPurityGram());
        loan.setValue(updatedLoan.getValue());
        loan.setLoanVal(updatedLoan.getLoanVal());
        loan.setInterestRate(updatedLoan.getInterestRate());
        loan.setDuration(updatedLoan.getDuration());

        return loanRepository.save(loan);
    }

    public void deleteLoan(Long loanId) {
        loanRepository.deleteById(loanId);
    }
    
    public List<LoanScheduleDTO> getLoanSchedule(@PathVariable Long loanId){
    	Loan loan = getLoanById(loanId);

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
