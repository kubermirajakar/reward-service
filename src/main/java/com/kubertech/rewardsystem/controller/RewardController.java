package com.kubertech.rewardsystem.controller;

import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.CustomerBasicDTO;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
import com.kubertech.rewardsystem.service.RewardCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@Slf4j
@RequiredArgsConstructor
public class RewardController {

    private final RewardCalculationService rewardCalculationService;

    @GetMapping
    public ResponseEntity<List<RewardSummary>> fetchAllRewardsSummary() {
        log.info("API called: fetchAllRewardsSummary");
        return ResponseEntity.ok(rewardCalculationService.getAllRewardSummaries());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummary> fetchCustomerRewards(
            @PathVariable String customerId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("API called: fetchCustomerRewards for {}", customerId);
        return ResponseEntity.ok(rewardCalculationService.getCustomerRewards(customerId, startDate, endDate));
    }

    // CRUD - Customers
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        return ResponseEntity.ok(rewardCalculationService.createCustomer(customer));
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerBasicDTO>> getAllCustomers() {
        return ResponseEntity.ok(rewardCalculationService.getAllCustomers());
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        return ResponseEntity.ok(rewardCalculationService.getCustomerById(id));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @Valid @RequestBody Customer customer) {
        return ResponseEntity.ok(rewardCalculationService.updateCustomer(id, customer));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String id) {
        rewardCalculationService.deleteCustomer(id);
        return ResponseEntity.ok("Customer has been deleted successfully");
    }

    @PostMapping("/transactions")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(rewardCalculationService.createTransaction(transaction));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(rewardCalculationService.getAllTransactions());
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String id) {
        return ResponseEntity.ok(rewardCalculationService.getTransactionById(id));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id, @Valid @RequestBody Transaction transaction) {
        return ResponseEntity.ok(rewardCalculationService.updateTransaction(id, transaction));
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String id) {
        rewardCalculationService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction has been deleted successfully");
    }
}
