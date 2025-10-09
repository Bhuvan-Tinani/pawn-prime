package com.project.pawnprime.dto.transaction;

import com.project.pawnprime.model.PaymentMode;

import java.time.LocalDateTime;

public class TransactionResponseDTO {

    private Long transactionId;
    private Long loanId;
    private Long agentId;
    private String agentName;
    private String agentEmail;
    private String customerName;
    private Long customerId;
    private Double amount;
    private PaymentMode mode;
    private String transactionRef; 
    private LocalDateTime transactionDate;

    // --- Constructors ---
    public TransactionResponseDTO() {}

    

    public TransactionResponseDTO(Long transactionId, Long loanId, Long agentId, String agentName, String agentEmail,
			String customerName, Long customerId, Double amount, PaymentMode mode, String transactionRef,
			LocalDateTime transactionDate) {
		super();
		this.transactionId = transactionId;
		this.loanId = loanId;
		this.agentId = agentId;
		this.agentName = agentName;
		this.agentEmail = agentEmail;
		this.customerName = customerName;
		this.customerId = customerId;
		this.amount = amount;
		this.mode = mode;
		this.transactionRef = transactionRef;
		this.transactionDate = transactionDate;
	}
    
//    public TransactionResponseDTO(Long transactionId, Long loanId, Long agentId, String agentName, String agentEmail,
//			String customerName, Long customerId, Double amount, PaymentMode mode, String transactionRef,
//			LocalDateTime transactionDate) {
//		super();
//		this.transactionId = transactionId;
//		this.loanId = loanId;
//		this.agentId = agentId;
//		this.agentName = agentName;
//		this.agentEmail = agentEmail;
//		this.customerName = customerName;
//		this.customerId = customerId;
//		this.amount = amount;
//		this.mode = mode;
//		this.transactionRef = transactionRef;
//		this.transactionDate = transactionDate;
//	}



	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentEmail() {
		return agentEmail;
	}

	public void setAgentEmail(String agentEmail) {
		this.agentEmail = agentEmail;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	// --- Getters and Setters ---
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

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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
