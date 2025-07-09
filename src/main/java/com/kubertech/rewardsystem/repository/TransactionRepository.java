package com.kubertech.rewardsystem.repository;

import com.kubertech.rewardsystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCustomerIdAndTransactionDateBetween(String customerId, LocalDate startDate, LocalDate endDate);
}
