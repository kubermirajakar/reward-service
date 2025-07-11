package com.kubertech.rewardsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubertech.rewardsystem.controller.RewardController;
import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.CustomerBasicDTO;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RewardController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RewardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;


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
	void createCustomer_shouldReturnCustomer() throws Exception {
		Customer customer = Customer.builder().id("1").name("Alice").build();

		Mockito.when(rewardCalculationService.createCustomer(Mockito.any()))
				.thenReturn(customer);

		mockMvc.perform(post("/api/rewards/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(customer)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.name").value("Alice"));
	}

	@Test
	void getAllCustomers_shouldReturnList() throws Exception {
		List<CustomerBasicDTO> dtos = List.of(
				CustomerBasicDTO.builder().id("1").name("Alice").build()
		);

		Mockito.when(rewardCalculationService.getAllCustomers()).thenReturn(dtos);

		mockMvc.perform(get("/api/rewards/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value("1"))
				.andExpect(jsonPath("$[0].name").value("Alice"));
	}

	@Test
	void getCustomerById_shouldReturnCustomer() throws Exception {
		Customer customer = Customer.builder().id("1").name("Alice").build();

		Mockito.when(rewardCalculationService.getCustomerById("1")).thenReturn(customer);

		mockMvc.perform(get("/api/rewards/customers/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1"))
				.andExpect(jsonPath("$.name").value("Alice"));
	}

	@Test
	void updateCustomer_shouldReturnUpdatedCustomer() throws Exception {
		Customer updated = Customer.builder().id("1").name("Alice Updated").build();

		Mockito.when(rewardCalculationService.updateCustomer(Mockito.eq("1"), Mockito.any()))
				.thenReturn(updated);

		mockMvc.perform(put("/api/rewards/customers/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updated)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Alice Updated"));
	}

	@Test
	void deleteCustomer_shouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/api/rewards/customers/1"))
				.andExpect(status().isNoContent());

		Mockito.verify(rewardCalculationService).deleteCustomer("1");
	}

	@Test
	void createTransaction_shouldReturnTransaction() throws Exception {
		Transaction txn = Transaction.builder()
				.id(1001L)
				.amount(100)
				.transactionDate(LocalDate.of(2025, 7, 11))
				.build();

		Mockito.when(rewardCalculationService.createTransaction(Mockito.any()))
				.thenReturn(txn);

		mockMvc.perform(post("/api/rewards/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(txn)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1001L))
				.andExpect(jsonPath("$.amount").value(100))
				.andExpect(jsonPath("$.transactionDate").value("2025-07-11"));
	}

	@Test
	void getAllTransactions_shouldReturnList() throws Exception {
		List<Transaction> txns = List.of(
				Transaction.builder().id(1000L).amount(100).build()
		);

		Mockito.when(rewardCalculationService.getAllTransactions()).thenReturn(txns);

		mockMvc.perform(get("/api/rewards/transactions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1000L))
				.andExpect(jsonPath("$[0].amount").value(100));
	}

	@Test
	void getTransactionById_shouldReturnTransaction() throws Exception {
		Transaction txn = Transaction.builder().id(1000L).amount(100).build();

		Mockito.when(rewardCalculationService.getTransactionById("tx1")).thenReturn(txn);

		mockMvc.perform(get("/api/rewards/transactions/tx1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1000L))
				.andExpect(jsonPath("$.amount").value(100));
	}

	@Test
	void updateTransaction_shouldReturnUpdatedTransaction() throws Exception {
		Transaction updated = Transaction.builder()
				.id(1000L)
				.amount(150)
				.transactionDate(LocalDate.of(2025, 7, 11))
				.build();

		Mockito.when(rewardCalculationService.updateTransaction(Mockito.eq("1000"), Mockito.any()))
				.thenReturn(updated);

		mockMvc.perform(put("/api/rewards/transactions/1000")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updated)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1000))
				.andExpect(jsonPath("$.amount").value(150))
				.andExpect(jsonPath("$.transactionDate").value("2025-07-11"));
	}

	@Test
	void deleteTransaction_shouldReturnNoContent() throws Exception {
		mockMvc.perform(delete("/api/rewards/transactions/tx1"))
				.andExpect(status().isNoContent());

		Mockito.verify(rewardCalculationService).deleteTransaction("tx1");
	}



}