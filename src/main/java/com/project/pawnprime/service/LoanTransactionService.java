package com.project.pawnprime.service;

import com.project.pawnprime.dto.transaction.LoanTransactionAdminResponseDTO;
import com.project.pawnprime.dto.transaction.LoanTransactionDTO;
import com.project.pawnprime.dto.transaction.TransactionResponseDTO;
import com.project.pawnprime.model.*;
import com.project.pawnprime.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanTransactionService {

    private final LoanTransactionRepository loanTransactionRepository;
    private final LoanRepository loanRepository;
    private final AgentRepository agentRepository;
    private final CustomerRepository customerRepository;

    public LoanTransactionService(LoanTransactionRepository loanTransactionRepository,
                                  LoanRepository loanRepository,
                                  AgentRepository agentRepository,
                                  CustomerRepository customerRepository) {
        this.loanTransactionRepository = loanTransactionRepository;
        this.loanRepository = loanRepository;
        this.agentRepository = agentRepository;
        this.customerRepository = customerRepository;
    }

    // ✅ Create transaction using DTO
    public LoanTransactionDTO createTransaction(LoanTransactionDTO dto) {
        Loan loan = loanRepository.findById(dto.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        Agent agent = agentRepository.findById(dto.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        LoanTransaction txn = new LoanTransaction();
        txn.setLoan(loan);
        txn.setAgent(agent);
        txn.setCustomer(customer);
        txn.setAmount(dto.getAmount());
        txn.setPaymentMode(dto.getMode());
        txn.setTransactionRef(dto.getTransactionRef());
        txn.setStatus("SUCCESS");
        txn.setTransactionDate(LocalDateTime.now());

        LoanTransaction savedTxn = loanTransactionRepository.save(txn);

        // ✅ Update loan status if successful
        if ("SUCCESS".equalsIgnoreCase(savedTxn.getStatus())) {
            loan.setLoanStatus("T_DONE");
            loanRepository.save(loan);
        }

        // ✅ Return DTO back (not entity)
        return mapToDTO(savedTxn);
    }

    // ✅ Get all transactions
    public List<LoanTransactionAdminResponseDTO> getAllTransactions() {
        return loanTransactionRepository.findAll()
                .stream()
                .map(this::mapForAdminDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get transactions by Loan
    public List<LoanTransactionDTO> getTransactionsByLoan(Long loanId) {
        return loanTransactionRepository.findByLoanId(loanId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get transactions by Customer
    public List<LoanTransactionDTO> getTransactionsByCustomer(Long customerId) {
        return loanTransactionRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ Get transactions by Agent
    public List<TransactionResponseDTO> getTransactionsByAgent(Long agentId) {
        return loanTransactionRepository.findByAgentId(agentId)
                .stream()
                .map(this::mapResponseToDTO)
                .collect(Collectors.toList());
    }
    
    private TransactionResponseDTO mapResponseToDTO(LoanTransaction txn) {
    	TransactionResponseDTO dto = new TransactionResponseDTO();
    	dto.setTransactionId(txn.getId());
        dto.setLoanId(txn.getLoan().getId());
        dto.setAgentId(txn.getAgent().getId());
        dto.setCustomerId(txn.getCustomer().getId());
        dto.setAmount(txn.getAmount());
        dto.setMode(txn.getPaymentMode());
        dto.setTransactionRef(txn.getTransactionRef());
        dto.setTransactionDate(txn.getTransactionDate());
        return dto;
    }

    // ✅ Mapper method (Entity → DTO)
    private LoanTransactionDTO mapToDTO(LoanTransaction txn) {
        LoanTransactionDTO dto = new LoanTransactionDTO();
        dto.setLoanId(txn.getLoan().getId());
        dto.setAgentId(txn.getAgent().getId());
        dto.setCustomerId(txn.getCustomer().getId());
        dto.setAmount(txn.getAmount());
        dto.setMode(txn.getPaymentMode());
        dto.setTransactionRef(txn.getTransactionRef());
        return dto;
    }
    
    private LoanTransactionAdminResponseDTO mapForAdminDTO(LoanTransaction txn) {
    	LoanTransactionAdminResponseDTO dto=new LoanTransactionAdminResponseDTO();
    	dto.setTransactionId(txn.getId());
    	dto.setLoanId(txn.getLoan().getId());
        dto.setAgentName(txn.getAgent().getName());
        dto.setCustomerName(txn.getCustomer().getFirstName()+" "+txn.getCustomer().getMiddleName()+" "+txn.getCustomer().getLastName());
        dto.setAmount(txn.getAmount());
        dto.setMode(txn.getPaymentMode());
        dto.setTransactionRef(txn.getTransactionRef());
        dto.setTransactionDate(txn.getTransactionDate());
    	return dto;
    }
}
