package com.example;

import com.example.debug.TransactionDebugger;
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
 * Comprehensive demonstration of capturing first four transaction amounts
 * This class shows multiple approaches and scenarios for debugging transaction amounts
 */
class ComprehensiveTaskTwoDemo {
    
    private static final Logger log = LoggerFactory.getLogger(ComprehensiveTaskTwoDemo.class);
    
    private TransactionStore store;
    private TransactionDebugger debugger;
    
    @BeforeEach
    void setUp() {
        store = new TransactionStore();
        debugger = new TransactionDebugger();
        log.info("=== Comprehensive TaskTwo Demo Setup Complete ===");
    }
    
    @Test
    void comprehensiveFirstFourAmountsDemo() {
        log.info("=== COMPREHENSIVE FIRST FOUR AMOUNTS DEMONSTRATION ===");
        
        // Scenario 1: Basic first four amounts capture
        log.info("SCENARIO 1: Basic First Four Amounts Capture");
        demonstrateBasicCapture();
        
        // Clear store for next scenario
        store.clear();
        
        // Scenario 2: Real-world financial amounts
        log.info("SCENARIO 2: Real-World Financial Amounts");
        demonstrateRealWorldAmounts();
        
        // Clear store for next scenario  
        store.clear();
        
        // Scenario 3: Edge cases (very small and very large amounts)
        log.info("SCENARIO 3: Edge Cases - Small and Large Amounts");
        demonstrateEdgeCases();
        
        // Clear store for next scenario
        store.clear();
        
        // Scenario 4: Incomplete sets (less than 4 transactions)
        log.info("SCENARIO 4: Incomplete Sets (Less than 4 transactions)");
        demonstrateIncompleteSets();
        
        log.info("=== COMPREHENSIVE DEMONSTRATION COMPLETED ===");
    }
    
    private void demonstrateBasicCapture() {
        log.info("Adding basic test transactions...");
        
        // Add exactly 4 transactions
        store.add(createTransaction("TXN-BASIC-001", new BigDecimal("100.00")));
        store.add(createTransaction("TXN-BASIC-002", new BigDecimal("200.00")));
        store.add(createTransaction("TXN-BASIC-003", new BigDecimal("300.00")));
        store.add(createTransaction("TXN-BASIC-004", new BigDecimal("400.00")));
        
        // Capture first four amounts
        List<BigDecimal> firstFour = debugger.captureFirstFourAmounts(store);
        
        log.info("=== BASIC CAPTURE RESULTS ===");
        for (int i = 0; i < firstFour.size(); i++) {
            log.info("Transaction {}: Amount = {}", i + 1, firstFour.get(i));
        }
        
        // Validate results
        assertEquals(4, firstFour.size());
        assertEquals(new BigDecimal("100.00"), firstFour.get(0));
        assertEquals(new BigDecimal("200.00"), firstFour.get(1));
        assertEquals(new BigDecimal("300.00"), firstFour.get(2));
        assertEquals(new BigDecimal("400.00"), firstFour.get(3));
        
        log.info("Basic capture validation: PASSED");
    }
    
    private void demonstrateRealWorldAmounts() {
        log.info("Adding real-world financial transactions...");
        
        // Real-world amounts that might be seen in financial systems
        store.add(createTransaction("TXN-REAL-001", new BigDecimal("1234.56")));   // Credit card payment
        store.add(createTransaction("TXN-REAL-002", new BigDecimal("50000.00")));  // Wire transfer
        store.add(createTransaction("TXN-REAL-003", new BigDecimal("99.99")));     // Online purchase
        store.add(createTransaction("TXN-REAL-004", new BigDecimal("250000.75"))); // Large business transaction
        store.add(createTransaction("TXN-REAL-005", new BigDecimal("12.50")));     // Small purchase
        
        // Capture first four amounts
        List<BigDecimal> firstFour = debugger.captureFirstFourAmounts(store);
        
        log.info("=== REAL-WORLD AMOUNTS RESULTS ===");
        for (int i = 0; i < firstFour.size(); i++) {
            log.info("Transaction {}: Amount = ${}", i + 1, firstFour.get(i));
        }
        
        // Generate summary
        String summary = debugger.createAmountSummary(store);
        log.info("Real-world amounts summary generated successfully");
        
        // Validate results
        assertEquals(4, firstFour.size());
        assertTrue(firstFour.contains(new BigDecimal("1234.56")));
        assertTrue(firstFour.contains(new BigDecimal("50000.00")));
        assertTrue(firstFour.contains(new BigDecimal("99.99")));
        assertTrue(firstFour.contains(new BigDecimal("250000.75")));
        
        log.info("Real-world amounts validation: PASSED");
    }
    
    private void demonstrateEdgeCases() {
        log.info("Adding edge case transactions...");
        
        // Edge cases: very small and very large amounts
        store.add(createTransaction("TXN-EDGE-001", new BigDecimal("0.01")));        // Smallest possible
        store.add(createTransaction("TXN-EDGE-002", new BigDecimal("999999999.99"))); // Very large
        store.add(createTransaction("TXN-EDGE-003", new BigDecimal("0.001")));       // Sub-cent (testing precision)
        store.add(createTransaction("TXN-EDGE-004", new BigDecimal("5000000.00")));  // Large round number
        
        // Capture first four amounts
        List<BigDecimal> firstFour = debugger.captureFirstFourAmounts(store);
        
        log.info("=== EDGE CASES RESULTS ===");
        for (int i = 0; i < firstFour.size(); i++) {
            BigDecimal amount = firstFour.get(i);
            log.info("Transaction {}: Amount = {} (Scale: {})", i + 1, amount, amount.scale());
        }
        
        // Additional debugging for edge cases
        debugger.logTransactionDetails(store);
        
        // Validate results
        assertEquals(4, firstFour.size());
        assertEquals(new BigDecimal("0.01"), firstFour.get(0));
        assertEquals(new BigDecimal("999999999.99"), firstFour.get(1));
        assertEquals(new BigDecimal("0.001"), firstFour.get(2));
        assertEquals(new BigDecimal("5000000.00"), firstFour.get(3));
        
        log.info("Edge cases validation: PASSED");
    }
    
    private void demonstrateIncompleteSets() {
        log.info("Testing incomplete sets (less than 4 transactions)...");
        
        // Test with 0 transactions
        List<BigDecimal> emptyResult = debugger.captureFirstFourAmounts(store);
        log.info("Zero transactions result: {}", emptyResult);
        assertEquals(0, emptyResult.size());
        
        // Test with 1 transaction
        store.add(createTransaction("TXN-INCOMPLETE-001", new BigDecimal("111.11")));
        List<BigDecimal> oneResult = debugger.captureFirstFourAmounts(store);
        log.info("One transaction result: {}", oneResult);
        assertEquals(1, oneResult.size());
        
        // Test with 2 transactions
        store.add(createTransaction("TXN-INCOMPLETE-002", new BigDecimal("222.22")));
        List<BigDecimal> twoResult = debugger.captureFirstFourAmounts(store);
        log.info("Two transactions result: {}", twoResult);
        assertEquals(2, twoResult.size());
        
        // Test with 3 transactions
        store.add(createTransaction("TXN-INCOMPLETE-003", new BigDecimal("333.33")));
        List<BigDecimal> threeResult = debugger.captureFirstFourAmounts(store);
        log.info("Three transactions result: {}", threeResult);
        assertEquals(3, threeResult.size());
        
        log.info("=== INCOMPLETE SETS RESULTS ===");
        log.info("Zero transactions: {} amounts captured", emptyResult.size());
        log.info("One transaction: {} amounts captured", oneResult.size());
        log.info("Two transactions: {} amounts captured", twoResult.size());
        log.info("Three transactions: {} amounts captured", threeResult.size());
        
        log.info("Incomplete sets validation: PASSED");
    }
    
    @Test
    void demonstrateDebuggingWorkflow() {
        log.info("=== DEBUGGING WORKFLOW DEMONSTRATION ===");
        
        // Step 1: Initialize and add transactions
        log.info("Step 1: Adding transactions to store");
        addSampleTransactions();
        
        // Step 2: Log current state
        log.info("Step 2: Logging current transaction store state");
        debugger.logTransactionDetails(store);
        
        // Step 3: Capture first four amounts
        log.info("Step 3: Capturing first four transaction amounts");
        List<BigDecimal> firstFour = debugger.captureFirstFourAmounts(store);
        
        // Step 4: Validate captured amounts
        log.info("Step 4: Validating captured amounts");
        List<BigDecimal> expectedAmounts = List.of(
            new BigDecimal("125.50"),
            new BigDecimal("2500.00"), 
            new BigDecimal("75.25"),
            new BigDecimal("10000.00")
        );
        boolean isValid = debugger.validateAmounts(expectedAmounts, store);
        assertTrue(isValid, "Amount validation should pass");
        
        // Step 5: Generate comprehensive summary
        log.info("Step 5: Generating comprehensive summary");
        String summary = debugger.createAmountSummary(store);
        assertNotNull(summary);
        
        log.info("=== DEBUGGING WORKFLOW COMPLETED SUCCESSFULLY ===");
    }
    
    private void addSampleTransactions() {
        store.add(createTransaction("TXN-SAMPLE-001", new BigDecimal("125.50")));
        store.add(createTransaction("TXN-SAMPLE-002", new BigDecimal("2500.00")));
        store.add(createTransaction("TXN-SAMPLE-003", new BigDecimal("75.25")));
        store.add(createTransaction("TXN-SAMPLE-004", new BigDecimal("10000.00")));
        store.add(createTransaction("TXN-SAMPLE-005", new BigDecimal("500.75")));
        store.add(createTransaction("TXN-SAMPLE-006", new BigDecimal("1250.00")));
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