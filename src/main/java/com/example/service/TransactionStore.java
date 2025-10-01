package com.example.service;

import com.example.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple thread-safe store that keeps transactions in arrival order for inspection.
 * 
 * This component provides an in-memory storage solution for debugging and monitoring
 * financial transactions in the MIDAS system. It maintains thread safety while
 * allowing real-time inspection of processed transactions.
 */
@Component
public class TransactionStore {
    private final List<Transaction> received = Collections.synchronizedList(new ArrayList<>());

    /**
     * Add a transaction to the store
     * 
     * @param tx the transaction to store
     */
    public void add(Transaction tx) {
        received.add(tx);
    }

    /**
     * Get all transactions as a snapshot copy
     * 
     * @return immutable copy of all stored transactions
     */
    public List<Transaction> getAll() {
        // return a snapshot copy
        synchronized (received) {
            return List.copyOf(received);
        }
    }

    /**
     * Get transaction by index
     * 
     * @param index the index of the transaction to retrieve
     * @return the transaction at the specified index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Transaction get(int index) {
        synchronized (received) {
            return received.get(index);
        }
    }

    /**
     * Get the total number of stored transactions
     * 
     * @return the size of the transaction store
     */
    public int size() {
        return received.size();
    }

    /**
     * Clear all stored transactions (useful for testing)
     */
    public void clear() {
        synchronized (received) {
            received.clear();
        }
    }

    /**
     * Get the most recent transaction
     * 
     * @return the last transaction added, or null if store is empty
     */
    public Transaction getLatest() {
        synchronized (received) {
            return received.isEmpty() ? null : received.get(received.size() - 1);
        }
    }

    /**
     * Check if the store contains any transactions
     * 
     * @return true if store is empty, false otherwise
     */
    public boolean isEmpty() {
        return received.isEmpty();
    }

    /**
     * Get transactions in reverse order (newest first)
     * 
     * @return list of transactions with newest first
     */
    public List<Transaction> getAllReversed() {
        synchronized (received) {
            List<Transaction> reversed = new ArrayList<>(received);
            Collections.reverse(reversed);
            return List.copyOf(reversed);
        }
    }

    /**
     * Get the first four transactions using the specified pattern
     * Pattern: ((com.example.service.TransactionStore) applicationContext.getBean("transactionStore")).getAll().subList(0,4)
     * 
     * @return list of first four transactions, or all available if less than 4
     */
    public List<Transaction> getFirstFour() {
        synchronized (received) {
            List<Transaction> allTransactions = getAll();
            int endIndex = Math.min(4, allTransactions.size());
            return allTransactions.subList(0, endIndex);
        }
    }
}