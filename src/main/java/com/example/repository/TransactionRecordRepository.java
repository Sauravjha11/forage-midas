package com.example.repository;

import com.example.entity.TransactionRecord;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for TransactionRecord entity operations
 */
@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    
    /**
     * Find all transactions for a specific user (either as sender or recipient)
     * @param user the user to search for
     * @return list of transactions involving the user
     */
    @Query("SELECT tr FROM TransactionRecord tr WHERE tr.sender = :user OR tr.recipient = :user ORDER BY tr.timestamp DESC")
    List<TransactionRecord> findAllByUser(@Param("user") User user);
    
    /**
     * Find all transactions sent by a specific user
     * @param sender the sender user
     * @return list of transactions sent by the user
     */
    List<TransactionRecord> findBySenderOrderByTimestampDesc(User sender);
    
    /**
     * Find all transactions received by a specific user
     * @param recipient the recipient user
     * @return list of transactions received by the user
     */
    List<TransactionRecord> findByRecipientOrderByTimestampDesc(User recipient);
    
    /**
     * Find transactions within a date range
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of transactions within the range
     */
    List<TransactionRecord> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find transactions by status
     * @param status the transaction status
     * @return list of transactions with the specified status
     */
    List<TransactionRecord> findByStatusOrderByTimestampDesc(TransactionRecord.TransactionStatus status);
    
    /**
     * Count total transactions for a user
     * @param user the user
     * @return count of transactions involving the user
     */
    @Query("SELECT COUNT(tr) FROM TransactionRecord tr WHERE tr.sender = :user OR tr.recipient = :user")
    long countByUser(@Param("user") User user);
}