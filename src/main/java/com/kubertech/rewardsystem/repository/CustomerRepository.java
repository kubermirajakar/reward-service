package com.kubertech.rewardsystem.repository;

import com.kubertech.rewardsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and managing {@link Customer} entities.
 * <p>
 * Provides standard CRUD operations and query methods via Spring Data JPA.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}