package com.kubertech.rewardsystem.controller;

import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.service.RewardCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/{customerId}/external-summary")
    public ResponseEntity<RewardSummary> fetchRewardSummaryUsingExternalMultipliers(
            @PathVariable String customerId,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        log.info("API called: external reward summary for {}", customerId);
        RewardSummary summary = rewardCalculationService.getCustomerRewardsUsingExternalMultiplier(customerId, start, end);
        return ResponseEntity.ok(summary);
    }


}