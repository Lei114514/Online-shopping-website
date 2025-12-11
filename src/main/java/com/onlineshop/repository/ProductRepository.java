package com.onlineshop.repository;

import com.onlineshop.model.Product;
import com.onlineshop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 商品數據訪問接口
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 根據分類查找商品
     */
    List<Product> findByCategory(Category category);
    
    /**
     * 根據狀態查找商品
     */
    List<Product> findByStatus(Product.ProductStatus status);
    
    /**
     * 根據SKU查找商品
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * 查找價格範圍內的商品
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * 根據關鍵字搜索商品
     */
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    /**
     * 查找庫存不足的商品
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
    /**
     * 查找熱銷商品（按銷售數量排序）
     */
    @Query("SELECT p FROM Product p LEFT JOIN OrderItem oi ON p.id = oi.product.id " +
           "GROUP BY p.id ORDER BY SUM(oi.quantity) DESC")
    List<Product> findTopSellingProducts();
    
    /**
     * 根據創建者查找商品
     */
    @Query("SELECT p FROM Product p WHERE p.createdBy.id = :userId ORDER BY p.createdAt DESC")
    List<Product> findByCreatedById(@Param("userId") Long userId);
    
    /**
     * 檢查商品是否屬於指定用戶
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Product p " +
           "WHERE p.id = :productId AND p.createdBy.id = :userId")
    boolean isProductOwnedByUser(@Param("productId") Long productId, @Param("userId") Long userId);
}
