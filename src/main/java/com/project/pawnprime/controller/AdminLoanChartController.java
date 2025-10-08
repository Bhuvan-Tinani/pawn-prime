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

	    response.put("byAgentCount", byAgentCount);
	    response.put("cumulativeSeries", cumulativeSeries);
	    response.put("byAgentApproved", byAgentApproved);
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
}