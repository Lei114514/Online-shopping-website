package com.onlineshop.repository;

import com.onlineshop.model.CartItem;
import com.onlineshop.model.User;
import com.onlineshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 購物車項目數據訪問接口
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * 根據用戶查找購物車項目
     */
    List<CartItem> findByUser(User user);
    
    /**
     * 根據用戶和商品查找購物車項目
     */
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    
    /**
     * 計算用戶購物車總項目數
     */
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.user = :user")
    Integer countItemsByUser(@Param("user") User user);
    
    /**
     * 計算用戶購物車總金額
     */
    @Query("SELECT SUM(ci.quantity * p.price) FROM CartItem ci JOIN ci.product p WHERE ci.user = :user")
    java.math.BigDecimal calculateCartTotal(@Param("user") User user);
    
    /**
     * 刪除用戶的所有購物車項目
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user = :user")
    void deleteByUser(@Param("user") User user);
    
    /**
     * 刪除用戶的特定購物車項目
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user = :user AND ci.product = :product")
    void deleteByUserAndProduct(@Param("user") User user, @Param("product") Product product);
}
