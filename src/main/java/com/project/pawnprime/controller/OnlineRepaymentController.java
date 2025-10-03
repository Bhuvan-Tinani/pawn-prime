package com.project.pawnprime.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.pawnprime.dto.transaction.OnlineRepaymentTransactionDTO;
import com.project.pawnprime.dto.transaction.RepaymentRequest;
import com.project.pawnprime.model.OnlineRepaymentTransaction;
import com.project.pawnprime.service.OnlineRepaymentService;
import com.project.pawnprime.service.RepaymentTransactionService;

@RestController
@RequestMapping("/api/repayments")
public class OnlineRepaymentController {

    private final OnlineRepaymentService repaymentService;

    private final RepaymentTransactionService repaymentTransService;

    public OnlineRepaymentController(OnlineRepaymentService repaymentService,RepaymentTransactionService repaymentTransService) {
        this.repaymentService = repaymentService;
        this.repaymentTransService=repaymentTransService;
    }

    // Step 1: Customer starts repayment → Create Razorpay Order
    @PostMapping("/create-order")
    public String createRepaymentOrder(@RequestBody RepaymentRequest request) throws Exception {
        return repaymentService.createRepaymentOrder(request.getLoanId(), request.getAmount());
    }

    // Step 2: Razorpay payment success → Confirm payment
    @PostMapping("/confirm")
    public OnlineRepaymentTransaction confirmPayment(@RequestBody OnlineRepaymentTransactionDTO dto) {
    	OnlineRepaymentTransaction ort=repaymentService.confirmPayment(dto);
    	return ort;
    }

    // Step 3: Get all repayments for a loan
    @GetMapping("/loan/{loanId}")
    public List<OnlineRepaymentTransaction> getRepaymentsByLoan(@PathVariable Long loanId) {
        return repaymentService.getRepaymentsByLoan(loanId);
    }
    
    @PutMapping("/update-status")
    public OnlineRepaymentTransaction updateRepaymentStatus(
            @RequestParam String orderId,
            @RequestParam String paymentId) {
        return repaymentService.updateRepaymentStatus(orderId, paymentId, "SUCCESS");
    }
}
