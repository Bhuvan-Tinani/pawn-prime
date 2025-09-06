
package com.project.pawnprime.dto.loanDTO;

import java.time.LocalDate;

public class LoanCalcResponse {
	 private double principal;
	    private double rate;
	    private int duration; // in months

	    // Schedule fields
	    private double emi;
	    private double remaining;
	    private double total;
	    private LocalDate date;
	    
	    private String type; // "GOLD" or "SILVER"
	    private double totalGram;
	    private double purityPercentage;
	    private double netGram;
	    private double pricePerGram;
	    private double totalValue; // Total ornament value
	    private double ltvRatio;
	    
	    
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
		public double getNetGram() {
			return netGram;
		}
		public void setNetGram(double netGram) {
			this.netGram = netGram;
		}
		public double getPricePerGram() {
			return pricePerGram;
		}
		public void setPricePerGram(double pricePerGram) {
			this.pricePerGram = pricePerGram;
		}
		public double getTotalValue() {
			return totalValue;
		}
		public void setTotalValue(double totalValue) {
			this.totalValue = totalValue;
		}
		public double getLtvRatio() {
			return ltvRatio;
		}
		public void setLtvRatio(double ltvRatio) {
			this.ltvRatio = ltvRatio;
		}
	    // Getters & Setters
	    public double getPrincipal() {
	        return principal;
	    }
	    public void setPrincipal(double principal) {
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

	    public double getEmi() {
	        return emi;
	    }
	    public void setEmi(double emi) {
	        this.emi = emi;
	    }

	    public double getRemaining() {
	        return remaining;
	    }
	    public void setRemaining(double remaining) {
	        this.remaining = remaining;
	    }

	    public double getTotal() {
	        return total;
	    }
	    public void setTotal(double total) {
	        this.total = total;
	    }

	    public LocalDate getDate() {
	        return date;
	    }
	    public void setDate(LocalDate date) {
	        this.date = date;
	    }
}
