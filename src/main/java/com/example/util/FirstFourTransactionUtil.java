package com.example.util;

import com.example.model.Transaction;
import com.example.service.TransactionStore;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class demonstrating the exact pattern for getting first four transactions:
 * ((com.example.service.TransactionStore) applicationContext.getBean("transactionStore")).getAll().subList(0,4)
 */
@Component
public class FirstFourTransactionUtil {
    
    /**
     * Get the first four transactions using the exact pattern specified
     * Pattern: ((com.example.service.TransactionStore) applicationContext.getBean("transactionStore")).getAll().subList(0,4)
     * 
     * @param applicationContext Spring application context
     * @return List of first four transactions, or all available if less than 4
     */
    public static List<Transaction> getFirstFourTransactions(ApplicationContext applicationContext) {
        // Execute the exact pattern you specified
        TransactionStore transactionStore = (TransactionStore) applicationContext.getBean("transactionStore");
        List<Transaction> allTransactions = transactionStore.getAll();
        
        // Handle edge case where there might be less than 4 transactions
        int endIndex = Math.min(4, allTransactions.size());
        return allTransactions.subList(0, endIndex);
    }
    
    /**
     * Alternative method using bean name directly (more flexible)
     * 
     * @param applicationContext Spring application context
     * @param transactionStoreBeanName Name of the transaction store bean
     * @return List of first four transactions
     */
    public static List<Transaction> getFirstFourTransactions(ApplicationContext applicationContext, String transactionStoreBeanName) {
        // Your exact pattern with parameterized bean name
        TransactionStore transactionStore = (TransactionStore) applicationContext.getBean(transactionStoreBeanName);
        List<Transaction> allTransactions = transactionStore.getAll();
        
        int endIndex = Math.min(4, allTransactions.size());
        return allTransactions.subList(0, endIndex);
    }
    
    /**
     * Method that implements the exact code you provided as a one-liner
     * 
     * @param applicationContext Spring application context
     * @return List of first four transactions
     */
    public static List<Transaction> getFirstFourTransactionsOneLiner(ApplicationContext applicationContext) {
        // This is your exact code pattern as a single line
        return ((TransactionStore) applicationContext.getBean("transactionStore")).getAll().subList(0, 
            Math.min(4, ((TransactionStore) applicationContext.getBean("transactionStore")).getAll().size()));
    }
}