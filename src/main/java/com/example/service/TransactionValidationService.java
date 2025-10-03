package com.example.service;

import com.example.entity.TransactionRecord;
import com.example.entity.User;
import com.example.repository.TransactionRecordRepository;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service for validating and processing transactions
 * Handles validation of sender/recipient existence and balance checks
 */
@Service
@Transactional
public class TransactionValidationService {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionValidationService.class);
    
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    
    public TransactionValidationService(UserRepository userRepository, 
                                       TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }
    
    /**
     * Validates and processes a transaction with senderId, recipientId, and amount
     * @param senderId the sender's user ID
     * @param recipientId the recipient's user ID
     * @param amount the transaction amount
     * @return true if transaction was processed successfully, false if validation failed
     */
    public boolean validateAndProcessTransaction(Long senderId, Long recipientId, BigDecimal amount) {
        log.debug("Validating transaction: senderId={}, recipientId={}, amount={}", senderId, recipientId, amount);
        
        try {
            // Validate sender exists
            Optional<User> senderOpt = userRepository.findById(senderId);
            if (senderOpt.isEmpty()) {
                log.warn("Transaction rejected: Sender ID {} not found", senderId);
                return false;
            }
            User sender = senderOpt.get();
            
            // Validate recipient exists
            Optional<User> recipientOpt = userRepository.findById(recipientId);
            if (recipientOpt.isEmpty()) {
                log.warn("Transaction rejected: Recipient ID {} not found", recipientId);
                return false;
            }
            User recipient = recipientOpt.get();
            
            // Validate sender has sufficient balance
            if (!sender.hasSufficientBalance(amount)) {
                log.warn("Transaction rejected: Sender ID {} has insufficient balance. Required: {}, Available: {}", 
                    senderId, amount, sender.getBalance());
                return false;
            }
            
            // All validations passed - process the transaction
            return processValidTransaction(sender, recipient, amount);
            
        } catch (Exception e) {
            log.error("Error processing transaction: senderId={}, recipientId={}, amount={}", 
                senderId, recipientId, amount, e);
            return false;
        }
    }
    
    /**
     * Processes a validated transaction by updating balances and recording to database
     * @param sender the sender user
     * @param recipient the recipient user
     * @param amount the transaction amount
     * @return true if processing was successful
     */
    private boolean processValidTransaction(User sender, User recipient, BigDecimal amount) {
        try {
            // Update balances
            sender.deductBalance(amount);
            recipient.addBalance(amount);
            
            // Save updated users
            userRepository.save(sender);
            userRepository.save(recipient);
            
            // Create and save transaction record
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount);
            transactionRecord.setDescription(String.format("Transfer from %s to %s", 
                sender.getUsername(), recipient.getUsername()));
            transactionRecordRepository.save(transactionRecord);
            
            log.info("Transaction processed successfully: {} transferred {} from {} to {}", 
                amount, amount, sender.getUsername(), recipient.getUsername());
            
            return true;
            
        } catch (Exception e) {
            log.error("Error saving transaction: sender={}, recipient={}, amount={}", 
                sender.getId(), recipient.getId(), amount, e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }
    
    /**
     * Validates a transaction without processing it
     * @param senderId the sender's user ID
     * @param recipientId the recipient's user ID  
     * @param amount the transaction amount
     * @return validation result with details
     */
    public ValidationResult validateTransaction(Long senderId, Long recipientId, BigDecimal amount) {
        // Check if sender exists
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) {
            return new ValidationResult(false, "Sender ID " + senderId + " not found");
        }
        User sender = senderOpt.get();
        
        // Check if recipient exists
        Optional<User> recipientOpt = userRepository.findById(recipientId);
        if (recipientOpt.isEmpty()) {
            return new ValidationResult(false, "Recipient ID " + recipientId + " not found");
        }
        
        // Check if sender has sufficient balance
        if (!sender.hasSufficientBalance(amount)) {
            return new ValidationResult(false, 
                String.format("Insufficient balance. Required: %s, Available: %s", amount, sender.getBalance()));
        }
        
        return new ValidationResult(true, "Transaction is valid");
    }
    
    /**
     * Gets user balance by ID
     * @param userId the user ID
     * @return user balance or null if user not found
     */
    public BigDecimal getUserBalance(Long userId) {
        return userRepository.findById(userId)
            .map(User::getBalance)
            .orElse(null);
    }
    
    /**
     * Gets user by username
     * @param username the username
     * @return user if found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Result of transaction validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, message='%s'}", valid, message);
        }
    }
}