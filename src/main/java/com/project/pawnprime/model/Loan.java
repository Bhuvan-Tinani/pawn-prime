package com.project.pawnprime.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String typeOrnament;   // e.g. Gold Chain, Ring
    private Double netGram;        // net weight in grams
    private Double purityPercent;  // e.g. 91.6% for 22k
    private Double purityGram;     // netGram * purityPercent/100
    private Double value;          // value based on gold rate
    private Double loanVal;        // sanctioned loan amount
    private Double interestRate;   // % interest rate
    private Integer duration;      // duration in months
    private String loanStatus;

	// 🔗 Relationship: Many loans can belong to one customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;
    
    

    // --- Getters & Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTypeOrnament() {
        return typeOrnament;
    }

    public void setTypeOrnament(String typeOrnament) {
        this.typeOrnament = typeOrnament;
    }

    public Double getNetGram() {
        return netGram;
    }

    public void setNetGram(Double netGram) {
        this.netGram = netGram;
    }

    public Double getPurityPercent() {
        return purityPercent;
    }

    public void setPurityPercent(Double purityPercent) {
        this.purityPercent = purityPercent;
    }

    public Double getPurityGram() {
        return purityGram;
    }

    public void setPurityGram(Double purityGram) {
        this.purityGram = purityGram;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getLoanVal() {
        return loanVal;
    }

    public void setLoanVal(Double loanVal) {
        this.loanVal = loanVal;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}
    

    public String getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}
}
