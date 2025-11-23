package com.project.pawnprime.dto.loanDTO;

public class LoanSummaryDTO {
    private Long loanId;
    private Double loanVal;
    private String loanStatus;
	public LoanSummaryDTO(Long loanId, Double loanVal, String loanStatus) {
		super();
		this.loanId = loanId;
		this.loanVal = loanVal;
		this.loanStatus = loanStatus;
	}
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
	public String getLoanStatus() {
		return loanStatus;
	}
	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}
}
