package com.kubertech.rewardsystem.repository;

import com.kubertech.rewardsystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing {@link Transaction} entities.
 * <p>
 * Provides standard CRUD operations and custom query methods
 * for filtering transactions by customer and date range.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions for a specific customer within a given date range.
     *
     * @param customerId the ID of the customer whose transactions are to be retrieved
     * @param startDate  the start date of the range (inclusive)
     * @param endDate    the end date of the range (inclusive)
     * @return a list of {@link Transaction} objects matching the criteria
     */
    List<Transaction> findByCustomerIdAndTransactionDateBetween(Long customerId, LocalDate startDate, LocalDate endDate);
}