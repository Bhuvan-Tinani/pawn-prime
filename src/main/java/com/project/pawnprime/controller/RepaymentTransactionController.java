package com.project.pawnprime.controller;

import com.project.pawnprime.dto.transaction.RepaymentTransactionDTO;
import com.project.pawnprime.model.RepaymentTransaction;
import com.project.pawnprime.service.ReceiptService;
import com.project.pawnprime.service.RepaymentTransactionService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/repayments")
public class RepaymentTransactionController {

	private final RepaymentTransactionService repaymentService;
	private final ReceiptService receiptService;

	public RepaymentTransactionController(RepaymentTransactionService repaymentService, ReceiptService receiptService) {
		this.repaymentService = repaymentService;
		this.receiptService = receiptService;
	}

	// ✅ Add a repayment (cash / online)
//    @PostMapping
//    public boolean addRepayment(@RequestBody RepaymentTransactionDTO dto) {
//    	RepaymentTransaction rt=repaymentService.addRepayment(dto);
//    	if(rt!=null) {
//    		return true;
//    	}
//    	return false;
//    }
	@PostMapping
	public boolean addRepayments(@RequestBody List<RepaymentTransactionDTO> dtoList) {
		boolean allSaved = true;

		for (RepaymentTransactionDTO dto : dtoList) {
			RepaymentTransaction rt = repaymentService.addRepayment(dto);
			if (rt == null) {
				allSaved = false;
			}
		}

		return allSaved;
	}

	@GetMapping("/loan/{loanId}")
    public List<RepaymentTransactionDTO> getRepaymentsByLoan(@PathVariable Long loanId) {
        List<RepaymentTransaction> transactions = repaymentService.getRepaymentsByLoan(loanId);
        return transactions.stream().map(txn -> {
	        RepaymentTransactionDTO dto = new RepaymentTransactionDTO();
	        dto.setLoanId(txn.getLoan().getId());
	        dto.setAgentId(txn.getAgent() != null ? txn.getAgent().getId() : null);
	        dto.setPrincipalAmt(txn.getPrincipalAmt());
	        dto.setInterestAmt(txn.getInterestAmt());
	        dto.setType(txn.getType());
	        dto.setDate(txn.getDate());
	        dto.setPaidDate(txn.getPaidDate());
	        dto.setAgentName(txn.getLoan().getAgent().getName());
	        dto.setCustomerName(txn.getLoan().getCustomer().getFirstName()+" "+txn.getLoan().getCustomer().getLastName());
	        return dto;
	    }).collect(Collectors.toList());
    }

	@GetMapping("/loan/{loanId}/receipt")
	public void generateLoanReceipt(@PathVariable Long loanId, // Now takes loanId
			HttpServletResponse response) throws IOException {

		// 1. Set response headers for PDF download
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=loan_receipt_" + loanId + ".pdf";
		response.setHeader(headerKey, headerValue);

		// 2. Call the service with loanId
		receiptService.exportLoanReceipt(loanId, response.getOutputStream());
	}
	
	@GetMapping
    public List<RepaymentTransactionDTO> getAllRepayments() {
        List<RepaymentTransaction> transactions = repaymentService.getAllRepayments();
        return transactions.stream().map(txn -> {
            RepaymentTransactionDTO dto = new RepaymentTransactionDTO();
            dto.setLoanId(txn.getLoan().getId());
            dto.setAgentId(txn.getAgent() != null ? txn.getAgent().getId() : null);
            dto.setPrincipalAmt(txn.getPrincipalAmt());
            dto.setInterestAmt(txn.getInterestAmt());
            dto.setType(txn.getType());
            dto.setDate(txn.getDate());
            dto.setPaidDate(txn.getPaidDate());
            
            dto.setAgentName(txn.getLoan().getAgent().getName());
            
	        dto.setCustomerName(txn.getLoan().getCustomer().getFirstName()+" "+txn.getLoan().getCustomer().getLastName());
            return dto;
        }).collect(Collectors.toList());
    }
	
	@GetMapping("/agent/{agentId}")
	public List<RepaymentTransactionDTO> getRepaymentsByAgent(@PathVariable Long agentId) {
	    List<RepaymentTransaction> transactions = repaymentService.getRepaymentsByAgent(agentId);

	    return transactions.stream().map(txn -> {
	        RepaymentTransactionDTO dto = new RepaymentTransactionDTO();
	        dto.setLoanId(txn.getLoan().getId());
	        dto.setAgentId(txn.getAgent() != null ? txn.getAgent().getId() : null);
	        dto.setPrincipalAmt(txn.getPrincipalAmt());
	        dto.setInterestAmt(txn.getInterestAmt());
	        dto.setType(txn.getType());
	        dto.setDate(txn.getDate());
	        dto.setPaidDate(txn.getPaidDate());
	        dto.setAgentName(txn.getLoan().getAgent().getName());
	        dto.setCustomerName(txn.getLoan().getCustomer().getFirstName()+" "+txn.getLoan().getCustomer().getLastName());
	        return dto;
	    }).collect(Collectors.toList());
	}


}
