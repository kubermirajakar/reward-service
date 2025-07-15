package com.kubertech.rewardsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a financial transaction associated with a customer.
 * <p>
 * Each transaction contributes to reward point calculations. This entity includes
 * the transaction amount, date, and a reference back to the owning customer.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    /**
     * Unique identifier for the transaction.
     * <p>
     * Auto-generated using the {@link GenerationType#IDENTITY} strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Monetary value of the transaction.
     * <p>
     * Must be a positive value as enforced by {@link DecimalMin}.
     */
    @DecimalMin(value = "0.0", message = "Amount must be positive")
    private double amount;

    /**
     * Date when the transaction occurred.
     * <p>
     * Cannot be null; validated with {@link NotNull}.
     */
    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    /**
     * The customer associated with the transaction.
     * <p>
     * Defined as a many-to-one relationship. Lazy fetching ensures the customer
     * is only loaded when accessed. {@link JsonBackReference} handles JSON serialization
     * to avoid circular references in bidirectional relationships.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer;
}