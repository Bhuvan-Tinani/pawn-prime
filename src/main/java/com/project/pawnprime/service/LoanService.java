package com.project.pawnprime.service;

import com.project.pawnprime.model.Loan;
import com.project.pawnprime.model.Customer;
import com.project.pawnprime.repo.LoanRepository;
import com.project.pawnprime.repo.CustomerRepository;
import org.springframework.stereotype.Service;

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
}
