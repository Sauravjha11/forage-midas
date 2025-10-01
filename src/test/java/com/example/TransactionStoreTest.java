package com.example;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransactionStore service
 */
public class TransactionStoreTest {

    private TransactionStore transactionStore;

    @BeforeEach
    void setUp() {
        transactionStore = new TransactionStore();
    }

    @Test
    void shouldStartEmpty() {
        assertTrue(transactionStore.isEmpty());
        assertEquals(0, transactionStore.size());
        assertNull(transactionStore.getLatest());
    }

    @Test
    void shouldAddTransaction() {
        // Given
        Transaction transaction = createSampleTransaction();
        
        // When
        transactionStore.add(transaction);
        
        // Then
        assertEquals(1, transactionStore.size());
        assertFalse(transactionStore.isEmpty());
        assertEquals(transaction, transactionStore.get(0));
        assertEquals(transaction, transactionStore.getLatest());
    }

    @Test
    void shouldReturnAllTransactions() {
        // Given
        Transaction tx1 = createSampleTransaction();
        Transaction tx2 = createSampleTransaction();
        tx2.setId("TXN-002");
        
        // When
        transactionStore.add(tx1);
        transactionStore.add(tx2);
        
        // Then
        assertEquals(2, transactionStore.getAll().size());
        assertTrue(transactionStore.getAll().contains(tx1));
        assertTrue(transactionStore.getAll().contains(tx2));
    }

    @Test
    void shouldReturnTransactionsInReverseOrder() {
        // Given
        Transaction tx1 = createSampleTransaction();
        Transaction tx2 = createSampleTransaction();
        tx2.setId("TXN-002");
        
        transactionStore.add(tx1);
        transactionStore.add(tx2);
        
        // When
        var reversed = transactionStore.getAllReversed();
        
        // Then
        assertEquals(2, reversed.size());
        assertEquals(tx2, reversed.get(0)); // newest first
        assertEquals(tx1, reversed.get(1)); // oldest last
    }

    @Test
    void shouldClearAllTransactions() {
        // Given
        transactionStore.add(createSampleTransaction());
        assertEquals(1, transactionStore.size());
        
        // When
        transactionStore.clear();
        
        // Then
        assertTrue(transactionStore.isEmpty());
        assertEquals(0, transactionStore.size());
        assertNull(transactionStore.getLatest());
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // This is a basic thread safety test
        // In a real scenario, you'd want more comprehensive testing
        
        int numThreads = 10;
        int transactionsPerThread = 100;
        Thread[] threads = new Thread[numThreads];
        
        // Create threads that add transactions concurrently
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < transactionsPerThread; j++) {
                    Transaction tx = createSampleTransaction();
                    tx.setId("TXN-" + threadId + "-" + j);
                    transactionStore.add(tx);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify all transactions were added
        assertEquals(numThreads * transactionsPerThread, transactionStore.size());
    }

    private Transaction createSampleTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId("TXN-001");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("USD");
        transaction.setFromAccount("ACC001");
        transaction.setToAccount("ACC002");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setTransactionType("TRANSFER");
        transaction.setDescription("Test transaction");
        transaction.setStatus("COMPLETED");
        return transaction;
    }
}