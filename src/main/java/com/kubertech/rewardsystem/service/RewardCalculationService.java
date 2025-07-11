package com.kubertech.rewardsystem.service;

import com.kubertech.rewardsystem.exception.ResourceNotFoundException;
import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.CustomerBasicDTO;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
import com.kubertech.rewardsystem.repository.CustomerRepository;
import com.kubertech.rewardsystem.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardCalculationService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

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
        log.info("Calculating rewards for customer {} from {} to {}", customerId, startDate, endDate);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

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

    public int calculateRewardPoints(double amount) {
        if (amount <= 50) return 0;
        if (amount <= 100) return (int) (amount - 50);
        return 50 + 2 * (int) (amount - 100);
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<CustomerBasicDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(c -> {
                   return CustomerBasicDTO.builder().id(c.getId()).name(c.getName()).build();
                })
                .toList();
    }


    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
    }

    public Customer updateCustomer(String id, Customer updatedCustomer) {
        Customer existing = getCustomerById(id);
        existing.setName(updatedCustomer.getName());
        return customerRepository.save(existing);
    }

    public void deleteCustomer(String id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getCustomer() == null || transaction.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer ID must be provided");
        }

        Customer customer = customerRepository.findById(transaction.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + transaction.getCustomer().getId()));

        transaction.setCustomer(customer);

        return transactionRepository.save(transaction);
    }


    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
    }

    public Transaction updateTransaction(String id, Transaction updatedTransaction) {
        Transaction existing = transactionRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + id));


        if (updatedTransaction.getCustomer() == null || updatedTransaction.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer ID must be provided");
        }

        Customer customer = customerRepository.findById(updatedTransaction.getCustomer().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + updatedTransaction.getCustomer().getId()));

        existing.setAmount(updatedTransaction.getAmount());
        existing.setTransactionDate(updatedTransaction.getTransactionDate());
        existing.setCustomer(customer);

        return transactionRepository.save(existing);
    }

    public void deleteTransaction(String id) {
        Transaction tx = getTransactionById(id);
        transactionRepository.delete(tx);
    }
}
