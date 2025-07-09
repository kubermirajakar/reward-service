package com.kubertech.rewardsystem;

import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
import com.kubertech.rewardsystem.repository.CustomerRepository;
import com.kubertech.rewardsystem.repository.TransactionRepository;
import com.kubertech.rewardsystem.service.RewardCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RewardPointsServiceApplicationTest {

	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private TransactionRepository transactionRepository;
	@InjectMocks
	private RewardCalculationService rewardCalculationService;
	private Customer customer;
	private List<Transaction> transactions;



	@BeforeEach
	void setUp() {
		transactions = List.of(
				Transaction.builder().amount(120).transactionDate(LocalDate.of(2025, 6, 1)).customer(customer).build(),
				Transaction.builder().amount(80).transactionDate(LocalDate.of(2025, 6, 2)).customer(customer).build()
		);

		customer = Customer.builder().id("C001").name("Test User").transactions(transactions).build();

}

	@Test
	void testGetCustomerRewards() {
		when(customerRepository.findById("C001")).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq("C001"), any(), any())).thenReturn(transactions);

		var summary = rewardCalculationService.getCustomerRewards("C001", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

		System.out.println(summary.getTotalPoints());
		assertEquals("C001", summary.getCustomerId());
		assertEquals("Test User", summary.getCustomerName());
		assertEquals(120, summary.getTotalPoints());
		assertEquals(1, summary.getMonthlyPoints().size());
		verify(customerRepository, times(1)).findById("C001");
	}

	@Test
	void testCalculateTotalPoints_EmptyTransactions() {
		when(customerRepository.findById("C001")).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq("C001"), any(), any())).thenReturn(Collections.emptyList());

		var summary = rewardCalculationService.getCustomerRewards("C001", LocalDate.now(), LocalDate.now());

		assertEquals(0, summary.getTotalPoints());
		assertTrue(summary.getMonthlyPoints().isEmpty());
	}

	@Test
	void testCustomerNotFound() {
		when(customerRepository.findById("INVALID")).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () ->
				rewardCalculationService.getCustomerRewards("INVALID", LocalDate.now(), LocalDate.now()));

		assertEquals("Customer not found with ID: INVALID", exception.getMessage());
	}
}
