package com.kubertech.rewardsystem.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing reward points earned by a customer in a specific month and year.
 * <p>
 * This class is useful for constructing time-based reward summaries and analytics.
 */
@Data
@Builder
@AllArgsConstructor
public class MonthlyPointDTO {

    /**
     * The calendar year for which the reward points are calculated.
     */
    private int year;

    /**
     * The name of the month (e.g., "January", "Feb", or any preferred format).
     */
    private String month;

    /**
     * Total reward points accumulated by the customer during the specified month.
     */
    private int points;
}