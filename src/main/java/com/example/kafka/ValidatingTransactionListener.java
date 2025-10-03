package com.example.kafka;

import com.example.model.SimpleTransaction;
import com.example.service.TransactionValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka listener for processing simple transactions with validation and database persistence
 */
@Service
public class ValidatingTransactionListener {
    
    private static final Logger log = LoggerFactory.getLogger(ValidatingTransactionListener.class);
    
    private final TransactionValidationService transactionValidationService;
    
    public ValidatingTransactionListener(TransactionValidationService transactionValidationService) {
        this.transactionValidationService = transactionValidationService;
    }
    
    /**
     * Listens for transactions and validates/processes them
     * @param transaction the incoming transaction
     */
    @KafkaListener(topics = "midas-validating-transactions", containerFactory = "simpleTransactionKafkaListenerContainerFactory")
    public void processTransaction(SimpleTransaction transaction) {
        log.info("Received transaction for validation: {}", transaction);
        
        try {
            // Validate and process the transaction
            boolean success = transactionValidationService.validateAndProcessTransaction(
                transaction.getSenderId(),
                transaction.getRecipientId(), 
                transaction.getAmount()
            );
            
            if (success) {
                log.info("Transaction processed successfully: {}", transaction);
            } else {
                log.warn("Transaction rejected: {}", transaction);
            }
            
        } catch (Exception e) {
            log.error("Error processing transaction: {}", transaction, e);
        }
    }
    
    /**
     * Alternative method for processing transactions from string format
     * Expected format: "senderId,recipientId,amount"
     */
    @KafkaListener(topics = "midas-string-transactions", containerFactory = "kafkaListenerContainerFactory")
    public void processStringTransaction(String transactionString) {
        log.info("Received string transaction: {}", transactionString);
        
        try {
            // Parse the string format: "senderId,recipientId,amount"
            String[] parts = transactionString.split(",");
            if (parts.length != 3) {
                log.error("Invalid transaction format. Expected: senderId,recipientId,amount. Got: {}", transactionString);
                return;
            }
            
            Long senderId = Long.parseLong(parts[0].trim());
            Long recipientId = Long.parseLong(parts[1].trim());
            java.math.BigDecimal amount = new java.math.BigDecimal(parts[2].trim());
            
            // Create SimpleTransaction object
            SimpleTransaction transaction = new SimpleTransaction(senderId, recipientId, amount);
            
            // Process the transaction
            processTransaction(transaction);
            
        } catch (NumberFormatException e) {
            log.error("Error parsing transaction string: {}", transactionString, e);
        } catch (Exception e) {
            log.error("Error processing string transaction: {}", transactionString, e);
        }
    }
}