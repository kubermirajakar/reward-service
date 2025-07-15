package com.kubertech.rewardsystem.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

/**
 * Represents a customer in the reward system.
 * <p>
 * Each customer has a unique identifier, a name, and a list of associated transactions.
 * This entity is mapped to a relational database table using JPA annotations.
 * The {@code transactions} field is managed in a one-to-many relationship with cascading behavior.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    /**
     * Unique identifier for the customer.
     * <p>
     * Auto-generated using the {@link GenerationType#IDENTITY} strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the customer.
     * <p>
     * Must not be blank, enforced by {@link NotBlank} validation.
     */
    @NotBlank(message = "Customer name is mandatory")
    private String name;

    /**
     * List of transactions associated with the customer.
     * <p>
     * This defines a one-to-many relationship where:
     * - The {@code customer} field in {@link Transaction} is the owner of the relationship.
     * - All related transactions are cascaded and removed if the customer is deleted.
     * - {@link JsonManagedReference} is used for correct bidirectional serialization.
     */
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Transaction> transactions;
}
