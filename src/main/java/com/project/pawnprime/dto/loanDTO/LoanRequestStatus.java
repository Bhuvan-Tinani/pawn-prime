package com.project.pawnprime.dto.loanDTO;

import java.time.LocalDate;

import com.project.pawnprime.dto.customerDTO.CustomerDTO;

public class LoanRequestStatus {
	long loanId,agentId;
	private String agentName;
	private LocalDate date;
    private String typeOrnament;
    private double netGram;
    private double purityPercent;
    private double purityGram;
    private double value;
    private double loanVal;
    private double interestRate;
    private int duration;
    private String loanStatus;
    private CustomerDTO customer;
    
	public LoanRequestStatus() {
		super();
	}
	public LoanRequestStatus(long loanId, long agentId, String agentName, LocalDate date, String typeOrnament,
			double netGram, double purityPercent, double purityGram, double value, double loanVal, double interestRate,
			int duration, String loanStatus, CustomerDTO customer) {
		super();
		this.loanId = loanId;
		this.agentId = agentId;
		this.agentName = agentName;
		this.date = date;
		this.typeOrnament = typeOrnament;
		this.netGram = netGram;
		this.purityPercent = purityPercent;
		this.purityGram = purityGram;
		this.value = value;
		this.loanVal = loanVal;
		this.interestRate = interestRate;
		this.duration = duration;
		this.loanStatus = loanStatus;
		this.customer = customer;
	}
	public long getLoanId() {
		return loanId;
	}
	public void setLoanId(long loanId) {
		this.loanId = loanId;
	}
	public long getAgentId() {
		return agentId;
	}
	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
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
	public String getLoanStatus() {
		return loanStatus;
	}
	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}
	public CustomerDTO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerDTO customer) {
		this.customer = customer;
	}
    
	   
}
