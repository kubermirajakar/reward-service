package com.kubertech.rewardsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubertech.rewardsystem.controller.RewardController;
import com.kubertech.rewardsystem.model.*;
import com.kubertech.rewardsystem.service.RewardService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test class for {@link RewardController}.
 * <p>
 * Uses Spring's {@link WebMvcTest} to simulate HTTP interactions and verify controller behavior.
 * Mocks the {@link RewardService} to isolate business logic from endpoint testing.
 */
@WebMvcTest(controllers = RewardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RewardControllerTest {

	/** Mocked HTTP client to simulate requests and validate responses. */
	@Autowired
	private MockMvc mockMvc;

	/** Object mapper for serializing/deserializing JSON payloads. */
	@Autowired
	private ObjectMapper objectMapper;

	/** Mocked service layer injected into the controller. */
	@MockitoBean
	private RewardService rewardService;

	/** Reusable mock summary object used in various test cases. */
	private RewardSummary mockSummary;

	/**
	 * Initializes test data before each test execution.
	 * <p>
	 * Creates a sample {@link MonthlyPointDTO} and embeds it in a {@link RewardSummary}.
	 */
	@BeforeEach
	void setUp() {
		MonthlyPointDTO monthlyPoint = MonthlyPointDTO.builder()
				.year(2025)
				.month("June")
				.points(120)
				.build();

		mockSummary = RewardSummary.builder()
				.customerId(1L)
				.customerName("Test User")
				.totalPoints(120)
				.monthlyPoints(List.of(monthlyPoint))
				.build();
	}

	/**
	 * Tests the creation of a new customer via POST request.
	 *
	 * @throws Exception if the request fails
	 */
	@Test
	void shouldCreateCustomerAndReturn201() throws Exception {
		Customer customer = Customer.builder().id(1L).name("Alice").build();
		Mockito.when(rewardService.createCustomer(any())).thenReturn(customer);

		mockMvc.perform(post("/api/rewards/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Alice"));
	}

	/**
	 * Tests the creation of a transaction associated with a customer.
	 *
	 * @throws Exception if the request fails
	 */
	@Test
	void shouldCreateTransactionAndReturn201() throws Exception {
		Transaction transaction = Transaction.builder()
				.id(1001L)
				.amount(100)
				.transactionDate(LocalDate.of(2025, 7, 11))
				.customer(Customer.builder().id(1L).build())
				.build();

		Mockito.when(rewardService.createTransaction(any())).thenReturn(transaction);

		mockMvc.perform(post("/api/rewards/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(transaction)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1001L))
				.andExpect(jsonPath("$.amount").value(100.0))
				.andExpect(jsonPath("$.transactionDate").value("2025-07-11"));
	}

	/**
	 * Tests retrieval of all reward summaries.
	 *
	 * @throws Exception if the request fails
	 */
	@Test
	void shouldFetchAllRewardsSummary() throws Exception {
		Mockito.when(rewardService.getAllRewardSummaries()).thenReturn(List.of(mockSummary));

		mockMvc.perform(get("/api/rewards"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].customerId").value(1L))
				.andExpect(jsonPath("$[0].totalPoints").value(120));
	}

	/**
	 * Tests retrieval of a customer's reward summary within a specified date range.
	 *
	 * @throws Exception if the request fails
	 */
	@Test
	void shouldFetchCustomerRewardSummaryByDateRange() throws Exception {
		Mockito.when(rewardService.getCustomerRewards(eq(1L), any(), any())).thenReturn(mockSummary);

		mockMvc.perform(get("/api/rewards/1")
						.param("startDate", "2025-06-01")
						.param("endDate", "2025-06-30")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value(1L))
				.andExpect(jsonPath("$.customerName").value("Test User"))
				.andExpect(jsonPath("$.totalPoints").value(120))
				.andExpect(jsonPath("$.monthlyPoints[0].month").value("June"))
				.andExpect(jsonPath("$.monthlyPoints[0].points").value(120));
	}
}