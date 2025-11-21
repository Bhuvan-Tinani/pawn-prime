package com.project.pawnprime.service;

import com.project.pawnprime.dto.transaction.OnlineRepaymentTransactionDTO;
import com.project.pawnprime.model.Loan;
import com.project.pawnprime.model.OnlineRepaymentTransaction;
import com.project.pawnprime.repo.LoanRepository;
import com.project.pawnprime.repo.OnlineRepaymentTransactionRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OnlineRepaymentService {

    private final RazorpayClient razorpayClient;
    private final OnlineRepaymentTransactionRepository repaymentRepo;
    private final LoanRepository loanRepo;

    public OnlineRepaymentService(RazorpayClient razorpayClient,
                                    OnlineRepaymentTransactionRepository repaymentRepo,
                                    LoanRepository loanRepo) {
        this.razorpayClient = razorpayClient;
        this.repaymentRepo = repaymentRepo;
        this.loanRepo = loanRepo;
    }

    // Step 1: Create Razorpay Order for repayment
    public String createRepaymentOrder(Long loanId, Double amount) throws Exception {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount); // convert to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(orderRequest);

        // Save in DB
        OnlineRepaymentTransaction txn = new OnlineRepaymentTransaction();
        txn.setOrderId(order.get("id"));
        txn.setLoan(loan);
        txn.setAmount(amount);
        txn.setStatus("PENDING");

        repaymentRepo.save(txn);

        return order.toString();
    }

    // Step 2: Save payment details after success
    public OnlineRepaymentTransaction confirmPayment(OnlineRepaymentTransactionDTO dto) {
        Loan loan = loanRepo.findById(dto.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        OnlineRepaymentTransaction txn = repaymentRepo.findByLoanId(dto.getLoanId())
                .stream()
                .filter(t -> t.getOrderId().equals(dto.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Repayment order not found"));

        txn.setPaymentId(dto.getPaymentId());
        txn.setStatus(dto.getStatus());
        txn.setAmount(dto.getAmount());
        txn.setLoan(loan);

        return repaymentRepo.save(txn);
    }

    public List<OnlineRepaymentTransaction> getRepaymentsByLoan(Long loanId) {
        return repaymentRepo.findByLoanId(loanId);
    }
    
    public OnlineRepaymentTransaction updateRepaymentStatus(String orderId, String paymentId, String status) {
        OnlineRepaymentTransaction txn = repaymentRepo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        txn.setPaymentId(paymentId);
        txn.setStatus(status);  // e.g., "SUCCESS"
        return repaymentRepo.save(txn);
    } 
    
    public List<OnlineRepaymentTransaction> getRepaymentsByAgent(Long agentId) {
        // The repository method will need to query based on Loan.agentId
        return repaymentRepo.findByLoanAgentId(agentId);
    }
   
    public List<OnlineRepaymentTransaction> getAllRepayments() {
        return repaymentRepo.findAll();
    }

}
