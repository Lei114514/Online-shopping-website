package com.onlineshop.repository;

import com.onlineshop.model.Order;
import com.onlineshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository Interface
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Find orders by user
     */
    List<Order> findByUser(User user);
    
    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Find orders by status
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * Find orders within date range
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count orders by user
     */
    Long countByUser(User user);
    
    /**
     * Calculate total spent by user
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user AND o.paymentStatus = 'PAID'")
    java.math.BigDecimal calculateUserTotalSpent(@Param("user") User user);
    
    /**
     * Get sales statistics
     */
    @Query("SELECT DATE(o.createdAt) as date, " +
           "COUNT(o.id) as orderCount, " +
           "SUM(o.totalAmount) as totalRevenue, " +
           "AVG(o.totalAmount) as avgOrderValue " +
           "FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.paymentStatus = 'PAID' " +
           "GROUP BY DATE(o.createdAt) " +
           "ORDER BY DATE(o.createdAt) DESC")
    List<Object[]> getSalesStatistics(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get top selling products
     */
    @Query("SELECT p.name, SUM(oi.quantity) as totalSold, SUM(oi.subtotal) as totalRevenue " +
           "FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.paymentStatus = 'PAID' " +
           "GROUP BY p.id, p.name " +
           "ORDER BY totalSold DESC")
    List<Object[]> getTopSellingProducts(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders by payment status
     */
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    /**
     * Find orders by order number (partial match)
     */
    List<Order> findByOrderNumberContaining(String orderNumber);
    
    /**
     * Count orders by status
     */
    Long countByStatus(Order.OrderStatus status);
    
    /**
     * Get recent orders
     */
    List<Order> findTop10ByOrderByCreatedAtDesc();
}
