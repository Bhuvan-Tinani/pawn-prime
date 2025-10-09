package com.project.pawnprime.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.pawnprime.dto.transaction.OnlineRepaymentAgentResponseDTO;
import com.project.pawnprime.dto.transaction.OnlineRepaymentTransactionDTO;
import com.project.pawnprime.dto.transaction.RepaymentRequest;
import com.project.pawnprime.model.OnlineRepaymentTransaction;
import com.project.pawnprime.service.OnlineRepaymentService;
import com.project.pawnprime.service.RepaymentTransactionService;

@RestController
@RequestMapping("/api/onlinerepayments")
public class OnlineRepaymentController {

	private final OnlineRepaymentService repaymentService;

	private final RepaymentTransactionService repaymentTransService;

	public OnlineRepaymentController(OnlineRepaymentService repaymentService,
			RepaymentTransactionService repaymentTransService) {
		this.repaymentService = repaymentService;
		this.repaymentTransService = repaymentTransService;
	}

	// Step 1: Customer starts repayment → Create Razorpay Order
	@PostMapping("/create-order")
	public String createRepaymentOrder(@RequestBody RepaymentRequest request) throws Exception {
		return repaymentService.createRepaymentOrder(request.getLoanId(), request.getAmount());
	}

	// Step 2: Razorpay payment success → Confirm payment
	@PostMapping("/confirm")
	public boolean confirmPayment(@RequestBody OnlineRepaymentTransactionDTO dto) {
		OnlineRepaymentTransaction ort = repaymentService.confirmPayment(dto);
		return (ort != null);
	}

	// Step 3: Get all repayments for a loan
	@GetMapping("/loan/{loanId}")
	public List<OnlineRepaymentAgentResponseDTO> getRepaymentsByLoan(@PathVariable Long loanId) {
	    List<OnlineRepaymentTransaction> ortList = repaymentService.getRepaymentsByLoan(loanId);

	    // Convert each transaction to DTO
	    return ortList.stream().map(ort -> {
	        OnlineRepaymentAgentResponseDTO dto = new OnlineRepaymentAgentResponseDTO();
	        dto.setId(ort.getId());
	        dto.setOrderId(ort.getOrderId());
	        dto.setPaymentId(ort.getPaymentId());
	        dto.setAmount(ort.getAmount());
	        dto.setLoanId(ort.getLoan() != null ? ort.getLoan().getId() : null);

	        // agent details
	        if (ort.getLoan() != null && ort.getLoan().getAgent() != null) {
	            dto.setAgentId(ort.getLoan().getAgent().getId());
	            dto.setAgentName(ort.getLoan().getAgent().getName());
	            dto.setAgentEmail(ort.getLoan().getAgent().getEmail());
	        }

	        // customer details (borrower)
	        if (ort.getLoan() != null && ort.getLoan().getCustomer() != null) {
	            dto.setCustomerName(
	                ort.getLoan().getCustomer().getFirstName() + " " + ort.getLoan().getCustomer().getLastName()
	            );
	        }

	        dto.setStatus(ort.getStatus());
	        dto.setCreatedAt(ort.getCreatedAt());

	        return dto;
	    }).collect(Collectors.toList());
	}


	@PutMapping("/update-status")
	public OnlineRepaymentTransaction updateRepaymentStatus(@RequestParam String orderId,
			@RequestParam String paymentId) {
		return repaymentService.updateRepaymentStatus(orderId, paymentId, "SUCCESS");
	}

	// package com.project.pawnprime.controller;
	// ... (imports)

	// In OnlineRepaymentController:
	// ...

	// ✅ Get all online repayments handled by a specific agent (Returns DTOs)
	@GetMapping("/agent/{agentId}")
	public List<OnlineRepaymentAgentResponseDTO> getRepaymentsByAgent(@PathVariable Long agentId) {
		List<OnlineRepaymentTransaction> ortList = repaymentService.getRepaymentsByAgent(agentId);

		// Convert each transaction to a DTO
		List<OnlineRepaymentAgentResponseDTO> response = ortList.stream().map(ort -> {
			OnlineRepaymentAgentResponseDTO dto = new OnlineRepaymentAgentResponseDTO();
			dto.setId(ort.getId());
			dto.setOrderId(ort.getOrderId());
			dto.setPaymentId(ort.getPaymentId());
			dto.setAmount(ort.getAmount());
			dto.setLoanId(ort.getLoan() != null ? ort.getLoan().getId() : null);

			// agent details
			if (ort.getLoan() != null && ort.getLoan().getAgent() != null) {
				dto.setAgentId(ort.getLoan().getAgent().getId());
				dto.setAgentName(ort.getLoan().getAgent().getName());
				dto.setAgentEmail(ort.getLoan().getAgent().getEmail());
			}

			// customer details (borrower)
			if (ort.getLoan() != null && ort.getLoan().getCustomer() != null) {
				dto.setCustomerName(
						ort.getLoan().getCustomer().getFirstName() + " " + ort.getLoan().getCustomer().getLastName());
			}

			dto.setStatus(ort.getStatus());
			dto.setCreatedAt(ort.getCreatedAt());

			return dto;
		}).collect(Collectors.toList());

		return response;
	}

	// In OnlineRepaymentController.java

	// ✅ Get all online repayment transactions (returns DTOs)
	@GetMapping
	public List<OnlineRepaymentAgentResponseDTO> getAllOnlineRepayments() {
		List<OnlineRepaymentTransaction> ortList = repaymentService.getAllRepayments();

		// Convert each transaction to DTO
		return ortList.stream().map(ort -> {
			OnlineRepaymentAgentResponseDTO dto = new OnlineRepaymentAgentResponseDTO();
			dto.setId(ort.getId());
			dto.setOrderId(ort.getOrderId());
			dto.setPaymentId(ort.getPaymentId());
			dto.setAmount(ort.getAmount());
			dto.setLoanId(ort.getLoan() != null ? ort.getLoan().getId() : null);

			// agent details
			if (ort.getLoan() != null && ort.getLoan().getAgent() != null) {
				dto.setAgentId(ort.getLoan().getAgent().getId());
				dto.setAgentName(ort.getLoan().getAgent().getName());
				dto.setAgentEmail(ort.getLoan().getAgent().getEmail());
			}

			// customer details (borrower)
			if (ort.getLoan() != null && ort.getLoan().getCustomer() != null) {
				dto.setCustomerName(
						ort.getLoan().getCustomer().getFirstName() + " " + ort.getLoan().getCustomer().getLastName());
			}

			dto.setStatus(ort.getStatus());
			dto.setCreatedAt(ort.getCreatedAt());

			return dto;
		}).collect(Collectors.toList());
	}

}
