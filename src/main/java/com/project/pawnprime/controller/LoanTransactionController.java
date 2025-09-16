package com.project.pawnprime.controller;

import com.project.pawnprime.dto.transaction.LoanTransactionAdminResponseDTO;
import com.project.pawnprime.dto.transaction.LoanTransactionDTO;
import com.project.pawnprime.dto.transaction.TransactionResponseDTO;
import com.project.pawnprime.service.LoanTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class LoanTransactionController {

    private final LoanTransactionService loanTransactionService;

    public LoanTransactionController(LoanTransactionService loanTransactionService) {
        this.loanTransactionService = loanTransactionService;
    }

    // ✅ Create Transaction (DTO-based)
    @PostMapping
    public ResponseEntity<LoanTransactionDTO> createTransaction(
            @RequestBody LoanTransactionDTO dto
    ) {
        LoanTransactionDTO savedTxn = loanTransactionService.createTransaction(dto);
        return ResponseEntity.ok(savedTxn);
    }

    // ✅ Get all transactions
    @GetMapping
    public ResponseEntity<List<LoanTransactionAdminResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(loanTransactionService.getAllTransactions());
    }

    // ✅ Get transactions by loan
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<LoanTransactionDTO>> getTransactionsByLoan(@PathVariable Long loanId) {
        return ResponseEntity.ok(loanTransactionService.getTransactionsByLoan(loanId));
    }

    // ✅ Get transactions by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanTransactionDTO>> getTransactionsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanTransactionService.getTransactionsByCustomer(customerId));
    }

    // ✅ Get transactions by agent
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByAgent(@PathVariable Long agentId) {
        return ResponseEntity.ok(loanTransactionService.getTransactionsByAgent(agentId));
    }
}
