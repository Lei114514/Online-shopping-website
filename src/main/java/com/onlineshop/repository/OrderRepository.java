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
 * 訂單數據訪問接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根據用戶查找訂單
     */
    List<Order> findByUser(User user);
    
    /**
     * 根據訂單編號查找訂單
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根據狀態查找訂單
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * 查找指定時間範圍內的訂單
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 計算用戶的訂單總數
     */
    Long countByUser(User user);
    
    /**
     * 計算用戶的總消費金額
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user AND o.paymentStatus = 'PAID'")
    java.math.BigDecimal calculateUserTotalSpent(@Param("user") User user);
    
    /**
     * 獲取銷售統計數據
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
     * 獲取熱銷商品統計
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
}
