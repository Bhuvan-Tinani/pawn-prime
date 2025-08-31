package com.project.pawnprime.dto.loanDTO;

import java.time.LocalDate;

public class LoanScheduleDTO {
    private int installmentNo;
    private Long loanId;
    private double principalAmount;
    private double interestAmount;
    private double totalInstallment;
    private double remainingAmount;
    private LocalDate date;
	public int getInstallmentNo() {
		return installmentNo;
	}
	public void setInstallmentNo(int installmentNo) {
		this.installmentNo = installmentNo;
	}
	public Long getLoanId() {
		return loanId;
	}
	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}
	public double getPrincipalAmount() {
		return principalAmount;
	}
	public void setPrincipalAmount(double principalAmount) {
		this.principalAmount = principalAmount;
	}
	public double getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(double interestAmount) {
		this.interestAmount = interestAmount;
	}
	public double getTotalInstallment() {
		return totalInstallment;
	}
	public void setTotalInstallment(double totalInstallment) {
		this.totalInstallment = totalInstallment;
	}
	public double getRemainingAmount() {
		return remainingAmount;
	}
	public void setRemainingAmount(double remainingAmount) {
		this.remainingAmount = remainingAmount;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
    
    
}
