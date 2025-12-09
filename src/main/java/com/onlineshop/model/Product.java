package com.onlineshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品實體類
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(unique = true, length = 50)
    private String sku;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 檢查是否有足夠庫存
     */
    public boolean hasSufficientStock(int requestedQuantity) {
        return stockQuantity >= requestedQuantity;
    }
    
    /**
     * 減少庫存
     */
    public void reduceStock(int quantity) {
        if (hasSufficientStock(quantity)) {
            this.stockQuantity -= quantity;
            if (this.stockQuantity == 0) {
                this.status = ProductStatus.OUT_OF_STOCK;
            }
        } else {
            throw new IllegalArgumentException("庫存不足");
        }
    }
    
    /**
     * 增加庫存
     */
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
        if (this.stockQuantity > 0 && this.status == ProductStatus.OUT_OF_STOCK) {
            this.status = ProductStatus.ACTIVE;
        }
    }
    
    /**
     * 商品狀態枚舉
     */
    public enum ProductStatus {
        ACTIVE,        // 正常銷售
        INACTIVE,      // 下架
        OUT_OF_STOCK   // 缺貨
    }
}
