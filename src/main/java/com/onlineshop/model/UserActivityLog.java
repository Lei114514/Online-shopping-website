package com.onlineshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用戶活動日誌實體類
 * 記錄用戶的瀏覽和購買行為
 */
@Entity
@Table(name = "user_activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;
    
    @Column(name = "activity_details", columnDefinition = "TEXT")
    private String activityDetails;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * 活動類型常量
     */
    public static class ActivityType {
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String VIEW_PRODUCT = "VIEW_PRODUCT";
        public static final String ADD_TO_CART = "ADD_TO_CART";
        public static final String REMOVE_FROM_CART = "REMOVE_FROM_CART";
        public static final String PLACE_ORDER = "PLACE_ORDER";
        public static final String SEARCH = "SEARCH";
        public static final String REGISTER = "REGISTER";
    }
}
