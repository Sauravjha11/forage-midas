package com.example;

import com.example.model.SimpleTransaction;
import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.TransactionValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskThreeTests - Integration test for H2 database transaction validation and persistence
 * This test loads users, processes transactions, and allows debugging of user balances
 */
@SpringBootTest
@ActiveProfiles("test")
public class TaskThreeTests {

    @Autowired
    private TransactionValidationService transactionValidationService;

    @Autowired
    private UserRepository userRepository;

    private List<SimpleTransaction> testTransactions;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        userRepository.deleteAll();
        
        // Create test users with initial balances
        createTestUsers();
        
        // Create test transactions for processing
        createTestTransactions();
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
     * Create test transactions for processing
     */
    private void createTestTransactions() {
        testTransactions = new ArrayList<>();
        
        // Get user IDs for transactions
        User waldorf = userRepository.findByUsername("waldorf").orElse(null);
        User alice = userRepository.findByUsername("alice").orElse(null);
        User bob = userRepository.findByUsername("bob").orElse(null);
        User charlie = userRepository.findByUsername("charlie").orElse(null);
        
        assertNotNull(waldorf, "Waldorf user should exist");
        assertNotNull(alice, "Alice user should exist");
        assertNotNull(bob, "Bob user should exist");
        assertNotNull(charlie, "Charlie user should exist");
        
        // Create transactions that will affect waldorf's balance
        testTransactions.add(new SimpleTransaction(waldorf.getId(), alice.getId(), new BigDecimal("100.00")));   // waldorf -> alice: -100
        testTransactions.add(new SimpleTransaction(bob.getId(), waldorf.getId(), new BigDecimal("50.00")));      // bob -> waldorf: +50
        testTransactions.add(new SimpleTransaction(waldorf.getId(), charlie.getId(), new BigDecimal("75.00")));  // waldorf -> charlie: -75
        testTransactions.add(new SimpleTransaction(alice.getId(), waldorf.getId(), new BigDecimal("25.00")));    // alice -> waldorf: +25
        testTransactions.add(new SimpleTransaction(waldorf.getId(), bob.getId(), new BigDecimal("200.00")));     // waldorf -> bob: -200
        testTransactions.add(new SimpleTransaction(charlie.getId(), waldorf.getId(), new BigDecimal("150.00")));  // charlie -> waldorf: +150
        
        System.out.println("Created " + testTransactions.size() + " test transactions");
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
        
        // Process each transaction
        int successfulTransactions = 0;
        BigDecimal expectedBalance = initialBalance;
        
        for (int i = 0; i < testTransactions.size(); i++) {
            SimpleTransaction transaction = testTransactions.get(i);
            System.out.println("\nProcessing transaction " + (i + 1) + ": " + transaction);
            
            boolean success = transactionValidationService.validateAndProcessTransaction(
                transaction.getSenderId(),
                transaction.getRecipientId(),
                transaction.getAmount()
            );
            
            if (success) {
                successfulTransactions++;
                System.out.println("✓ Transaction successful");
                
                // Update expected balance if waldorf is involved
                if (transaction.getSenderId().equals(waldorfBefore.getId())) {
                    expectedBalance = expectedBalance.subtract(transaction.getAmount());
                    System.out.println("  Waldorf sent $" + transaction.getAmount() + " - Expected balance: $" + expectedBalance);
                } else if (transaction.getRecipientId().equals(waldorfBefore.getId())) {
                    expectedBalance = expectedBalance.add(transaction.getAmount());
                    System.out.println("  Waldorf received $" + transaction.getAmount() + " - Expected balance: $" + expectedBalance);
                }
            } else {
                System.out.println("✗ Transaction failed");
            }
        }
        
        // Get final waldorf balance
        User waldorfAfter = userRepository.findByUsername("waldorf").orElse(null);
        assertNotNull(waldorfAfter, "Waldorf user should exist after transactions");
        BigDecimal finalBalance = waldorfAfter.getBalance();
        
        System.out.println("\n=== WALDORF BALANCE DEBUG RESULTS ===");
        System.out.println("Initial balance: $" + initialBalance);
        System.out.println("Expected balance: $" + expectedBalance);
        System.out.println("Final balance: $" + finalBalance);
        System.out.println("Successful transactions: " + successfulTransactions + "/" + testTransactions.size());
        
        // Round down to nearest integer as requested
        int waldorfBalanceRoundedDown = finalBalance.intValue();
        System.out.println("\nWALDORF BALANCE (rounded down to nearest integer): " + waldorfBalanceRoundedDown);
        
        // Verification
        assertEquals(expectedBalance, finalBalance, "Final balance should match expected balance");
        assertTrue(successfulTransactions > 0, "At least some transactions should be successful");
        
        // DEBUG BREAKPOINT: Set breakpoint here to examine waldorf balance
        System.out.println("\n*** DEBUG POINT: Set breakpoint here to examine waldorf user ***");
        System.out.println("Waldorf final balance (for debugging): $" + finalBalance);
        System.out.println("Waldorf balance rounded down: " + waldorfBalanceRoundedDown);
    }

    /**
     * Additional test for debugging all user balances
     */
    @Test
    void debugAllUserBalances() {
        // Process transactions first
        testTransactionProcessingAndDebugWaldorfBalance();
        
        System.out.println("\n=== ALL USER BALANCES AFTER TRANSACTIONS ===");
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            System.out.println(user.getUsername() + ": $" + user.getBalance() + " (rounded down: " + user.getBalance().intValue() + ")");
        }
    }

    /**
     * Test individual transaction validation
     */
    @Test
    void testIndividualTransactionValidation() {
        User waldorf = userRepository.findByUsername("waldorf").orElse(null);
        User alice = userRepository.findByUsername("alice").orElse(null);
        
        assertNotNull(waldorf);
        assertNotNull(alice);
        
        // Test valid transaction
        boolean success = transactionValidationService.validateAndProcessTransaction(
            waldorf.getId(), alice.getId(), new BigDecimal("50.00"));
        assertTrue(success, "Valid transaction should succeed");
        
        // Test invalid transaction (insufficient balance)
        boolean failure = transactionValidationService.validateAndProcessTransaction(
            alice.getId(), waldorf.getId(), new BigDecimal("10000.00"));
        assertFalse(failure, "Transaction with insufficient balance should fail");
    }
}