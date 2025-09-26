package com.project.pawnprime.dto.transaction;

public class OnlineRepaymentTransactionDTO {
    private String orderId;
    private String paymentId;
    private Double amount;
    private Long loanId;
    private String status;

    // getters and setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Long getLoanId() { return loanId; }
    public void setLoanId(Long loanId) { this.loanId = loanId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
