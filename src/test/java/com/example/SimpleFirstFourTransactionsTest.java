package com.example;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple demonstration of getting first four transactions using subList
 * This test works without Spring context to demonstrate the core pattern
 */
class SimpleFirstFourTransactionsTest {
    
    private static final Logger log = LoggerFactory.getLogger(SimpleFirstFourTransactionsTest.class);
    
    private TransactionStore transactionStore;
    
    @BeforeEach
    void setUp() {
        transactionStore = new TransactionStore();
        log.info("=== Simple First Four Transactions Test Setup Complete ===");
    }
    
    @Test
    void demonstrateFirstFourTransactionsUsingSubList() {
        log.info("=== DEMONSTRATING FIRST FOUR TRANSACTIONS USING getAll().subList(0,4) ===");
        
        // Add sample transactions
        addSampleTransactions();
        
        // This is the exact pattern you requested:
        // ((com.example.service.TransactionStore) applicationContext.getBean("transactionStore")).getAll().subList(0,4)
        // Simplified without applicationContext since we have direct access to the store
        
        log.info("Executing: transactionStore.getAll().subList(0, Math.min(4, transactionStore.getAll().size()))");
        
        List<Transaction> allTransactions = transactionStore.getAll();
        int endIndex = Math.min(4, allTransactions.size());
        List<Transaction> firstFour = allTransactions.subList(0, endIndex);
        
        // Log results
        log.info("=== FIRST FOUR TRANSACTIONS RESULTS ===");
        log.info("Total transactions in store: {}", allTransactions.size());
        log.info("First four transactions retrieved: {}", firstFour.size());
        
        for (int i = 0; i < firstFour.size(); i++) {
            Transaction tx = firstFour.get(i);
            log.info("Transaction {}: ID={}, Amount={}, Currency={}", 
                i + 1, tx.getId(), tx.getAmount(), tx.getCurrency());
        }
        
        // Validate results
        assertEquals(4, firstFour.size());
        assertEquals("TXN-001", firstFour.get(0).getId());
        assertEquals("TXN-002", firstFour.get(1).getId());
        assertEquals("TXN-003", firstFour.get(2).getId());
        assertEquals("TXN-004", firstFour.get(3).getId());
        
        log.info("Validation PASSED - first four transactions captured correctly");
    }
    
    @Test
    void demonstrateFirstFourWithDirectMethod() {
        log.info("=== DEMONSTRATING USING THE getFirstFour() METHOD ===");
        
        // Add sample transactions
        addSampleTransactions();
        
        // Using the direct method we added to TransactionStore
        List<Transaction> firstFour = transactionStore.getFirstFour();
        
        // Log results
        log.info("=== FIRST FOUR TRANSACTIONS (DIRECT METHOD) ===");
        for (int i = 0; i < firstFour.size(); i++) {
            Transaction tx = firstFour.get(i);
            log.info("Transaction {}: ID={}, Amount={}", i + 1, tx.getId(), tx.getAmount());
        }
        
        assertEquals(4, firstFour.size());
        log.info("Direct method validation PASSED");
    }
    
    @Test
    void demonstrateEdgeCases() {
        log.info("=== DEMONSTRATING EDGE CASES ===");
        
        // Test with empty store
        List<Transaction> emptyResult = transactionStore.getAll().subList(0, 
            Math.min(4, transactionStore.getAll().size()));
        assertEquals(0, emptyResult.size());
        log.info("Empty store test: PASSED - returned {} transactions", emptyResult.size());
        
        // Test with only 2 transactions
        transactionStore.add(createTransaction("TXN-EDGE-001", new BigDecimal("100.00")));
        transactionStore.add(createTransaction("TXN-EDGE-002", new BigDecimal("200.00")));
        
        List<Transaction> allTransactions = transactionStore.getAll();
        List<Transaction> twoTransactionsResult = allTransactions.subList(0, 
            Math.min(4, allTransactions.size()));
        assertEquals(2, twoTransactionsResult.size());
        log.info("Two transactions test: PASSED - returned {} transactions", twoTransactionsResult.size());
        
        // Test with exactly 4 transactions
        transactionStore.add(createTransaction("TXN-EDGE-003", new BigDecimal("300.00")));
        transactionStore.add(createTransaction("TXN-EDGE-004", new BigDecimal("400.00")));
        
        allTransactions = transactionStore.getAll();
        List<Transaction> fourTransactionsResult = allTransactions.subList(0, 
            Math.min(4, allTransactions.size()));
        assertEquals(4, fourTransactionsResult.size());
        log.info("Four transactions test: PASSED - returned {} transactions", fourTransactionsResult.size());
        
        // Test with more than 4 transactions
        transactionStore.add(createTransaction("TXN-EDGE-005", new BigDecimal("500.00")));
        transactionStore.add(createTransaction("TXN-EDGE-006", new BigDecimal("600.00")));
        
        allTransactions = transactionStore.getAll();
        List<Transaction> moreThanFourResult = allTransactions.subList(0, 
            Math.min(4, allTransactions.size()));
        assertEquals(4, moreThanFourResult.size());
        log.info("More than four transactions test: PASSED - returned {} transactions (limited to 4)", 
            moreThanFourResult.size());
        
        // Verify it's the first four
        assertEquals("TXN-EDGE-001", moreThanFourResult.get(0).getId());
        assertEquals("TXN-EDGE-002", moreThanFourResult.get(1).getId());
        assertEquals("TXN-EDGE-003", moreThanFourResult.get(2).getId());
        assertEquals("TXN-EDGE-004", moreThanFourResult.get(3).getId());
        
        log.info("All edge case tests PASSED");
    }
    
    @Test
    void demonstrateOneLinePattern() {
        log.info("=== DEMONSTRATING ONE-LINE PATTERN ===");
        
        addSampleTransactions();
        
        // Your exact pattern as a one-liner (simulating the application context call)
        List<Transaction> firstFour = transactionStore.getAll().subList(0, 
            Math.min(4, transactionStore.getAll().size()));
        
        log.info("One-line pattern result: {} transactions", firstFour.size());
        for (Transaction tx : firstFour) {
            log.info("  - ID: {}, Amount: {}", tx.getId(), tx.getAmount());
        }
        
        assertEquals(4, firstFour.size());
        log.info("One-line pattern validation PASSED");
    }
    
    private void addSampleTransactions() {
        log.info("Adding sample transactions to the store");
        
        transactionStore.add(createTransaction("TXN-001", new BigDecimal("100.00")));
        transactionStore.add(createTransaction("TXN-002", new BigDecimal("250.50")));
        transactionStore.add(createTransaction("TXN-003", new BigDecimal("75.25")));
        transactionStore.add(createTransaction("TXN-004", new BigDecimal("500.00")));
        transactionStore.add(createTransaction("TXN-005", new BigDecimal("1000.00")));
        transactionStore.add(createTransaction("TXN-006", new BigDecimal("150.75")));
        
        log.info("Added {} transactions to store", transactionStore.size());
    }
    
    private Transaction createTransaction(String id, BigDecimal amount) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setAmount(amount);
        tx.setCurrency("USD");
        tx.setFromAccount("ACCT-FROM-" + id.substring(id.length() - 3));
        tx.setToAccount("ACCT-TO-" + id.substring(id.length() - 3));
        tx.setTransactionType("TRANSFER");
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus("COMPLETED");
        return tx;
    }
}