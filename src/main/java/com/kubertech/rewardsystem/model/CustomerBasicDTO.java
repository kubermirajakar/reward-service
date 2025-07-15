package com.kubertech.rewardsystem.model;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object representing basic customer details.
 * <p>
 * This DTO is typically used in contexts where full transaction data is not needed—
 * for example, when listing customers or referencing them in summaries.
 */
@Data
@Builder
public class CustomerBasicDTO {

    /**
     * Unique identifier of the customer.
     */
    private Long id;

    /**
     * Name of the customer.
     */
    private String name;
}