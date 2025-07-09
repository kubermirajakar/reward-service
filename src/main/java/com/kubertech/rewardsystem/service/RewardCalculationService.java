package com.kubertech.rewardsystem.service;

import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
import com.kubertech.rewardsystem.repository.CustomerRepository;
import com.kubertech.rewardsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardCalculationService {
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final WebClient webClient;

    public List<RewardSummary> getAllRewardSummaries() {
        log.info("Fetching reward summaries for all customers...");
        List<Customer> customers = customerRepository.findAll();
        List<RewardSummary> summaries = new ArrayList<>();

        for (Customer customer : customers) {
            Map<String, Integer> monthlyPoints = new HashMap<>();

            for (Transaction tx : customer.getTransactions()) {
                String monthKey = tx.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
                int earnedPoints = calculateRewardPoints(tx.getAmount());
                monthlyPoints.merge(monthKey, earnedPoints, Integer::sum);
            }

            int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();
            summaries.add(RewardSummary.builder()
                    .customerId(customer.getId())
                    .customerName(customer.getName())
                    .monthlyPoints(monthlyPoints)
                    .totalPoints(totalPoints)
                    .transactions(customer.getTransactions())
                    .build());
        }
        return summaries;
    }

    public RewardSummary getCustomerRewards(String customerId, LocalDate startDate, LocalDate endDate) {
        log.info("Calculating rewards for customer: {} from {} to {}", customerId, startDate, endDate);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);

        Map<String, Integer> monthlyPoints = new HashMap<>();
        for (Transaction tx : transactions) {
            String monthKey = tx.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            int earnedPoints = calculateRewardPoints(tx.getAmount());
            monthlyPoints.merge(monthKey, earnedPoints, Integer::sum);
        }

        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

        return RewardSummary.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .monthlyPoints(monthlyPoints)
                .totalPoints(totalPoints)
                .transactions(transactions)
                .build();
    }

    private int calculateRewardPoints(double amount) {
        if (amount <= 50) return 0;
        if (amount <= 100) return (int) (amount - 50);
        return 50 + 2 * (int)(amount - 100);
    }


    public RewardSummary getCustomerRewardsUsingExternalMultiplier(String customerId, LocalDate startDate, LocalDate endDate) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);

        Map<String, Integer> monthlyPoints = new HashMap<>();

        for (Transaction tx : transactions) {
            String monthKey = tx.getTransactionDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            int basePoints = calculateRewardPoints(tx.getAmount());
            int multiplier = fetchRewardMultiplier(monthKey); // calls external microservice via WebClient
            int finalPoints = basePoints * multiplier;
            monthlyPoints.merge(monthKey, finalPoints, Integer::sum);
        }

        int totalPoints = monthlyPoints.values().stream().mapToInt(Integer::intValue).sum();

        return RewardSummary.builder()
                .customerId(customer.getId())
                .customerName(customer.getName())
                .transactions(transactions)
                .monthlyPoints(monthlyPoints)
                .totalPoints(totalPoints)
                .build();
    }
    public int fetchRewardMultiplier(String monthKey) {
        try {
            return webClient.get()
                    .uri("/api/reward-rules/{monthKey}", monthKey)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
        } catch (Exception e) {
            log.warn("Could not fetch multiplier for {}. Using default: {}", monthKey, e.getMessage());
            return 1;
        }
    }


}
