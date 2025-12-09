package com.onlineshop.repository;

import com.onlineshop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 分類數據訪問接口
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 根據名稱查找分類
     */
    List<Category> findByNameContainingIgnoreCase(String name);
    
    /**
     * 查找頂級分類（沒有父分類的分類）
     */
    List<Category> findByParentIsNull();
    
    /**
     * 查找指定父分類的子分類
     */
    List<Category> findByParentId(Long parentId);
    
    /**
     * 根據關鍵字搜索分類
     */
    @Query("SELECT c FROM Category c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchCategories(@Param("keyword") String keyword);
}
