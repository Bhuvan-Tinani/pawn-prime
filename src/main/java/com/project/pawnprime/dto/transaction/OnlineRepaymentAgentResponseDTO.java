package com.project.pawnprime.dto.transaction;

import java.time.LocalDateTime;

public class OnlineRepaymentAgentResponseDTO {

    private Long id;
    private String orderId;
    private String paymentId;
    private Double amount;
    private Long loanId;
    private Long agentId;
    
    // ✅ New Fields Added
    private String agentName;
    private String agentEmail;
    private String customerName;
    
    private String status;
    private LocalDateTime createdAt;

    // Default Constructor
    public OnlineRepaymentAgentResponseDTO() {}

	public OnlineRepaymentAgentResponseDTO(Long id, String orderId, String paymentId, Double amount, Long loanId,
			Long agentId, String agentName, String agentEmail, String customerName, String status,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.orderId = orderId;
		this.paymentId = paymentId;
		this.amount = amount;
		this.loanId = loanId;
		this.agentId = agentId;
		this.agentName = agentName;
		this.agentEmail = agentEmail;
		this.customerName = customerName;
		this.status = status;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    
}