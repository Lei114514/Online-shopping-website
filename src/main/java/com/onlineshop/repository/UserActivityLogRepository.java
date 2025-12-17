package com.onlineshop.repository;

import com.onlineshop.model.UserActivityLog;
import com.onlineshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User Activity Log Repository Interface
 */
@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    
    /**
     * Find activity logs by user
     */
    List<UserActivityLog> findByUser(User user);
    
    /**
     * Find logs by activity type
     */
    List<UserActivityLog> findByActivityType(String activityType);
    
    /**
     * Find logs within date range
     */
    List<UserActivityLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find user logs within date range
     */
    List<UserActivityLog> findByUserAndCreatedAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get user activity statistics
     */
    @Query("SELECT l.activityType, COUNT(l) as activityCount " +
           "FROM UserActivityLog l " +
           "WHERE l.user = :user AND l.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY l.activityType " +
           "ORDER BY activityCount DESC")
    List<Object[]> getUserActivityStatistics(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get product view statistics
     */
    @Query("SELECT SUBSTRING(l.activityDetails, LOCATE('productId=', l.activityDetails) + 10, 10) as productId, " +
           "COUNT(l) as viewCount " +
           "FROM UserActivityLog l " +
           "WHERE l.activityType = 'VIEW_PRODUCT' AND l.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY productId " +
           "ORDER BY viewCount DESC")
    List<Object[]> getProductViewStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get recent activity logs
     */
    List<UserActivityLog> findTop20ByOrderByCreatedAtDesc();
    
    /**
     * Get recent 100 activity logs
     */
    List<UserActivityLog> findTop100ByOrderByCreatedAtDesc();
    
    /**
     * Get user's recent activity logs
     */
    List<UserActivityLog> findTop50ByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find logs after specific date
     */
    List<UserActivityLog> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Get activity type statistics
     */
    @Query("SELECT l.activityType, COUNT(l) as activityCount " +
           "FROM UserActivityLog l " +
           "WHERE l.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY l.activityType " +
           "ORDER BY activityCount DESC")
    List<Object[]> getActivityTypeStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
