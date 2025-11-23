package com.project.pawnprime.dto;

public class LoanStatusDTO {

    private Long loanId;
    private Double totalPrincipal;  // remaining principal
    private Double totalInterest;   // this month's interest
    private Double totalPaid;       // principal + interest to be paid

    public LoanStatusDTO() {}

    public LoanStatusDTO(Long loanId, Double totalPrincipal,
                         Double totalInterest, Double totalPaid) {
        this.loanId = loanId;
        this.totalPrincipal = totalPrincipal;
        this.totalInterest = totalInterest;
        this.totalPaid = totalPaid;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Double getTotalPrincipal() {
        return totalPrincipal;
    }

    public void setTotalPrincipal(Double totalPrincipal) {
        this.totalPrincipal = totalPrincipal;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(Double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public Double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Double totalPaid) {
        this.totalPaid = totalPaid;
    }
}
