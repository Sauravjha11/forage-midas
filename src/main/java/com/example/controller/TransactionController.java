package com.example.controller;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Transaction Management
 * 
 * This controller provides endpoints to interact with the TransactionStore
 * for debugging and testing purposes in the MIDAS system.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionStore transactionStore;

    @Autowired
    public TransactionController(TransactionStore transactionStore) {
        this.transactionStore = transactionStore;
    }

    /**
     * Get all transactions
     */
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionStore.getAll();
    }

    /**
     * Get transaction by index
     */
    @GetMapping("/{index}")
    public Transaction getTransaction(@PathVariable int index) {
        return transactionStore.get(index);
    }

    /**
     * Get transaction count
     */
    @GetMapping("/count")
    public int getTransactionCount() {
        return transactionStore.size();
    }

    /**
     * Get latest transaction
     */
    @GetMapping("/latest")
    public Transaction getLatestTransaction() {
        return transactionStore.getLatest();
    }

    /**
     * Add a sample transaction for testing
     */
    @PostMapping("/sample")
    public Transaction addSampleTransaction() {
        Transaction sample = new Transaction();
        sample.setId("TXN-" + System.currentTimeMillis());
        sample.setAmount(new BigDecimal("100.50"));
        sample.setCurrency("USD");
        sample.setFromAccount("ACC001");
        sample.setToAccount("ACC002");
        sample.setTimestamp(LocalDateTime.now());
        sample.setTransactionType("TRANSFER");
        sample.setDescription("Sample transaction for testing");
        sample.setStatus("COMPLETED");
        
        transactionStore.add(sample);
        return sample;
    }

    /**
     * Clear all transactions
     */
    @DeleteMapping
    public String clearTransactions() {
        transactionStore.clear();
        return "All transactions cleared";
    }

    /**
     * Get transactions in reverse order (newest first)
     */
    @GetMapping("/reversed")
    public List<Transaction> getTransactionsReversed() {
        return transactionStore.getAllReversed();
    }
}