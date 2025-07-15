package com.kubertech.rewardsystem.service;

import com.kubertech.rewardsystem.exception.ResourceNotFoundException;
import com.kubertech.rewardsystem.model.*;
import com.kubertech.rewardsystem.repository.CustomerRepository;
import com.kubertech.rewardsystem.repository.TransactionRepository;
import com.kubertech.rewardsystem.utility.RewardPointsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Service class that encapsulates business logic for managing customers,
 * transactions, and calculating reward points in the Reward System application.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RewardService {

    /** Repository for accessing {@link Customer} data. */
    private final CustomerRepository customerRepository;

    /** Repository for accessing {@link Transaction} data. */
    private final TransactionRepository transactionRepository;

    /**
     * Creates and persists a new customer.
     *
     * @param customer the {@link Customer} entity to be saved
     * @return the saved {@link Customer}
     */
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Creates and persists a transaction associated with a customer.
     *
     * @param transaction the {@link Transaction} to be saved
     * @return the saved {@link Transaction}
     * @throws IllegalArgumentException if the customer ID is missing
     * @throws ResourceNotFoundException if the customer does not exist
     */
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getCustomer() == null || transaction.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer ID must be provided");
        }

        Customer customer = customerRepository.findById(transaction.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + transaction.getCustomer().getId()));

        transaction.setCustomer(customer);

        return transactionRepository.save(transaction);
    }

    /**
     * Retrieves a list of {@link RewardSummary} for all customers.
     *
     * @return a list of aggregated reward summaries
     */
    public List<RewardSummary> getAllRewardSummaries() {
        log.info("Fetching reward summaries for all customers...");

        List<Customer> customers = customerRepository.findAll();
        List<RewardSummary> summaries = new ArrayList<>();

        for (Customer customer : customers) {
            summaries.add(buildRewardSummary(customer, customer.getTransactions()));
        }
        return summaries;
    }

    /**
     * Calculates the reward summary for a specific customer within a date range.
     *
     * @param customerId the ID of the customer
     * @param startDate  start date of the range
     * @param endDate    end date of the range
     * @return a {@link RewardSummary} containing monthly breakdown and total points
     * @throws ResourceNotFoundException if the customer does not exist
     */
    public RewardSummary getCustomerRewards(Long customerId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating rewards for customer {} from {} to {}", customerId, startDate, endDate);
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);

        return buildRewardSummary(customer, transactions);
    }

    /**
     * Constructs a {@link RewardSummary} by calculating monthly and total points.
     *
     * @param customer     the customer whose transactions are being evaluated
     * @param transactions the list of transactions during the target period
     * @return a {@link RewardSummary} with calculated points
     */
    private RewardSummary buildRewardSummary(Customer customer, List<Transaction> transactions) {
        Map<String, Integer> monthlyPoints = new HashMap<>();

        for (Transaction tx : transactions) {
            String monthKey = tx.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            int earnedPoints = RewardPointsUtil.calculateRewardPoints(tx.getAmount());
            monthlyPoints.merge(monthKey, earnedPoints, Integer::sum);
        }

        List<MonthlyPointDTO> formattedMonthlyPoints = monthlyPoints.entrySet().stream()
                .map(entry -> {
                    YearMonth yearMonth = YearMonth.parse(entry.getKey());
                    String monthName = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    return new MonthlyPointDTO(yearMonth.getYear(), monthName, entry.getValue());
                })
                .toList();

        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

        return RewardSummary.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .monthlyPoints(formattedMonthlyPoints)
                .totalPoints(totalPoints)
                .transactions(transactions)
                .build();
    }
}