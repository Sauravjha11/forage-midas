package com.example;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstration of getting first four transactions using the specified pattern:
 * ((com.example.service.TransactionStore) applicationContext.getBean("transactionStore")).getAll().subList(0,4)
 */
@SpringBootTest
class FirstFourTransactionsDemo {
    
    private static final Logger log = LoggerFactory.getLogger(FirstFourTransactionsDemo.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private TransactionStore transactionStore;
    
    @Test
    void demonstrateFirstFourTransactionsWithApplicationContext() {
        log.info("=== DEMONSTRATING FIRST FOUR TRANSACTIONS USING APPLICATION CONTEXT ===");
        
        // Clear any existing transactions
        transactionStore.clear();
        
        // Add sample transactions to the store
        addSampleTransactions();
        
        // Method 1: Using the exact pattern you specified
        log.info("Method 1: Using exact pattern with applicationContext.getBean()");
        List<Transaction> firstFourUsingPattern = getFirstFourUsingApplicationContextPattern();
        
        // Method 2: Direct method call for comparison
        log.info("Method 2: Using direct TransactionStore method");
        List<Transaction> firstFourDirect = transactionStore.getFirstFour();
        
        // Method 3: Using getAll().subList() directly
        log.info("Method 3: Using getAll().subList() directly");
        List<Transaction> allTransactions = transactionStore.getAll();
        List<Transaction> firstFourSublist = allTransactions.subList(0, Math.min(4, allTransactions.size()));
        
        // Log results
        logResults("Pattern Method", firstFourUsingPattern);
        logResults("Direct Method", firstFourDirect);
        logResults("Sublist Method", firstFourSublist);
        
        // Validate all methods return the same results
        assertEquals(firstFourUsingPattern.size(), firstFourDirect.size());
        assertEquals(firstFourDirect.size(), firstFourSublist.size());
        
        for (int i = 0; i < firstFourUsingPattern.size(); i++) {
            assertEquals(firstFourUsingPattern.get(i).getId(), firstFourDirect.get(i).getId());
            assertEquals(firstFourDirect.get(i).getId(), firstFourSublist.get(i).getId());
        }
        
        log.info("All methods returned identical results - validation PASSED");
    }
    
    /**
     * Implementation of the exact pattern you specified:
     * ((com.example.service.TransactionStore) applicationContext.getBean("transactionStore")).getAll().subList(0,4)
     */
    private List<Transaction> getFirstFourUsingApplicationContextPattern() {
        log.info("Executing: ((com.example.service.TransactionStore) applicationContext.getBean(\"transactionStore\")).getAll().subList(0,4)");
        
        // Get the TransactionStore bean from application context
        TransactionStore store = (TransactionStore) applicationContext.getBean("transactionStore");
        
        // Get all transactions
        List<Transaction> allTransactions = store.getAll();
        
        // Get first four using subList (handle case where less than 4 transactions exist)
        int endIndex = Math.min(4, allTransactions.size());
        List<Transaction> firstFour = allTransactions.subList(0, endIndex);
        
        log.info("Retrieved {} transactions using application context pattern", firstFour.size());
        return firstFour;
    }
    
    private void addSampleTransactions() {
        log.info("Adding sample transactions to the store");
        
        transactionStore.add(createTransaction("TXN-FIRST-001", new BigDecimal("100.00")));
        transactionStore.add(createTransaction("TXN-FIRST-002", new BigDecimal("250.50")));
        transactionStore.add(createTransaction("TXN-FIRST-003", new BigDecimal("75.25")));
        transactionStore.add(createTransaction("TXN-FIRST-004", new BigDecimal("500.00")));
        transactionStore.add(createTransaction("TXN-FIRST-005", new BigDecimal("1000.00")));
        transactionStore.add(createTransaction("TXN-FIRST-006", new BigDecimal("150.75")));
        
        log.info("Added {} transactions to store", transactionStore.size());
    }
    
    private void logResults(String methodName, List<Transaction> transactions) {
        log.info("=== {} Results ===", methodName);
        log.info("Number of transactions retrieved: {}", transactions.size());
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction tx = transactions.get(i);
            log.info("Transaction {}: ID={}, Amount={}, Currency={}", 
                i + 1, tx.getId(), tx.getAmount(), tx.getCurrency());
        }
    }
    
    @Test
    void demonstrateEdgeCasesWithApplicationContext() {
        log.info("=== DEMONSTRATING EDGE CASES WITH APPLICATION CONTEXT PATTERN ===");
        
        // Test with empty store
        transactionStore.clear();
        List<Transaction> emptyResult = getFirstFourUsingApplicationContextPattern();
        assertEquals(0, emptyResult.size());
        log.info("Empty store test: PASSED - returned {} transactions", emptyResult.size());
        
        // Test with only 2 transactions
        transactionStore.add(createTransaction("TXN-EDGE-001", new BigDecimal("99.99")));
        transactionStore.add(createTransaction("TXN-EDGE-002", new BigDecimal("199.99")));
        
        List<Transaction> twoTransactionsResult = getFirstFourUsingApplicationContextPattern();
        assertEquals(2, twoTransactionsResult.size());
        log.info("Two transactions test: PASSED - returned {} transactions", twoTransactionsResult.size());
        
        // Test with exactly 4 transactions
        transactionStore.add(createTransaction("TXN-EDGE-003", new BigDecimal("299.99")));
        transactionStore.add(createTransaction("TXN-EDGE-004", new BigDecimal("399.99")));
        
        List<Transaction> fourTransactionsResult = getFirstFourUsingApplicationContextPattern();
        assertEquals(4, fourTransactionsResult.size());
        log.info("Four transactions test: PASSED - returned {} transactions", fourTransactionsResult.size());
        
        // Test with more than 4 transactions
        transactionStore.add(createTransaction("TXN-EDGE-005", new BigDecimal("499.99")));
        transactionStore.add(createTransaction("TXN-EDGE-006", new BigDecimal("599.99")));
        
        List<Transaction> moreThanFourResult = getFirstFourUsingApplicationContextPattern();
        assertEquals(4, moreThanFourResult.size());
        log.info("More than four transactions test: PASSED - returned {} transactions (limited to 4)", moreThanFourResult.size());
        
        // Verify the first four are the correct ones
        assertEquals("TXN-EDGE-001", moreThanFourResult.get(0).getId());
        assertEquals("TXN-EDGE-002", moreThanFourResult.get(1).getId());
        assertEquals("TXN-EDGE-003", moreThanFourResult.get(2).getId());
        assertEquals("TXN-EDGE-004", moreThanFourResult.get(3).getId());
        
        log.info("All edge case tests PASSED");
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