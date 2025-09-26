package com.project.pawnprime.controller;

import com.project.pawnprime.dto.loanDTO.LoanCalcRequest;
import com.project.pawnprime.dto.loanDTO.LoanCalcResponse;
import com.project.pawnprime.dto.loanDTO.LoanDTO;
import com.project.pawnprime.dto.loanDTO.LoanRequestStatus;
import com.project.pawnprime.dto.loanDTO.LoanScheduleDTO;
import com.project.pawnprime.mapper.LoanMapper;
import com.project.pawnprime.model.Agent;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.service.AgentService;
import com.project.pawnprime.service.LoanService;
import com.project.pawnprime.service.RepaymentTransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('AGENT','ADMIN')")
public class LoanController {

    private final LoanService loanService;
    private final AgentService agentService;
    private final RepaymentTransactionService repaymentTransService;

    public LoanController(LoanService loanService,AgentService agentService,RepaymentTransactionService repaymentTransService) {
        this.loanService = loanService;
        this.agentService= agentService;
        this.repaymentTransService=repaymentTransService;
    }

    // Create loan for a customer
    @PostMapping("/customer/{customerId}")
    public LoanDTO createLoan(@PathVariable Long customerId, @RequestBody LoanDTO loanDTO) {
        loanDTO.setDate(LocalDate.now()); // Always set today's date from server
        Loan loan = LoanMapper.toEntity(loanDTO); // DTO → Entity
        Agent agent=agentService.getAgentById(loanDTO.getAgentId()).orElse(null);
        if(agent!=null) {
        loan.setAgent(agent);
        Loan savedLoan = loanService.createLoan(customerId, loan);
        return LoanMapper.toDTO(savedLoan); // Entity → DTO
        }
        return null;
    }

    // Get all loans
    @GetMapping
    public List<LoanDTO> getAllLoans() {
        return loanService.getAllLoans()
                .stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get loans for a specific customer
    @GetMapping("/customer/{customerId}")
    public List<LoanDTO> getLoansByCustomer(@PathVariable Long customerId) {
        return loanService.getLoansByCustomer(customerId)
                .stream()
                .map(LoanMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Get loan by ID
    @GetMapping("/{loanId}")
    public LoanDTO getLoanById(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        return LoanMapper.toDTO(loan);
    }

    // Update loan
    @PutMapping("/{loanId}")
    public LoanDTO updateLoan(@PathVariable Long loanId, @RequestBody LoanDTO loanDTO) {
        Loan loan = LoanMapper.toEntity(loanDTO);
        Loan updatedLoan = loanService.updateLoan(loanId, loan);
        return LoanMapper.toDTO(updatedLoan);
    }

    // Delete loan
    @DeleteMapping("/{loanId}")
    public String deleteLoan(@PathVariable Long loanId) {
        loanService.deleteLoan(loanId);
        return "Loan deleted successfully!";
    }

    @GetMapping("/{loanId}/schedule")
    public List<LoanScheduleDTO> getLoanSchedule(@PathVariable Long loanId) {
        Loan loan = loanService.getLoanById(loanId);
        double principal = loan.getLoanVal();
        double annualRate = loan.getInterestRate(); // e.g., 4%
        int months = loan.getDuration();
        LocalDate startDate = loan.getDate();

        // simple interest total
        double totalInterest = (principal * annualRate * (months / 12.0)) / 100;
        double totalPayable = principal + totalInterest;

        // monthly breakup
        double monthlyPrincipal = principal / months;
        double monthlyInterest = totalInterest / months;
        double monthlyInstallment = monthlyPrincipal + monthlyInterest;

        List<LoanScheduleDTO> schedule = new ArrayList<>();
        double balance = totalPayable;

        for (int i = 1; i <= months; i++) {
            balance -= monthlyInstallment;

            LoanScheduleDTO dto = new LoanScheduleDTO();
            dto.setInstallmentNo(i);
            dto.setLoanId(loan.getId());
            dto.setPrincipalAmount(Math.round(principal * 100.0) / 100.0);
            dto.setInterestAmount(Math.round(monthlyInterest * 100.0) / 100.0);
            dto.setTotalInstallment(Math.round(monthlyInstallment * 100.0) / 100.0);
            dto.setRemainingAmount(Math.max(0, Math.round(balance * 100.0) / 100.0));
            dto.setDate(startDate.plusMonths(i));

            schedule.add(dto);
        }

        return schedule;
    }

    // Modified calculator endpoint with ornament calculation
    @PostMapping("/LoanCalculator")
    public List<LoanCalcResponse> loanCalculator(@RequestBody LoanCalcRequest request) {
        return loanService.calculateSchedule(request);
    }

    // NEW ENDPOINT: Fetch fresh prices from external API (called after login)
    @PostMapping("/fetch-prices")
    public Map<String, Object> fetchFreshPrices() {
        try {
            Map<String, Double> prices = loanService.fetchAndCachePrices();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("prices", prices);
            response.put("lastUpdated", loanService.getLastFetchedTime());
            response.put("message", "Prices updated successfully");
            response.put("source", "MetalpriceAPI");
            return response;
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Failed to fetch prices: " + e.getMessage());
            response.put("fallback", true);
            return response;
        }
    }

    // MODIFIED: Get current cached prices (used by Calculator and Dashboard)
    @GetMapping("/prices")
    public Map<String, Object> getCurrentPrices() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Double> prices = new HashMap<>();
        prices.put("gold", loanService.getCurrentGoldPrice());
        prices.put("silver", loanService.getCurrentSilverPrice());
        
        response.put("success", true);
        response.put("prices", prices);
        response.put("lastUpdated", loanService.getLastFetchedTime());
        response.put("source", "cached");
        return response;
    }

    // Optional: Endpoint to get price for specific type
    @GetMapping("/prices/{type}")
    public Map<String, Object> getPrice(@PathVariable String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            double price;
            if ("gold".equalsIgnoreCase(type)) {
                price = loanService.getCurrentGoldPrice();
            } else if ("silver".equalsIgnoreCase(type)) {
                price = loanService.getCurrentSilverPrice();
            } else {
                response.put("error", "Invalid type. Use 'gold' or 'silver'");
                return response;
            }
            
            response.put("success", true);
            response.put("type", type.toLowerCase());
            response.put("pricePerGram", price);
            response.put("currency", "INR");
            response.put("lastUpdated", loanService.getLastFetchedTime());
            
        } catch (Exception e) {
            response.put("error", "Failed to get price for " + type);
        }
        
        return response;
    }
    
    @GetMapping("/agent/{agentId}")
    public List<LoanDTO> getLoansByAgent(@PathVariable Long agentId) {
    	List<Loan> loans = loanService.getLoansByAgentId(agentId);
        return LoanMapper.toDTOList(loans);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanRequestStatus>> getLoanByStatus(@PathVariable String status){
    	List<Loan> loanlist=loanService.getLoanByStatus(status);
    	return ResponseEntity.ok(LoanMapper.toAdminLoanDTOList(loanlist));
    }
    
    @PostMapping("/status/{loanId}")
    public boolean updateLoanStatus(@PathVariable Long loanId,@RequestBody Map<String, String> body) {
    	String status = body.get("status");
    	if(status.equals("approved") || status.equals("pending") || status.equals("rejected")) {
        	return loanService.changeLoanStatus(loanId,status);
    	}
    	return false;
    }
    
    @GetMapping("/{loanId}/repayment")
    public List<LoanScheduleDTO> getLoanRepayment(@PathVariable Long loanId) {
        Loan loan = loanService.getByStatusAndId(loanId, "T_DONE");
        if(loan == null) {
            return null;
        }
        
        int paidLoan = (int) repaymentTransService.getRepaymentCountForLoan(loanId);

        double principal = loan.getLoanVal();
        double annualRate = loan.getInterestRate(); // e.g., 4%
        int months = loan.getDuration();
        LocalDate startDate = loan.getDate();

        // simple interest total
        double totalInterest = (principal * annualRate * (months / 12.0)) / 100;
        double totalPayable = principal + totalInterest;

        // monthly breakup
        double monthlyPrincipal = principal / months;
        double monthlyInterest = totalInterest / months;
        double monthlyInstallment = monthlyPrincipal + monthlyInterest;

        List<LoanScheduleDTO> schedule = new ArrayList<>();
        double balance = totalPayable;

        for (int i = 1; i <= months; i++) {
            balance -= monthlyInstallment;

            LoanScheduleDTO dto = new LoanScheduleDTO();
            dto.setInstallmentNo(i);
            dto.setLoanId(loan.getId());
            dto.setPrincipalAmount(Math.round(monthlyPrincipal * 100.0) / 100.0);
            dto.setInterestAmount(Math.round(monthlyInterest * 100.0) / 100.0);
            dto.setTotalInstallment(Math.round(monthlyInstallment * 100.0) / 100.0);
            dto.setRemainingAmount(Math.max(0, Math.round(balance * 100.0) / 100.0));
            dto.setDate(startDate.plusMonths(i));
            
            // Set status based on paid installments count
            if (i <= paidLoan) {
                dto.setStatus("PAID");
            } else {
                dto.setStatus("PENDING");
            }

            schedule.add(dto);
        }

        return schedule;
    }
    
}

