package com.kubertech.rewardsystem.model;

import lombok.*;
import java.util.List;

/**
 * Represents a summary of reward points for a customer.
 * <p>
 * This DTO includes customer identity, transaction history, monthly breakdowns,
 * and the overall total of reward points earned across a defined period.
 * Useful for reporting, analytics, or API responses to present consolidated reward data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RewardSummary {

    /**
     * Unique identifier of the customer.
     */
    private Long customerId;

    /**
     * Name of the customer.
     */
    private String customerName;

    /**
     * List of transactions associated with the customer.
     * Each transaction contributes to the reward calculation.
     */
    private List<Transaction> transactions;

    /**
     * Monthly breakdown of reward points.
     * Includes year, month, and earned points per entry.
     */
    private List<MonthlyPointDTO> monthlyPoints;

    /**
     * Total reward points accumulated by the customer across all transactions.
     */
    private int totalPoints;
}