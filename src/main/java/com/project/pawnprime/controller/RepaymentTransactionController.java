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

//    // ✅ Get all repayments for a loan
//    @GetMapping("/loan/{loanId}")
//    public List<RepaymentTransaction> getRepaymentsByLoan(@PathVariable Long loanId) {
//        return repaymentService.getRepaymentsByLoan(loanId);
//    }

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

}
