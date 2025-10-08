package com.project.pawnprime.mapper;

import com.project.pawnprime.dto.transaction.TransactionResponseDTO;
import com.project.pawnprime.model.LoanTransaction;

import java.util.List;
import java.util.stream.Collectors;

public class LoanTransactionMapper {

    public static TransactionResponseDTO toDTO(LoanTransaction txn) {
        if (txn == null) {
            return null;
        }

        return new TransactionResponseDTO(
                txn.getId(),
                txn.getLoan() != null ? txn.getLoan().getId() : null,
                txn.getAgent() != null ? txn.getAgent().getId() : null,
                txn.getAgent().getEmail(),
                txn.getAgent().getName(),
                txn.getCustomer().getFirstName()+" "+txn.getCustomer().getLastName(),
                txn.getCustomer() != null ? txn.getCustomer().getId() : null,
                txn.getAmount(),
                txn.getPaymentMode(),
                txn.getTransactionRef(),
                txn.getTransactionDate()
                
        );
    }

    public static List<TransactionResponseDTO> toDTOList(List<LoanTransaction> txns) {
        return txns.stream()
                .map(LoanTransactionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
