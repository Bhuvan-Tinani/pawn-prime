
package com.project.pawnprime.dto.loanDTO;

public class LoanCalcRequest {
	
    private String type; // "GOLD" or "SILVER"
    private double totalGram;
    private double purityPercentage;
    private double ltvRatio; // Loan-to-Value ratio (0-80)
    private double rate;
    private int duration; // in months
    private Double principal; // Make it nullable for backward compatibility

    // Getters & Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTotalGram() {
        return totalGram;
    }

    public void setTotalGram(double totalGram) {
        this.totalGram = totalGram;
    }

    public double getPurityPercentage() {
        return purityPercentage;
    }

    public void setPurityPercentage(double purityPercentage) {
        this.purityPercentage = purityPercentage;
    }

    public double getLtvRatio() {
        return ltvRatio;
    }

    public void setLtvRatio(double ltvRatio) {
        this.ltvRatio = ltvRatio;
    }

    public Double getPrincipal() {
        return principal;
    }

    public void setPrincipal(Double principal) {
        this.principal = principal;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
