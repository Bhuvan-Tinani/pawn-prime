package com.project.pawnprime.dto.transaction;

public class RepaymentRequest {
    private Long loanId;
    private Double amount;

    // Getters and setters
    public Long getLoanId() {
        return loanId;
    }
    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
