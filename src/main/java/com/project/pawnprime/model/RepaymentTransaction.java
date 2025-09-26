package com.project.pawnprime.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "repayment_transaction")
public class RepaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "agent_id", nullable = true)
    private Agent agent;  // only used when type = CASH

    private Double principalAmt;
    private Double interestAmt;
    private Double totalAmt;

    private LocalDateTime date;     // scheduled installment date
    private LocalDateTime paidDate; // actual payment date

    @Enumerated(EnumType.STRING)
    private PaymentType type; // ONLINE or CASH

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
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

	public Double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
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

	public PaymentType getType() {
		return type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

    // --- getters and setters ---
    
}
