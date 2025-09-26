package com.project.pawnprime.controller;

import com.project.pawnprime.dto.transaction.RepaymentTransactionDTO;
import com.project.pawnprime.model.RepaymentTransaction;
import com.project.pawnprime.service.RepaymentTransactionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repayments")
public class RepaymentTransactionController {

    private final RepaymentTransactionService repaymentService;

    public RepaymentTransactionController(RepaymentTransactionService repaymentService) {
        this.repaymentService = repaymentService;
    }

    // ✅ Add a repayment (cash / online)
    @PostMapping
    public boolean addRepayment(@RequestBody RepaymentTransactionDTO dto) {
    	RepaymentTransaction rt=repaymentService.addRepayment(dto);
    	if(rt!=null) {
    		return true;
    	}
    	return false;
    }

//    // ✅ Get all repayments for a loan
//    @GetMapping("/loan/{loanId}")
//    public List<RepaymentTransaction> getRepaymentsByLoan(@PathVariable Long loanId) {
//        return repaymentService.getRepaymentsByLoan(loanId);
//    }
}
