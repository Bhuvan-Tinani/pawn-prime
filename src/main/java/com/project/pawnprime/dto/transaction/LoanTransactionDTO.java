package com.project.pawnprime.dto.transaction;

import com.project.pawnprime.model.PaymentMode;

public class LoanTransactionDTO {

    private Long loanId;
    private Long agentId;
    private Long customerId;
    private Double amount;
    private PaymentMode mode;
    private String transactionRef; // optional (for online transfer ref)

    // --- Constructors ---
    public LoanTransactionDTO() {}

    public LoanTransactionDTO(Long loanId, Long agentId, Long customerId, Double amount, PaymentMode mode, String transactionRef) {
        this.loanId = loanId;
        this.agentId = agentId;
        this.customerId = customerId;
        this.amount = amount;
        this.mode = mode;
        this.transactionRef = transactionRef;
    }

    // --- Getters and Setters ---
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
}
