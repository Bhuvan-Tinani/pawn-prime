package com.project.pawnprime.controller;

import com.project.pawnprime.model.Loan;
import com.project.pawnprime.model.OnlineRepaymentTransaction;
import com.project.pawnprime.model.RepaymentTransaction;
import com.project.pawnprime.service.LoanService;
import com.project.pawnprime.service.OnlineRepaymentService;
import com.project.pawnprime.service.RepaymentTransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLoanChartController {

	private final LoanService loanService;
	private final RepaymentTransactionService repaymentTransactionService;
	private final OnlineRepaymentService onlineRepaymentService;

	public AdminLoanChartController(LoanService loanService, RepaymentTransactionService repaymentTransactionService, OnlineRepaymentService onlineRepaymentService) {
		this.loanService = loanService;
		this.repaymentTransactionService = repaymentTransactionService;
		this.onlineRepaymentService = onlineRepaymentService;
	}

	@GetMapping("/loan-charts")
	public Map<String, Object> getLoanCharts() {
	    List<Loan> allLoans = loanService.getAllLoans();
	    Map<String, Object> response = new HashMap<>();

	    List<Map<String, Object>> byAgentCount = allLoans.stream()
	            .collect(Collectors.groupingBy(
	                    loan -> loan.getAgent() != null ? loan.getAgent().getName() : "Unknown",
	                    Collectors.counting()))
	            .entrySet().stream()
	            .map(e -> {
	                Map<String, Object> map = new HashMap<>();
	                map.put("name", e.getKey());
	                map.put("value", e.getValue());
	                return map;
	            })
				.sorted(Comparator.comparingLong((Map<String, Object> m) -> ((Number) m.get("value")).longValue()).reversed())
	            .collect(Collectors.toList());

		List<Map<String, Object>> cumulativeSeries = buildCumulativeSeries(allLoans);

	    List<Map<String, Object>> byAgentApproved = allLoans.stream()
				.filter(l -> {
					String s = l.getLoanStatus() != null ? l.getLoanStatus().toLowerCase() : "";
					return "approved".equalsIgnoreCase(s) || "active".equalsIgnoreCase(s) || "t_done".equalsIgnoreCase(s);
				})
	            .collect(Collectors.groupingBy(
	                    loan -> loan.getAgent() != null ? loan.getAgent().getName() : "Unknown",
	                    Collectors.counting()))
	            .entrySet().stream()
	            .map(e -> {
	                Map<String, Object> map = new HashMap<>();
	                map.put("name", e.getKey());
	                map.put("value", e.getValue());
					return map;
				})
					.sorted(Comparator.comparingLong((Map<String, Object> m) -> ((Number) m.get("value")).longValue()).reversed())
				.collect(Collectors.toList());
	    
        // 🔹 NEW: total loan amount per agent
        List<Map<String, Object>> byAgentAmount = buildByAgentAmount(allLoans);

        // 🔹 NEW: loan status distribution
        List<Map<String, Object>> byStatus = buildByStatus(allLoans);

        // 🔹 NEW: monthly disbursed vs repaid
        List<Map<String, Object>> monthlyCashflow = buildMonthlyCashflow(allLoans);
        
        
        response.put("byAgentCount", byAgentCount);
        response.put("cumulativeSeries", cumulativeSeries);
        response.put("byAgentApproved", byAgentApproved);

        // 🔹 NEW fields added to API response
        response.put("byAgentAmount", byAgentAmount);
        response.put("byStatus", byStatus);
        response.put("monthlyCashflow", monthlyCashflow);

        return response;
	}

	private List<Map<String, Object>> buildCumulativeSeries(List<Loan> allLoans) {
		List<RepaymentTransaction> offlineRepayments = repaymentTransactionService.getAllRepayments();

		// --- LOGIC CORRECTED ---
		// Call the new, more specific method to get only successful payments
		List<OnlineRepaymentTransaction> successfulOnlineRepayments = onlineRepaymentService.getAllRepayments();

		Map<Long, Double> repaymentsByLoanId = new HashMap<>();

		// Guard against null loan references in transactions
		if (offlineRepayments != null) {
			offlineRepayments.stream()
				.filter(txn -> txn != null && txn.getLoan() != null && txn.getLoan().getId() != null && txn.getTotalAmt() != null)
				.forEach(txn ->
					repaymentsByLoanId.merge(txn.getLoan().getId(), txn.getTotalAmt(), Double::sum)
				);
		}

		// The filter is no longer needed here as it's done in the service layer, but still guard nulls
		if (successfulOnlineRepayments != null) {
			successfulOnlineRepayments.stream()
				.filter(txn -> txn != null && txn.getLoan() != null && txn.getLoan().getId() != null && txn.getAmount() != null)
				.forEach(txn ->
					repaymentsByLoanId.merge(txn.getLoan().getId(), txn.getAmount(), Double::sum)
				);
		}
		
		// We no longer rely on any date/time field; simply order by loan id (if present)
		List<Loan> sorted = allLoans.stream()
				.sorted(Comparator.comparing(l -> Optional.ofNullable(l.getId()).orElse(Long.MAX_VALUE)))
				.collect(Collectors.toList());

		List<Map<String, Object>> result = new ArrayList<>();
		double invested = 0;
		double recovered = 0;

		for (Loan loan : sorted) {
			invested += Optional.ofNullable(loan.getLoanVal()).orElse(0.0);
			recovered += repaymentsByLoanId.getOrDefault(loan.getId(), 0.0);
			String label = loan.getId() != null ? loan.getId().toString() : "?";
			result.add(Map.of(
				"label", label, // now purely an identifier, no date/time semantics
				"invested", invested,
				"recovered", recovered
			));
		}
		
		if (result.isEmpty()) {
			result.add(Map.of("label", "0", "invested", 0.0, "recovered", 0.0));
		}

		return result;
	}
    // 🔹 NEW: total loan amount per agent
    private List<Map<String, Object>> buildByAgentAmount(List<Loan> allLoans) {
        return allLoans.stream()
                .collect(Collectors.groupingBy(
                        loan -> loan.getAgent() != null ? loan.getAgent().getName() : "Unknown",
                        Collectors.summingDouble(l -> Optional.ofNullable(l.getLoanVal()).orElse(0.0))
                ))
                .entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getKey());
                    map.put("value", e.getValue()); // total amount
                    return map;
                })
                .sorted(Comparator.comparingDouble(
                        (Map<String, Object> m) -> ((Number) m.get("value")).doubleValue()
                ).reversed())
                .collect(Collectors.toList());
    }

    // 🔹 NEW: loan status distribution (count + total amount)
    private List<Map<String, Object>> buildByStatus(List<Loan> allLoans) {
        // group by status
        Map<String, List<Loan>> byStatus = allLoans.stream()
                .collect(Collectors.groupingBy(loan -> {
                    String status = loan.getLoanStatus();
                    return (status != null && !status.isBlank()) ? status.toUpperCase() : "UNKNOWN";
                }));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, List<Loan>> entry : byStatus.entrySet()) {
            String status = entry.getKey();
            List<Loan> loans = entry.getValue();

            long count = loans.size();
            double amount = loans.stream()
                    .map(l -> Optional.ofNullable(l.getLoanVal()).orElse(0.0))
                    .mapToDouble(Double::doubleValue)
                    .sum();

            Map<String, Object> map = new HashMap<>();
            map.put("status", status);
            map.put("count", count);
            map.put("amount", amount);
            result.add(map);
        }

        // sort by count descending
        result.sort(Comparator.comparingLong(
                (Map<String, Object> m) -> ((Number) m.get("count")).longValue()
        ).reversed());

        return result;
    }

    // 🔹 NEW: monthly disbursed (loans) vs repaid (offline + online)
    private List<Map<String, Object>> buildMonthlyCashflow(List<Loan> allLoans) {
        Map<String, Double> disbursedMap = new HashMap<>();
        Map<String, Double> repaidMap = new HashMap<>();

        // --- Disbursed per month (from Loan.date) ---
        for (Loan loan : allLoans) {
            if (loan.getDate() == null || loan.getLoanVal() == null || loan.getLoanStatus().equals("T_DONE") ) continue;
            String period = loan.getDate().toString().substring(0, 7); // "YYYY-MM"
            disbursedMap.merge(period, loan.getLoanVal(), Double::sum);
        }

        // --- Offline repayments per month ---
        List<RepaymentTransaction> offlineRepayments = repaymentTransactionService.getAllRepayments();
        if (offlineRepayments != null) {
            for (RepaymentTransaction txn : offlineRepayments) {
                if (txn == null || txn.getPaidDate() == null || txn.getTotalAmt() == null) continue;
                String period = txn.getPaidDate().toLocalDate().toString().substring(0, 7);
                repaidMap.merge(period, txn.getTotalAmt(), Double::sum);
            }
        }

        // --- Online repayments per month (success only, service already filters) ---
        List<OnlineRepaymentTransaction> onlineRepayments = onlineRepaymentService.getAllRepayments();
        if (onlineRepayments != null) {
            for (OnlineRepaymentTransaction txn : onlineRepayments) {
                if (txn == null || txn.getCreatedAt() == null || txn.getAmount() == null) continue;
                String period = txn.getCreatedAt().toLocalDate().toString().substring(0, 7);
                repaidMap.merge(period, txn.getAmount(), Double::sum);
            }
        }

        // --- Merge all periods and build sorted list ---
        // use sorted set for chronological order
        java.util.Set<String> allPeriods = new java.util.TreeSet<>();
        allPeriods.addAll(disbursedMap.keySet());
        allPeriods.addAll(repaidMap.keySet());

        List<Map<String, Object>> result = new ArrayList<>();
        for (String period : allPeriods) {
            double disbursed = disbursedMap.getOrDefault(period, 0.0);
            double repaid = repaidMap.getOrDefault(period, 0.0);

            Map<String, Object> map = new HashMap<>();
            map.put("period", period);        // "YYYY-MM"
            map.put("disbursed", disbursed);  // total loans started in that month
            map.put("repaid", repaid);        // offline + online
            result.add(map);
        }

        return result;
    }

    
}