package com.kubertech.rewardsystem;

import com.kubertech.rewardsystem.controller.RewardController;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.service.RewardCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RewardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RewardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RewardCalculationService rewardCalculationService;

	private RewardSummary mockSummary;

	@BeforeEach
	void setUp() {
		mockSummary = RewardSummary.builder()
				.customerId("C001")
				.customerName("Test User")
				.totalPoints(120)
				.monthlyPoints(Map.of("JUNE", 120))
				.build();
	}

	@Test
	void shouldFetchAllRewardsSummary() throws Exception {
		Mockito.when(rewardCalculationService.getAllRewardSummaries())
				.thenReturn(List.of(mockSummary));

		mockMvc.perform(get("/api/rewards"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].customerId").value("C001"))
				.andExpect(jsonPath("$[0].totalPoints").value(120));
	}

	@Test
	void shouldFetchRewardsByCustomerAndDateRange() throws Exception {
		Mockito.when(rewardCalculationService.getCustomerRewards(eq("C001"), any(), any()))
				.thenReturn(mockSummary);

		mockMvc.perform(get("/api/rewards/C001")
						.param("start", "2025-06-01")
						.param("end", "2025-06-30")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value("C001"))
				.andExpect(jsonPath("$.customerName").value("Test User"))
				.andExpect(jsonPath("$.totalPoints").value(120))
				.andExpect(jsonPath("$.monthlyPoints.JUNE").value(120));
	}


	@Test
	void shouldReturnRewardSummaryUsingExternalMultiplier() throws Exception {
		RewardSummary externalSummary = RewardSummary.builder()
				.customerId(mockSummary.getCustomerId())
				.customerName(mockSummary.getCustomerName())
				.totalPoints(180) // new expected total
				.monthlyPoints(Map.of("2025-06", 180))
				.transactions(List.of())
				.build();

		Mockito.when(rewardCalculationService.getCustomerRewardsUsingExternalMultiplier(
						"C001", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30)))
				.thenReturn(externalSummary);

		mockMvc.perform(get("/api/rewards/C001/external-summary")
						.param("start", "2025-06-01")
						.param("end", "2025-06-30")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value("C001"))
				.andExpect(jsonPath("$.customerName").value("Test User"))
				.andExpect(jsonPath("$.totalPoints").value(180))
				.andExpect(jsonPath("$.monthlyPoints.2025-06").value(180));
	}

}