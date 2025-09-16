package com.project.pawnprime.dto.transaction;

import java.time.LocalDateTime;

import com.project.pawnprime.model.PaymentMode;

public class LoanTransactionAdminResponseDTO {

	private Long transactionId;
    private Long loanId;
    private String agentName;
    private String customerName;
    private Double amount;
    private PaymentMode mode;
    private String transactionRef; 
    private LocalDateTime transactionDate;
    
    
	public LoanTransactionAdminResponseDTO() {
		super();
	}
	public LoanTransactionAdminResponseDTO(Long transactionId, Long loanId, String agentName, String customerName,
			Double amount, PaymentMode mode, String transactionRef, LocalDateTime transactionDate) {
		super();
		this.transactionId = transactionId;
		this.loanId = loanId;
		this.agentName = agentName;
		this.customerName = customerName;
		this.amount = amount;
		this.mode = mode;
		this.transactionRef = transactionRef;
		this.transactionDate = transactionDate;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public Long getLoanId() {
		return loanId;
	}
	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public PaymentMode getMode() {
		return mode;
	}
	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}
	public String getTransactionRef() {
		return transactionRef;
	}
	public void setTransactionRef(String transactionRef) {
		this.transactionRef = transactionRef;
	}
	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}
    
}
