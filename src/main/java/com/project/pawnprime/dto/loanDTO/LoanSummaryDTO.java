package com.project.pawnprime.dto.loanDTO;

public class LoanSummaryDTO {
    private Long loanId;
    private Double loanVal;

    // Constructor
    public LoanSummaryDTO(Long loanId, Double loanVal) {
        this.loanId = loanId;
        this.loanVal = loanVal;
    }

    // Getters & Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Double getLoanVal() {
        return loanVal;
    }

    public void setLoanVal(Double loanVal) {
        this.loanVal = loanVal;
    }
}
