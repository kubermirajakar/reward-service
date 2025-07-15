package com.kubertech.rewardsystem.service;

import com.kubertech.rewardsystem.exception.ResourceNotFoundException;
import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
import com.kubertech.rewardsystem.repository.CustomerRepository;
import com.kubertech.rewardsystem.repository.TransactionRepository;
import com.kubertech.rewardsystem.service.RewardService;
import com.kubertech.rewardsystem.utility.RewardPointsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link RewardService}.
 * <p>
 * Tests reward point calculation, customer and transaction creation,
 * and summary aggregation logic using mocked repositories.
 */
@SpringBootTest
class RewardServiceTest {

	/** Mock repository for customer entities. */
	@Mock
	private CustomerRepository customerRepository;

	/** Mock repository for transaction entities. */
	@Mock
	private TransactionRepository transactionRepository;

	/** Injected service under test. */
	@InjectMocks
	private RewardService rewardService;

	private Customer customer;
	private List<Transaction> transactions;

	/**
	 * Sets up mock data for reuse in multiple tests.
	 * Initializes customer and a list of transactions.
	 */
	@BeforeEach
	void setUp() {
		transactions = List.of(
				Transaction.builder().amount(120).transactionDate(LocalDate.of(2025, 6, 1)).customer(customer).build(),
				Transaction.builder().amount(80).transactionDate(LocalDate.of(2025, 6, 2)).customer(customer).build()
		);

		customer = Customer.builder().id(1L).name("Test User").transactions(transactions).build();
	}

	/**
	 * Tests reward summary calculation for a valid customer with two transactions.
	 */
	@Test
	void testGetCustomerRewards() {
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(1L), any(), any()))
				.thenReturn(transactions);

		RewardSummary summary = rewardService.getCustomerRewards(1L, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

		assertEquals(1L, summary.getCustomerId());
		assertEquals("Test User", summary.getCustomerName());
		assertEquals(120, summary.getTotalPoints()); // 90 + 30
		assertEquals(1, summary.getMonthlyPoints().size());
		verify(customerRepository, times(1)).findById(1L);
	}

	/**
	 * Verifies that total points are zero when no transactions exist.
	 */
	@Test
	void testCalculateTotalPoints_EmptyTransactions() {
		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(eq(1L), any(), any()))
				.thenReturn(Collections.emptyList());

		RewardSummary summary = rewardService.getCustomerRewards(1L, LocalDate.now(), LocalDate.now());

		assertEquals(0, summary.getTotalPoints());
		assertTrue(summary.getMonthlyPoints().isEmpty());
	}

	/**
	 * Verifies behavior when customer ID does not exist.
	 */
	@Test
	void testCustomerNotFound() {
		when(customerRepository.findById(-1L)).thenReturn(Optional.empty());

		Exception exception = assertThrows(ResourceNotFoundException.class, () ->
				rewardService.getCustomerRewards(-1L, LocalDate.now(), LocalDate.now()));

		assertEquals("Customer not found with ID: -1", exception.getMessage());
	}

	/**
	 * Tests creation of a new customer.
	 */
	@Test
	void createCustomer_shouldSaveCustomer() {
		Customer customer = Customer.builder().id(1L).name("Kuber").build();
		when(customerRepository.save(customer)).thenReturn(customer);

		Customer result = rewardService.createCustomer(customer);

		assertEquals(1L, result.getId());
		assertEquals("Kuber", result.getName());
	}

	/**
	 * Tests creation of a transaction with a valid associated customer.
	 */
	@Test
	void createTransaction_shouldAttachCustomerAndSave() {
		Customer customer = Customer.builder().id(1L).name("Kuber").build();
		Transaction txn = Transaction.builder().id(100L).amount(120).customer(customer).build();

		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(transactionRepository.save(txn)).thenReturn(txn);

		Transaction result = rewardService.createTransaction(txn);

		assertEquals(120, result.getAmount());
		assertEquals("Kuber", result.getCustomer().getName());
	}

	/**
	 * Verifies that transaction creation fails if customer is missing.
	 */
	@Test
	void createTransaction_shouldFailIfCustomerMissing() {
		Transaction txn = Transaction.builder().id(101L).amount(100).customer(null).build();

		assertThrows(IllegalArgumentException.class, () -> rewardService.createTransaction(txn));
	}

	/**
	 * Verifies that reward point utility calculates correct values.
	 */
	@Test
	void calculateRewardPoints_shouldReturnCorrectValues() {
		assertEquals(0, RewardPointsUtil.calculateRewardPoints(40));
		assertEquals(30, RewardPointsUtil.calculateRewardPoints(80));    // 80-50
		assertEquals(90, RewardPointsUtil.calculateRewardPoints(120));   // 50 + 2*(120-100)
	}

	/**
	 * Tests summary aggregation across multiple transactions and checks monthly breakdown.
	 */
	@Test
	void getAllRewardSummaries_shouldAggregateCorrectly() {
		Transaction tx1 = Transaction.builder().amount(120).transactionDate(LocalDate.of(2025, 7, 1)).build();
		Transaction tx2 = Transaction.builder().amount(90).transactionDate(LocalDate.of(2025, 7, 15)).build();
		Customer customer = Customer.builder().id(1L).name("Kuber").transactions(List.of(tx1, tx2)).build();

		when(customerRepository.findAll()).thenReturn(List.of(customer));

		List<RewardSummary> result = rewardService.getAllRewardSummaries();

		boolean containsJuly = result.get(0).getMonthlyPoints().stream()
				.anyMatch(dto -> dto.getYear() == 2025 && "July".equals(dto.getMonth()));

		assertEquals(1, result.size());
		assertEquals(130, result.get(0).getTotalPoints()); // 90 + 40
		assertTrue(containsJuly);
	}

	/**
	 * Verifies filtered reward calculation by date range for one transaction.
	 */
	@Test
	void getCustomerRewards_shouldFilterByDateAndCalculate() {
		Customer customer = Customer.builder().id(1L).name("Kuber").build();
		Transaction tx = Transaction.builder().amount(130).transactionDate(LocalDate.of(2025, 6, 5)).build();

		when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
		when(transactionRepository.findByCustomerIdAndTransactionDateBetween(1L, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30)))
				.thenReturn(List.of(tx));

		RewardSummary summary = rewardService.getCustomerRewards(1L, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

		assertEquals("Kuber", summary.getCustomerName());
		assertEquals(110, summary.getTotalPoints()); // 50 + 2*(130-100)
	}
}