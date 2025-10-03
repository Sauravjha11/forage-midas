package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * Simple Transaction model for Kafka messages with senderId, recipientId, and amount
 * This is different from the database TransactionRecord entity
 */
public class SimpleTransaction {
    
    @JsonProperty("senderId")
    private Long senderId;
    
    @JsonProperty("recipientId")
    private Long recipientId;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    // Default constructor for JSON deserialization
    public SimpleTransaction() {}
    
    // Constructor
    public SimpleTransaction(Long senderId, Long recipientId, BigDecimal amount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }
    
    // Getters and Setters
    public Long getSenderId() {
        return senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    
    public Long getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "SimpleTransaction{" +
                "senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", amount=" + amount +
                '}';
    }
}