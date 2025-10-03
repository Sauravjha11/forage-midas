package com.example.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * User entity representing a user in the MIDAS system with account balance
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    
    // Default constructor for JPA
    protected User() {}
    
    // Constructor
    public User(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }
    
    // Constructor with long id for testing
    public User(Long id, String username, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    // Helper method to deduct from balance
    public void deductBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
    
    // Helper method to add to balance
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
    
    // Helper method to check if user has sufficient balance
    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                '}';
    }
}