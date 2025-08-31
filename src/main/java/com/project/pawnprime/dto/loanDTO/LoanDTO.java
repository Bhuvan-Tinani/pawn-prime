package com.project.pawnprime.dto.loanDTO;

import java.time.LocalDate;

public class LoanDTO {
    private Long id;
    
	private LocalDate date;
    private String typeOrnament;
    private double netGram;
    private double purityPercent;
    private double purityGram;
    private double value;
    private double loanVal;
    private double interestRate;
    private int duration;
    private Long customerId;  // 🔹 instead of Customer object, just store ID
	public Long getId() {
		return id;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTypeOrnament() {
		return typeOrnament;
	}
	public void setTypeOrnament(String typeOrnament) {
		this.typeOrnament = typeOrnament;
	}
	public double getNetGram() {
		return netGram;
	}
	public void setNetGram(double netGram) {
		this.netGram = netGram;
	}
	public double getPurityPercent() {
		return purityPercent;
	}
	public void setPurityPercent(double purityPercent) {
		this.purityPercent = purityPercent;
	}
	public double getPurityGram() {
		return purityGram;
	}
	public void setPurityGram(double purityGram) {
		this.purityGram = purityGram;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public double getLoanVal() {
		return loanVal;
	}
	public void setLoanVal(double loanVal) {
		this.loanVal = loanVal;
	}
	public double getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

    // Getters & Setters
    // ...
    
}
