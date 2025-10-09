package com.project.pawnprime.repo;

import com.project.pawnprime.model.OnlineRepaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OnlineRepaymentTransactionRepository extends JpaRepository<OnlineRepaymentTransaction, Long> {

	List<OnlineRepaymentTransaction> findByLoanId(Long loanId);

	List<OnlineRepaymentTransaction> findByStatus(String status);

	Optional<OnlineRepaymentTransaction> findByOrderId(String orderId);

	Optional<OnlineRepaymentTransaction> findByPaymentId(String paymentId);
	
	List<OnlineRepaymentTransaction> findByLoanAgentId(Long agentId);

}
