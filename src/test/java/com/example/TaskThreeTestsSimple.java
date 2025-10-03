package com.example;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.TransactionValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskThreeTests - H2 database transaction validation test focused on 'waldorf' user debugging
 * This test bypasses Kafka and tests transaction validation directly
 */
@DataJpaTest
@Import(TransactionValidationService.class)
@ActiveProfiles("test")
public class TaskThreeTestsSimple {

    @Autowired
    private TransactionValidationService transactionValidationService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        userRepository.deleteAll();
        
        // Create test users
        createTestUsers();
    }

    /**
     * Create test users including 'waldorf' user for debugging
     */
    private void createTestUsers() {
        List<User> users = new ArrayList<>();
        
        // Create waldorf user with initial balance
        User waldorf = new User("waldorf", new BigDecimal("1000.00"));
        users.add(waldorf);
        
        // Create other test users
        User alice = new User("alice", new BigDecimal("500.00"));
        users.add(alice);
        
        User bob = new User("bob", new BigDecimal("750.00"));
        users.add(bob);
        
        User charlie = new User("charlie", new BigDecimal("300.00"));
        users.add(charlie);
        
        // Save all users
        userRepository.saveAll(users);
        
        System.out.println("Created test users:");
        for (User user : users) {
            System.out.println("- " + user.getUsername() + ": $" + user.getBalance());
        }
    }

    /**
     * Main test method - process all transactions and debug waldorf balance
     */
    @Test
    void testTransactionProcessingAndDebugWaldorfBalance() {
        System.out.println("\n=== STARTING TRANSACTION PROCESSING ===");
        
        // Get initial waldorf balance
        User waldorfBefore = userRepository.findByUsername("waldorf").orElse(null);
        assertNotNull(waldorfBefore, "Waldorf user should exist");
        BigDecimal initialBalance = waldorfBefore.getBalance();
        System.out.println("Waldorf initial balance: $" + initialBalance);
        
        // Get user IDs for transactions
        User alice = userRepository.findByUsername("alice").orElse(null);
        User bob = userRepository.findByUsername("bob").orElse(null);
        User charlie = userRepository.findByUsername("charlie").orElse(null);
        
        assertNotNull(alice, "Alice user should exist");
        assertNotNull(bob, "Bob user should exist");
        assertNotNull(charlie, "Charlie user should exist");
        
        // Process transactions that will affect waldorf's balance
        int successfulTransactions = 0;
        BigDecimal expectedBalance = initialBalance;
        
        System.out.println("\nProcessing transaction 1: waldorf -> alice: $100.00");
        boolean success1 = transactionValidationService.validateAndProcessTransaction(
            waldorfBefore.getId(), alice.getId(), new BigDecimal("100.00"));
        if (success1) {
            successfulTransactions++;
            expectedBalance = expectedBalance.subtract(new BigDecimal("100.00"));
            System.out.println("✓ Transaction successful - Expected balance: $" + expectedBalance);
        } else {
            System.out.println("✗ Transaction failed");
        }
        
        System.out.println("\nProcessing transaction 2: bob -> waldorf: $50.00");
        boolean success2 = transactionValidationService.validateAndProcessTransaction(
            bob.getId(), waldorfBefore.getId(), new BigDecimal("50.00"));
        if (success2) {
            successfulTransactions++;
            expectedBalance = expectedBalance.add(new BigDecimal("50.00"));
            System.out.println("✓ Transaction successful - Expected balance: $" + expectedBalance);
        } else {
            System.out.println("✗ Transaction failed");
        }
        
        System.out.println("\nProcessing transaction 3: waldorf -> charlie: $75.00");
        boolean success3 = transactionValidationService.validateAndProcessTransaction(
            waldorfBefore.getId(), charlie.getId(), new BigDecimal("75.00"));
        if (success3) {
            successfulTransactions++;
            expectedBalance = expectedBalance.subtract(new BigDecimal("75.00"));
            System.out.println("✓ Transaction successful - Expected balance: $" + expectedBalance);
        } else {
            System.out.println("✗ Transaction failed");
        }
        
        System.out.println("\nProcessing transaction 4: alice -> waldorf: $25.00");
        boolean success4 = transactionValidationService.validateAndProcessTransaction(
            alice.getId(), waldorfBefore.getId(), new BigDecimal("25.00"));
        if (success4) {
            successfulTransactions++;
            expectedBalance = expectedBalance.add(new BigDecimal("25.00"));
            System.out.println("✓ Transaction successful - Expected balance: $" + expectedBalance);
        } else {
            System.out.println("✗ Transaction failed");
        }
        
        System.out.println("\nProcessing transaction 5: waldorf -> bob: $200.00");
        boolean success5 = transactionValidationService.validateAndProcessTransaction(
            waldorfBefore.getId(), bob.getId(), new BigDecimal("200.00"));
        if (success5) {
            successfulTransactions++;
            expectedBalance = expectedBalance.subtract(new BigDecimal("200.00"));
            System.out.println("✓ Transaction successful - Expected balance: $" + expectedBalance);
        } else {
            System.out.println("✗ Transaction failed");
        }
        
        System.out.println("\nProcessing transaction 6: charlie -> waldorf: $150.00");
        boolean success6 = transactionValidationService.validateAndProcessTransaction(
            charlie.getId(), waldorfBefore.getId(), new BigDecimal("150.00"));
        if (success6) {
            successfulTransactions++;
            expectedBalance = expectedBalance.add(new BigDecimal("150.00"));
            System.out.println("✓ Transaction successful - Expected balance: $" + expectedBalance);
        } else {
            System.out.println("✗ Transaction failed");
        }
        
        // Get final waldorf balance
        User waldorfAfter = userRepository.findByUsername("waldorf").orElse(null);
        assertNotNull(waldorfAfter, "Waldorf user should exist after transactions");
        BigDecimal finalBalance = waldorfAfter.getBalance();
        
        System.out.println("\n=== WALDORF BALANCE DEBUG RESULTS ===");
        System.out.println("Initial balance: $" + initialBalance);
        System.out.println("Expected balance: $" + expectedBalance);
        System.out.println("Final balance: $" + finalBalance);
        System.out.println("Successful transactions: " + successfulTransactions + "/6");
        
        // Round down to nearest integer as requested
        int waldorfBalanceRoundedDown = finalBalance.intValue();
        System.out.println("\nWALDORF BALANCE (rounded down to nearest integer): " + waldorfBalanceRoundedDown);
        
        // DEBUG BREAKPOINT: Set breakpoint here to examine waldorf balance
        System.out.println("\n*** DEBUG POINT: Set breakpoint here to examine waldorf user ***");
        System.out.println("Waldorf final balance (for debugging): $" + finalBalance);
        System.out.println("Waldorf balance rounded down: " + waldorfBalanceRoundedDown);
        
        // Expected calculation: 1000 - 100 + 50 - 75 + 25 - 200 + 150 = 850
        BigDecimal expectedFinalBalance = new BigDecimal("850.00");
        
        // Verification
        assertEquals(expectedFinalBalance, finalBalance, "Final balance should match expected calculation");
        assertTrue(successfulTransactions > 0, "At least some transactions should be successful");
    }

    /**
     * Debug all user balances
     */
    @Test
    void debugAllUserBalances() {
        // Run main test first
        testTransactionProcessingAndDebugWaldorfBalance();
        
        System.out.println("\n=== ALL USER BALANCES AFTER TRANSACTIONS ===");
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            System.out.println(user.getUsername() + ": $" + user.getBalance() + 
                " (rounded down: " + user.getBalance().intValue() + ")");
        }
    }
}