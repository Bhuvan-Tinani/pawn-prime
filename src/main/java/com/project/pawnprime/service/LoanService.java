


package com.project.pawnprime.service;

import com.project.pawnprime.model.Loan;
import com.project.pawnprime.dto.loanDTO.LoanCalcRequest;
import com.project.pawnprime.dto.loanDTO.LoanCalcResponse;
import com.project.pawnprime.model.Customer;
import com.project.pawnprime.repo.LoanRepository;
import com.project.pawnprime.repo.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // Cache for prices (updated on login) - INR PER GRAM
    private static double CACHED_GOLD_PRICE = 10700.0; // Default fallback ₹ per gram
    private static double CACHED_SILVER_PRICE = 120.0;  // Default fallback ₹ per gram
    private static LocalDateTime LAST_FETCHED = null;

    public LoanService(LoanRepository loanRepository, CustomerRepository customerRepository) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // FIXED METHOD: Fetch and cache prices from external API
    public Map<String, Double> fetchAndCachePrices() {
        Map<String, Double> prices = new HashMap<>();
        
        try {
            // Your actual MetalpriceAPI key
            String apiKey = "26756a090062cb53d8cbadb4b1da4ac4"; 
            String url = "https://api.metalpriceapi.com/v1/latest?api_key=" + apiKey + "&base=USD&symbols=XAU,XAG";
            
            System.out.println("🔄 Fetching prices from MetalpriceAPI...");
            System.out.println("🌐 URL: " + url);
            
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("📡 Raw API Response: " + response);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            
            // Check if API call was successful
            if (jsonNode.has("success") && jsonNode.get("success").asBoolean()) {
                JsonNode rates = jsonNode.get("rates");
                
                // FIXED: Correct parsing for MetalpriceAPI
                // XAU and XAG are returned as rates (how much of that metal per 1 USD)
                double goldRate = rates.get("XAU").asDouble(); // Amount of gold per 1 USD
                double silverRate = rates.get("XAG").asDouble(); // Amount of silver per 1 USD
                
                System.out.println("📊 Raw API rates - XAU: " + goldRate + ", XAG: " + silverRate);
                
                // Convert to price per troy ounce in USD (inverse of rate)
                double goldUSDperOz = 1.0 / goldRate;  // USD per troy oz of gold
                double silverUSDperOz = 1.0 / silverRate; // USD per troy oz of silver
                
                System.out.println("💰 USD prices - Gold: $" + goldUSDperOz + "/oz, Silver: $" + silverUSDperOz + "/oz");
                
                // Conversion constants
                double usdToInr = 83.0; // Current USD to INR exchange rate
                double troyOzToGrams = 31.1035; // 1 troy oz = 31.1035 grams
                
                // Convert to INR per gram
                double goldINRperGram = Math.round((goldUSDperOz * usdToInr / troyOzToGrams) * 100.0) / 100.0;
                double silverINRperGram = Math.round((silverUSDperOz * usdToInr / troyOzToGrams) * 100.0) / 100.0;
                
                // Update cache
                CACHED_GOLD_PRICE = goldINRperGram;
                CACHED_SILVER_PRICE = silverINRperGram;
                LAST_FETCHED = LocalDateTime.now();
                
                prices.put("gold", goldINRperGram);
                prices.put("silver", silverINRperGram);
                
                System.out.println("✅ Prices calculated successfully:");
                System.out.println("📊 Gold: ₹" + goldINRperGram + "/gram");
                System.out.println("📊 Silver: ₹" + silverINRperGram + "/gram");
                
            } else {
                throw new Exception("API returned success=false or missing success field");
            }
            
        } catch (Exception e) {
            System.err.println("❌ API fetch failed: " + e.getMessage());
            System.out.println("🔄 Using realistic fallback prices");
            
            // Use current market fallback prices (INR per gram)
            CACHED_GOLD_PRICE = 10700.0; // Current market rate per gram
            CACHED_SILVER_PRICE = 120.0;  // Current market rate per gram
            LAST_FETCHED = LocalDateTime.now();
            
            prices.put("gold", CACHED_GOLD_PRICE);
            prices.put("silver", CACHED_SILVER_PRICE);
            
            System.out.println("📊 Using fallback: Gold ₹" + CACHED_GOLD_PRICE + "/gram, Silver ₹" + CACHED_SILVER_PRICE + "/gram");
        }
        
        return prices;
    }

    public Loan createLoan(Long customerId, Loan loan) {
    	loan.setLoanStatus("pending");
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));
        loan.setCustomer(customer);
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getLoansByCustomer(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public Loan getLoanById(Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with id " + loanId));
    }

    public Loan updateLoan(Long loanId, Loan updatedLoan) {
        Loan loan = getLoanById(loanId);
        loan.setDate(updatedLoan.getDate());
        loan.setTypeOrnament(updatedLoan.getTypeOrnament());
        loan.setNetGram(updatedLoan.getNetGram());
        loan.setPurityPercent(updatedLoan.getPurityPercent());
        loan.setPurityGram(updatedLoan.getPurityGram());
        loan.setValue(updatedLoan.getValue());
        loan.setLoanVal(updatedLoan.getLoanVal());
        loan.setInterestRate(updatedLoan.getInterestRate());
        loan.setDuration(updatedLoan.getDuration());
        return loanRepository.save(loan);
    }

    public void deleteLoan(Long loanId) {
        loanRepository.deleteById(loanId);
    }

    public List<LoanCalcResponse> calculateSchedule(LoanCalcRequest request) {
        // Calculate ornament details using CACHED prices per gram
        double netGram = request.getTotalGram() * (request.getPurityPercentage() / 100.0);
        double pricePerGram = getCurrentPrice(request.getType()); // INR per gram
        double totalValue = netGram * pricePerGram;
        double principal;
        
        if (request.getPrincipal() != null) {
            principal = request.getPrincipal();
        } else {
            principal = totalValue * (request.getLtvRatio() / 100.0);
        }
        
        double rate = request.getRate();
        int duration = request.getDuration();
        double total = principal + (principal * rate * duration / 100);
        double emi = total / duration;

        List<LoanCalcResponse> schedule = new ArrayList<>();
        double remaining = total;
        LocalDate startDate = LocalDate.now();

        for (int i = 1; i <= duration; i++) {
            remaining -= emi;
            LocalDate dueDate = startDate.plusMonths(i);

            LoanCalcResponse row = new LoanCalcResponse();
            row.setPrincipal(principal);
            row.setRate(rate);
            row.setDuration(duration);
            row.setEmi(emi);
            row.setRemaining(Math.max(0, remaining));
            row.setTotal(total);
            row.setDate(dueDate);
            row.setType(request.getType());
            row.setTotalGram(request.getTotalGram());
            row.setPurityPercentage(request.getPurityPercentage());
            row.setNetGram(netGram);
            row.setPricePerGram(pricePerGram); // INR per gram
            row.setTotalValue(totalValue);
            row.setLtvRatio(request.getLtvRatio());

            schedule.add(row);
        }

        return schedule;
    }

    private double getCurrentPrice(String type) {
        if ("GOLD".equalsIgnoreCase(type)) {
            return CACHED_GOLD_PRICE; // Returns INR per gram
        } else if ("SILVER".equalsIgnoreCase(type)) {
            return CACHED_SILVER_PRICE; // Returns INR per gram
        } else {
            throw new IllegalArgumentException("Invalid ornament type: " + type + ". Must be GOLD or SILVER.");
        }
    }

    // Getter methods return INR per gram
    public double getCurrentGoldPrice() {
        return CACHED_GOLD_PRICE; // INR per gram
    }

    public double getCurrentSilverPrice() {
        return CACHED_SILVER_PRICE; // INR per gram
    }
    
    public LocalDateTime getLastFetchedTime() {
        return LAST_FETCHED;
    }
    
    public List<Loan> getLoansByAgentId(Long agentId) {
        return loanRepository.findByAgentId(agentId);
    }
    
    public List<Loan> getLoanByStatus(String status){
    	List<Loan> loans=loanRepository.findByLoanStatus("pending");
    	return loans;
    }
    
    public boolean changeLoanStatus(long loanId,String status) {
    	try {
    		Loan loan=getLoanById(loanId);
    		loan.setLoanStatus(status);
    		loanRepository.save(loan);
    	}catch(Exception e) {
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    public Loan getByStatusAndId(Long id,String status){
    	Loan loans=loanRepository.findByLoanStatusAndId(status,id);
    	return loans;
    }
    
    public List<Loan> getLoansByCustomerAadhar(String aadharNo) {
        return loanRepository.findByCustomerAadharNo(aadharNo);
    }

}
