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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the TransactionDebugger utility for debugging transaction amounts
 */
class TransactionDebuggerTest {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionDebuggerTest.class);
    
    private TransactionStore store;
    private TransactionDebugger debugger;
    
    @BeforeEach
    void setUp() {
        store = new TransactionStore();
        debugger = new TransactionDebugger();
        log.info("=== TransactionDebuggerTest Setup Complete ===");
    }
    
    @Test
    void testCaptureFirstFourAmounts() {
        log.info("Testing capture first four amounts functionality");
        
        // Add 6 transactions
        store.add(createTransaction("TXN-001", new BigDecimal("100.50")));
        store.add(createTransaction("TXN-002", new BigDecimal("200.75")));
        store.add(createTransaction("TXN-003", new BigDecimal("300.25")));
        store.add(createTransaction("TXN-004", new BigDecimal("400.00")));
        store.add(createTransaction("TXN-005", new BigDecimal("500.50")));
        store.add(createTransaction("TXN-006", new BigDecimal("600.75")));
        
        // Capture first four
        List<BigDecimal> firstFour = debugger.captureFirstFourAmounts(store);
        
        // Validate
        assertEquals(4, firstFour.size());
        assertEquals(new BigDecimal("100.50"), firstFour.get(0));
        assertEquals(new BigDecimal("200.75"), firstFour.get(1));
        assertEquals(new BigDecimal("300.25"), firstFour.get(2));
        assertEquals(new BigDecimal("400.00"), firstFour.get(3));
        
        log.info("First four amounts captured successfully: {}", firstFour);
    }
    
    @Test
    void testCaptureFirstNAmounts() {
        log.info("Testing capture first N amounts functionality");
        
        // Add 5 transactions
        for (int i = 1; i <= 5; i++) {
            store.add(createTransaction("TXN-" + String.format("%03d", i), 
                new BigDecimal(i * 100)));
        }
        
        // Test different N values
        List<BigDecimal> first2 = debugger.captureFirstNAmounts(store, 2);
        assertEquals(2, first2.size());
        
        List<BigDecimal> first3 = debugger.captureFirstNAmounts(store, 3);
        assertEquals(3, first3.size());
        
        List<BigDecimal> first10 = debugger.captureFirstNAmounts(store, 10);
        assertEquals(5, first10.size()); // Only 5 transactions available
        
        log.info("First N amounts testing completed successfully");
    }
    
    @Test
    void testAmountValidation() {
        log.info("Testing amount validation functionality");
        
        // Add test transactions
        store.add(createTransaction("TXN-001", new BigDecimal("50.25")));
        store.add(createTransaction("TXN-002", new BigDecimal("75.50")));
        store.add(createTransaction("TXN-003", new BigDecimal("100.00")));
        
        // Test validation
        List<BigDecimal> expectedAmounts = Arrays.asList(
            new BigDecimal("50.25"),
            new BigDecimal("75.50"),
            new BigDecimal("100.00")
        );
        
        boolean isValid = debugger.validateAmounts(expectedAmounts, store);
        assertTrue(isValid, "Amount validation should pass");
        
        // Test with wrong expected amounts
        List<BigDecimal> wrongAmounts = Arrays.asList(
            new BigDecimal("999.99"),
            new BigDecimal("888.88"),
            new BigDecimal("777.77")
        );
        
        boolean isInvalid = debugger.validateAmounts(wrongAmounts, store);
        assertFalse(isInvalid, "Amount validation should fail for wrong amounts");
        
        log.info("Amount validation testing completed successfully");
    }
    
    @Test
    void testAmountSummaryGeneration() {
        log.info("Testing amount summary generation");
        
        // Add test transactions with various amounts
        store.add(createTransaction("TXN-001", new BigDecimal("100.00")));
        store.add(createTransaction("TXN-002", new BigDecimal("200.00")));
        store.add(createTransaction("TXN-003", new BigDecimal("50.00")));
        store.add(createTransaction("TXN-004", new BigDecimal("300.00")));
        store.add(createTransaction("TXN-005", new BigDecimal("150.00")));
        
        // Generate summary
        String summary = debugger.createAmountSummary(store);
        
        // Verify summary contains expected information
        assertNotNull(summary);
        assertTrue(summary.contains("Total Transactions: 5"));
        assertTrue(summary.contains("Total Amount: 800.00"));
        assertTrue(summary.contains("Average Amount: 160.00"));
        assertTrue(summary.contains("Minimum Amount: 50.00"));
        assertTrue(summary.contains("Maximum Amount: 300.00"));
        assertTrue(summary.contains("First Four Amounts: [100.00, 200.00, 50.00, 300.00]"));
        
        log.info("Amount summary generation completed successfully");
    }
    
    @Test
    void testEmptyStoreHandling() {
        log.info("Testing empty store handling");
        
        // Test with empty store
        List<BigDecimal> emptyResult = debugger.captureFirstFourAmounts(store);
        assertTrue(emptyResult.isEmpty());
        
        String emptySummary = debugger.createAmountSummary(store);
        assertEquals("No transactions to summarize", emptySummary);
        
        log.info("Empty store handling completed successfully");
    }
    
    private Transaction createTransaction(String id, BigDecimal amount) {
        Transaction tx = new Transaction();
        tx.setId(id);
        tx.setAmount(amount);
        tx.setCurrency("USD");
        tx.setFromAccount("ACCT-FROM-001");
        tx.setToAccount("ACCT-TO-001");
        tx.setTransactionType("TRANSFER");
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus("PENDING");
        return tx;
    }
}