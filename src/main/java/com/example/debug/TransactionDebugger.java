package com.example.debug;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TransactionDebugger - Utility for debugging and monitoring transactions
 * Provides methods to capture, analyze, and debug transaction amounts
 */
@Component
public class TransactionDebugger {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionDebugger.class);
    
    /**
     * Capture the first N transaction amounts from the store
     */
    public List<BigDecimal> captureFirstNAmounts(TransactionStore store, int n) {
        log.info("Capturing first {} transaction amounts", n);
        
        List<Transaction> allTransactions = store.getAll();
        log.debug("Total transactions available: {}", allTransactions.size());
        
        List<BigDecimal> amounts = allTransactions.stream()
            .limit(n)
            .map(Transaction::getAmount)
            .collect(Collectors.toList());
        
        log.info("Captured {} amounts: {}", amounts.size(), amounts);
        return amounts;
    }
    
    /**
     * Capture the first four transaction amounts (specific for Task Two)
     */
    public List<BigDecimal> captureFirstFourAmounts(TransactionStore store) {
        return captureFirstNAmounts(store, 4);
    }
    
    /**
     * Log detailed information about transactions in the store
     */
    public void logTransactionDetails(TransactionStore store) {
        log.info("=== TRANSACTION STORE DETAILED DEBUG ===");
        log.info("Store size: {}", store.size());
        log.info("Is empty: {}", store.isEmpty());
        
        if (!store.isEmpty()) {
            List<Transaction> all = store.getAll();
            
            for (int i = 0; i < all.size(); i++) {
                Transaction tx = all.get(i);
                log.info("Transaction {}: ID={}, Amount={}, Currency={}, Type={}, Status={}", 
                    i + 1, tx.getId(), tx.getAmount(), tx.getCurrency(), 
                    tx.getTransactionType(), tx.getStatus());
            }
            
            Transaction latest = store.getLatest();
            log.info("Latest transaction: ID={}, Amount={}", latest.getId(), latest.getAmount());
        }
        log.info("=== END DETAILED DEBUG ===");
    }
    
    /**
     * Monitor transactions and capture amounts as they arrive
     */
    public void startAmountMonitoring(TransactionStore store) {
        log.info("Starting real-time amount monitoring");
        
        // This would be used in conjunction with a Kafka listener
        // For now, it provides a snapshot of current state
        logCurrentAmounts(store);
    }
    
    /**
     * Log current amounts in the store
     */
    public void logCurrentAmounts(TransactionStore store) {
        List<BigDecimal> amounts = store.getAll().stream()
            .map(Transaction::getAmount)
            .collect(Collectors.toList());
        
        log.info("Current amounts in store: {}", amounts);
        
        if (amounts.size() >= 4) {
            List<BigDecimal> firstFour = amounts.subList(0, 4);
            log.info("First four amounts: {}", firstFour);
        }
    }
    
    /**
     * Validate transaction amounts for debugging
     */
    public boolean validateAmounts(List<BigDecimal> expectedAmounts, TransactionStore store) {
        List<BigDecimal> actualAmounts = captureFirstNAmounts(store, expectedAmounts.size());
        
        boolean isValid = expectedAmounts.equals(actualAmounts);
        
        if (isValid) {
            log.info("Amount validation PASSED: Expected {} = Actual {}", 
                expectedAmounts, actualAmounts);
        } else {
            log.error("Amount validation FAILED: Expected {} ≠ Actual {}", 
                expectedAmounts, actualAmounts);
        }
        
        return isValid;
    }
    
    /**
     * Create a summary report of transaction amounts
     */
    public String createAmountSummary(TransactionStore store) {
        List<Transaction> all = store.getAll();
        
        if (all.isEmpty()) {
            return "No transactions to summarize";
        }
        
        List<BigDecimal> amounts = all.stream().map(Transaction::getAmount).collect(Collectors.toList());
        
        BigDecimal total = amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = total.divide(new BigDecimal(amounts.size()), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal min = amounts.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal max = amounts.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== TRANSACTION AMOUNT SUMMARY ===\n");
        summary.append("Total Transactions: ").append(amounts.size()).append("\n");
        summary.append("Total Amount: ").append(total).append("\n");
        summary.append("Average Amount: ").append(average).append("\n");
        summary.append("Minimum Amount: ").append(min).append("\n");
        summary.append("Maximum Amount: ").append(max).append("\n");
        
        if (amounts.size() >= 4) {
            List<BigDecimal> firstFour = amounts.subList(0, 4);
            summary.append("First Four Amounts: ").append(firstFour).append("\n");
        }
        
        summary.append("All Amounts: ").append(amounts).append("\n");
        summary.append("=== END SUMMARY ===");
        
        String result = summary.toString();
        log.info(result);
        return result;
    }
}