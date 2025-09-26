package com.project.pawnprime.dto.transaction;

import com.project.pawnprime.model.PaymentType;
import java.time.LocalDateTime;

public class RepaymentTransactionDTO {
    private Long loanId;
    private Long agentId;          // optional for CASH payments
    private Double principalAmt;
    private Double interestAmt;
    private PaymentType type;      // ONLINE / CASH
    private LocalDateTime date;    // scheduled installment date
    private LocalDateTime paidDate;

    // Getters and Setters
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

    public Double getPrincipalAmt() {
        return principalAmt;
    }

    public void setPrincipalAmt(Double principalAmt) {
        this.principalAmt = principalAmt;
    }

    public Double getInterestAmt() {
        return interestAmt;
    }

    public void setInterestAmt(Double interestAmt) {
        this.interestAmt = interestAmt;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }
}
