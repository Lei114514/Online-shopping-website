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
 * 用戶活動日誌數據訪問接口
 */
@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    
    /**
     * 根據用戶查找活動日誌
     */
    List<UserActivityLog> findByUser(User user);
    
    /**
     * 根據活動類型查找日誌
     */
    List<UserActivityLog> findByActivityType(String activityType);
    
    /**
     * 查找指定時間範圍內的活動日誌
     */
    List<UserActivityLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 查找用戶在指定時間範圍內的活動日誌
     */
    List<UserActivityLog> findByUserAndCreatedAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 獲取用戶活動統計
     */
    @Query("SELECT l.activityType, COUNT(l) as activityCount " +
           "FROM UserActivityLog l " +
           "WHERE l.user = :user AND l.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY l.activityType " +
           "ORDER BY activityCount DESC")
    List<Object[]> getUserActivityStatistics(@Param("user") User user,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * 獲取熱門商品瀏覽統計
     */
    @Query("SELECT SUBSTRING(l.activityDetails, LOCATE('productId=', l.activityDetails) + 10, 10) as productId, " +
           "COUNT(l) as viewCount " +
           "FROM UserActivityLog l " +
           "WHERE l.activityType = 'VIEW_PRODUCT' AND l.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY productId " +
           "ORDER BY viewCount DESC")
    List<Object[]> getProductViewStatistics(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}
