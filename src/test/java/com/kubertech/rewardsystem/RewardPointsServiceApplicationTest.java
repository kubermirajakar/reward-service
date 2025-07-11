package com.kubertech.rewardsystem;

import com.kubertech.rewardsystem.exception.ResourceNotFoundException;
import com.kubertech.rewardsystem.model.Customer;
import com.kubertech.rewardsystem.model.RewardSummary;
import com.kubertech.rewardsystem.model.Transaction;
import com.kubertech.rewardsystem.repository.CustomerRepository;
import com.kubertech.rewardsystem.repository.TransactionRepository;
import com.kubertech.rewardsystem.service.RewardCalculationService;
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

		Exception exception = assertThrows(ResourceNotFoundException.class, () ->
				rewardCalculationService.getCustomerRewards("INVALID", LocalDate.now(), LocalDate.now()));

		assertEquals("Customer not found with ID: INVALID", exception.getMessage());
	}

	@Test
	void createCustomer_shouldSaveCustomer() {
		Customer customer = Customer.builder().id("1").name("Kuber").build();

		Mockito.when(customerRepository.save(customer)).thenReturn(customer);

		Customer result = rewardCalculationService.createCustomer(customer);

		Assertions.assertEquals("1", result.getId());
		Assertions.assertEquals("Kuber", result.getName());
	}

	@Test
	void getCustomerById_shouldReturnCustomer() {
		Customer customer = Customer.builder().id("1").name("Kuber").build();
		Mockito.when(customerRepository.findById("1")).thenReturn(Optional.of(customer));

		Customer result = rewardCalculationService.getCustomerById("1");

		Assertions.assertEquals("Kuber", result.getName());
	}

	@Test
	void getCustomerById_shouldThrowIfNotFound() {
		Mockito.when(customerRepository.findById("99")).thenReturn(Optional.empty());

		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> rewardCalculationService.getCustomerById("99"));
	}

	@Test
	void updateCustomer_shouldModifyAndSave() {
		Customer existing = Customer.builder().id("1").name("OldName").build();
		Customer updated = Customer.builder().id("1").name("NewName").build();

		Mockito.when(customerRepository.findById("1")).thenReturn(Optional.of(existing));
		Mockito.when(customerRepository.save(Mockito.any())).thenReturn(updated);

		Customer result = rewardCalculationService.updateCustomer("1", updated);

		Assertions.assertEquals("NewName", result.getName());
	}

	@Test
	void deleteCustomer_shouldRemoveCustomer() {
		Customer existing = Customer.builder().id("1").name("Kuber").build();
		Mockito.when(customerRepository.findById("1")).thenReturn(Optional.of(existing));

		rewardCalculationService.deleteCustomer("1");

		Mockito.verify(customerRepository).delete(existing);
	}

	@Test
	void createTransaction_shouldAttachCustomerAndSave() {
		Customer customer = Customer.builder().id("1").name("Kuber").build();
		Transaction txn = Transaction.builder().id(100L).amount(120).customer(customer).build();

		Mockito.when(customerRepository.findById("1")).thenReturn(Optional.of(customer));
		Mockito.when(transactionRepository.save(txn)).thenReturn(txn);

		Transaction result = rewardCalculationService.createTransaction(txn);

		Assertions.assertEquals(120, result.getAmount());
		Assertions.assertEquals("Kuber", result.getCustomer().getName());
	}

	@Test
	void createTransaction_shouldFailIfCustomerMissing() {
		Transaction txn = Transaction.builder().id(101L).amount(100).customer(null).build();

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> rewardCalculationService.createTransaction(txn));
	}

	@Test
	void getTransactionById_shouldReturnTransaction() {
		Transaction txn = Transaction.builder().id(100L).amount(75).build();
		Mockito.when(transactionRepository.findById(100L)).thenReturn(Optional.of(txn));

		Transaction result = rewardCalculationService.getTransactionById("100");

		Assertions.assertEquals(75, result.getAmount());
	}

	@Test
	void updateTransaction_shouldReplaceFieldsAndSave() {
		Customer customer = Customer.builder().id("1").name("Kuber").build();
		Transaction existing = Transaction.builder().id(100L).amount(60).build();
		Transaction updated = Transaction.builder().id(100L).amount(150).transactionDate(LocalDate.of(2025, 7, 11)).customer(customer).build();

		Mockito.when(transactionRepository.findById(100L)).thenReturn(Optional.of(existing));
		Mockito.when(customerRepository.findById("1")).thenReturn(Optional.of(customer));
		Mockito.when(transactionRepository.save(Mockito.any())).thenReturn(updated);

		Transaction result = rewardCalculationService.updateTransaction("100", updated);

		Assertions.assertEquals(150, result.getAmount());
		Assertions.assertEquals(LocalDate.of(2025, 7, 11), result.getTransactionDate());
	}

	@Test
	void calculateRewardPoints_shouldReturnCorrectValues() {
		Assertions.assertEquals(0, rewardCalculationService.calculateRewardPoints(40));
		Assertions.assertEquals(30, rewardCalculationService.calculateRewardPoints(80));
		Assertions.assertEquals(90, rewardCalculationService.calculateRewardPoints(120));
	}

	@Test
	void getAllRewardSummaries_shouldAggregateCorrectly() {
		Transaction tx1 = Transaction.builder().amount(120).transactionDate(LocalDate.of(2025, 7, 1)).build();
		Transaction tx2 = Transaction.builder().amount(90).transactionDate(LocalDate.of(2025, 7, 15)).build();

		Customer customer = Customer.builder().id("1").name("Kuber").transactions(List.of(tx1, tx2)).build();
		Mockito.when(customerRepository.findAll()).thenReturn(List.of(customer));

		List<RewardSummary> result = rewardCalculationService.getAllRewardSummaries();

		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals(90 + 40, result.get(0).getTotalPoints());
		Assertions.assertTrue(result.get(0).getMonthlyPoints().containsKey("2025-07"));
	}

	@Test
	void getCustomerRewards_shouldFilterByDateAndCalculate() {
		Customer customer = Customer.builder().id("1").name("Kuber").build();
		Transaction tx = Transaction.builder().amount(130).transactionDate(LocalDate.of(2025, 6, 5)).build();

		Mockito.when(customerRepository.findById("1")).thenReturn(Optional.of(customer));
		Mockito.when(transactionRepository.findByCustomerIdAndTransactionDateBetween("1", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30)))
				.thenReturn(List.of(tx));

		RewardSummary summary = rewardCalculationService.getCustomerRewards("1", LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30));

		Assertions.assertEquals("Kuber", summary.getCustomerName());
		Assertions.assertEquals(110, summary.getTotalPoints());
	}
}
