package com.kubertech.rewardsystem.controller;

import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
import com.kubertech.rewardsystem.service.RewardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for handling reward-related operations.
 * <p>
 * This controller provides endpoints for managing customers, transactions,
 * and fetching reward summaries using standard HTTP methods.
 */
@RestController
@RequestMapping("/api/rewards")
@Slf4j
@RequiredArgsConstructor
public class RewardController {

    /** The service layer for handling reward logic. */
    private final RewardService rewardService;

    /**
     * Creates a new customer.
     *
     * @param customer the {@link Customer} object to create, validated before processing
     * @return {@link ResponseEntity} containing the saved customer and HTTP 201 status
     */
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        Customer savedCustomer = rewardService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    /**
     * Creates a new transaction associated with a customer.
     *
     * @param transaction the {@link Transaction} object to create, validated before processing
     * @return {@link ResponseEntity} containing the saved transaction and HTTP 201 status
     */
    @PostMapping("/transactions")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction savedTransaction = rewardService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
    }

    /**
     * Retrieves reward summaries for all customers.
     *
     * @return {@link ResponseEntity} with a list of {@link RewardSummary} for all customers
     */
    @GetMapping
    public ResponseEntity<List<RewardSummary>> fetchAllRewardsSummary() {
        log.info("API called: fetchAllRewardsSummary");
        return ResponseEntity.ok(rewardService.getAllRewardSummaries());
    }

    /**
     * Retrieves the reward summary for a specific customer within a date range.
     *
     * @param customerId the ID of the customer
     * @param startDate  the start date of the reward calculation range (ISO format)
     * @param endDate    the end date of the reward calculation range (ISO format)
     * @return {@link ResponseEntity} containing the {@link RewardSummary} for the given customer
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummary> getCustomerRewardSummary(
            @PathVariable Long customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Retrieves a detailed reward summary for a given customer based on transactions within the specified date range");
        RewardSummary summary = rewardService.getCustomerRewards(customerId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
}