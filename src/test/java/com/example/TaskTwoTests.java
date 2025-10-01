package com.example;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TaskTwoTests - Debug and capture transaction amounts
 * This test class focuses on debugging transaction processing and capturing amounts
 */
public class TaskTwoTests {
    
    private static final Logger log = LoggerFactory.getLogger(TaskTwoTests.class);
    private TransactionStore transactionStore;
    
    @BeforeEach
    void setUp() {
        transactionStore = new TransactionStore();
        log.info("=== TaskTwoTests Setup Complete ===");
    }
    
    @Test
    @DisplayName("Debug: Capture First Four Transaction Amounts")
    void debugCaptureFirstFourTransactionAmounts() {
        log.info("Starting debug test for first four transaction amounts");
        
        // Create sample transactions with different amounts
        List<Transaction> testTransactions = createSampleTransactions();
        
        // Add transactions to store
        log.info("Adding {} test transactions to store", testTransactions.size());
        testTransactions.forEach(tx -> {
            transactionStore.add(tx);
            log.debug("Added transaction: id={}, amount={}", tx.getId(), tx.getAmount());
        });
        
        // Capture first four transaction amounts
        List<BigDecimal> firstFourAmounts = captureFirstFourAmounts();
        
        // Debug logging
        log.info("=== CAPTURED FIRST FOUR TRANSACTION AMOUNTS ===");
        for (int i = 0; i < firstFourAmounts.size(); i++) {
            log.info("Transaction {}: Amount = {}", i + 1, firstFourAmounts.get(i));
        }
        
        // Assertions
        assertEquals(4, firstFourAmounts.size(), "Should capture exactly 4 amounts");
        assertEquals(new BigDecimal("100.50"), firstFourAmounts.get(0));
        assertEquals(new BigDecimal("250.75"), firstFourAmounts.get(1));
        assertEquals(new BigDecimal("75.25"), firstFourAmounts.get(2));
        assertEquals(new BigDecimal("500.00"), firstFourAmounts.get(3));
        
        log.info("All assertions passed - first four amounts captured successfully");
    }
    
    @Test
    @DisplayName("Debug: Transaction Processing Pipeline")
    void debugTransactionProcessingPipeline() {
        log.info("Starting transaction processing pipeline debug");
        
        // Create and process transactions one by one
        IntStream.rangeClosed(1, 6).forEach(i -> {
            Transaction tx = createTransactionWithAmount(i, new BigDecimal(i * 100));
            
            log.info("Processing transaction {}: id={}, amount={}", 
                i, tx.getId(), tx.getAmount());
                
            transactionStore.add(tx);
            
            log.info("Store size after adding transaction {}: {}", i, transactionStore.size());
        });
        
        // Debug current store state
        debugStoreState();
        
        // Capture and analyze first four
        List<BigDecimal> firstFour = captureFirstFourAmounts();
        log.info("First four amounts: {}", firstFour);
        
        assertEquals(6, transactionStore.size());
        assertEquals(4, firstFour.size());
    }
    
    @Test
    @DisplayName("Debug: Real-time Amount Monitoring")
    void debugRealTimeAmountMonitoring() {
        log.info("Starting real-time amount monitoring debug");
        
        // Set up monitoring
        List<BigDecimal> capturedAmounts = new ArrayList<>();
        
        // Create transactions with various amounts
        BigDecimal[] testAmounts = {
            new BigDecimal("99.99"),
            new BigDecimal("1234.56"),
            new BigDecimal("0.01"),
            new BigDecimal("999999.99"),
            new BigDecimal("42.42")
        };
        
        for (int i = 0; i < testAmounts.length; i++) {
            Transaction tx = createTransactionWithAmount(i + 1, testAmounts[i]);
            
            log.info("=== Processing Transaction {} ===", i + 1);
            log.info("Transaction ID: {}", tx.getId());
            log.info("Transaction Amount: {}", tx.getAmount());
            log.info("Transaction Currency: {}", tx.getCurrency());
            
            transactionStore.add(tx);
            
            // Capture amount immediately after adding
            Transaction latest = transactionStore.getLatest();
            capturedAmounts.add(latest.getAmount());
            
            log.info("Captured amount: {}", latest.getAmount());
            log.info("Total captured amounts so far: {}", capturedAmounts.size());
            
            // If we have at least 4, show the first four
            if (capturedAmounts.size() >= 4) {
                List<BigDecimal> firstFour = capturedAmounts.subList(0, 4);
                log.info("Current first four amounts: {}", firstFour);
            }
        }
        
        // Final debug output
        List<BigDecimal> finalFirstFour = capturedAmounts.subList(0, 4);
        log.info("=== FINAL FIRST FOUR AMOUNTS ===");
        finalFirstFour.forEach(amount -> log.info("Amount: {}", amount));
        
        assertEquals(5, capturedAmounts.size());
        assertEquals(4, finalFirstFour.size());
    }
    
    /**
     * Helper method to capture the first four transaction amounts from the store
     */
    private List<BigDecimal> captureFirstFourAmounts() {
        log.debug("Capturing first four transaction amounts");
        
        List<Transaction> allTransactions = transactionStore.getAll();
        log.debug("Total transactions in store: {}", allTransactions.size());
        
        List<BigDecimal> amounts = new ArrayList<>();
        
        int limit = Math.min(4, allTransactions.size());
        for (int i = 0; i < limit; i++) {
            Transaction tx = allTransactions.get(i);
            BigDecimal amount = tx.getAmount();
            amounts.add(amount);
            
            log.debug("Captured amount {} of {}: {}", i + 1, limit, amount);
        }
        
        log.debug("Successfully captured {} amounts", amounts.size());
        return amounts;
    }
    
    /**
     * Helper method to create sample transactions for testing
     */
    private List<Transaction> createSampleTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        
        // Transaction 1
        transactions.add(createTransactionWithAmount(1, new BigDecimal("100.50")));
        
        // Transaction 2
        transactions.add(createTransactionWithAmount(2, new BigDecimal("250.75")));
        
        // Transaction 3
        transactions.add(createTransactionWithAmount(3, new BigDecimal("75.25")));
        
        // Transaction 4
        transactions.add(createTransactionWithAmount(4, new BigDecimal("500.00")));
        
        // Transaction 5 (for testing beyond first four)
        transactions.add(createTransactionWithAmount(5, new BigDecimal("1000.00")));
        
        log.debug("Created {} sample transactions", transactions.size());
        return transactions;
    }
    
    /**
     * Helper method to create a transaction with specific amount
     */
    private Transaction createTransactionWithAmount(int index, BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setId("TXN-DEBUG-" + String.format("%03d", index));
        transaction.setAmount(amount);
        transaction.setCurrency("USD");
        transaction.setFromAccount("ACC-FROM-" + index);
        transaction.setToAccount("ACC-TO-" + index);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionType("DEBUG_TRANSFER");
        transaction.setDescription("Debug transaction #" + index + " with amount " + amount);
        transaction.setStatus("COMPLETED");
        
        log.debug("Created transaction: id={}, amount={}", transaction.getId(), transaction.getAmount());
        return transaction;
    }
    
    /**
     * Helper method to debug the current state of the transaction store
     */
    private void debugStoreState() {
        log.info("=== TRANSACTION STORE DEBUG STATE ===");
        log.info("Total transactions: {}", transactionStore.size());
        log.info("Is empty: {}", transactionStore.isEmpty());
        
        if (!transactionStore.isEmpty()) {
            Transaction latest = transactionStore.getLatest();
            log.info("Latest transaction: id={}, amount={}", latest.getId(), latest.getAmount());
            
            List<Transaction> all = transactionStore.getAll();
            log.info("All transaction IDs: {}", 
                all.stream().map(Transaction::getId).toList());
            log.info("All transaction amounts: {}", 
                all.stream().map(Transaction::getAmount).toList());
        }
        log.info("=== END STORE DEBUG STATE ===");
    }
    
    @Test
    @DisplayName("Debug: Transaction Amount Validation")
    void debugTransactionAmountValidation() {
        log.info("Starting transaction amount validation debug");
        
        // Test various amount formats and edge cases
        BigDecimal[] testAmounts = {
            new BigDecimal("0.01"),           // Minimum
            new BigDecimal("999999999.99"),   // Large amount
            new BigDecimal("123.456"),        // 3 decimal places
            new BigDecimal("100")             // No decimal places
        };
        
        for (int i = 0; i < testAmounts.length; i++) {
            BigDecimal amount = testAmounts[i];
            log.info("Testing amount: {}", amount);
            
            Transaction tx = createTransactionWithAmount(i + 1, amount);
            transactionStore.add(tx);
            
            // Validate the amount was stored correctly
            Transaction stored = transactionStore.get(i);
            assertEquals(amount, stored.getAmount(), 
                "Amount should be stored exactly as provided");
                
            log.info("Amount validation passed for: {}", amount);
        }
        
        // Capture first four for debugging
        List<BigDecimal> firstFour = captureFirstFourAmounts();
        log.info("Final first four amounts after validation: {}", firstFour);
    }
}