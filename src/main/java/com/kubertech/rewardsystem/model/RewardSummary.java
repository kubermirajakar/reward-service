package com.kubertech.rewardsystem.model;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RewardSummary {
    private String customerId;
    private String customerName;
    private Map<String, Integer> monthlyPoints;
    private int totalPoints;
    private List<Transaction> transactions;
}