package com.onlineshop.service;

import com.onlineshop.model.Product;
import com.onlineshop.model.Category;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 商品服務類
 */
@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    private final String UPLOAD_DIR = "uploads/products/";
    
    /**
     * 創建新商品
     */
    public Product createProduct(Product product, Long categoryId) {
        // 設置分類
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("分類不存在"));
            product.setCategory(category);
        }
        
        // 生成SKU如果未提供
        if (product.getSku() == null || product.getSku().isEmpty()) {
            product.setSku(generateSku());
        }
        
        return productRepository.save(product);
    }
    
    /**
     * 更新商品信息
     */
    public Product updateProduct(Long productId, Product updatedProduct, Long categoryId) {
        Product existingProduct = getProductById(productId);
        
        // 更新基本信息
        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getStockQuantity() != null) {
            existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        }
        if (updatedProduct.getStatus() != null) {
            existingProduct.setStatus(updatedProduct.getStatus());
        }
        
        // 更新分類
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("分類不存在"));
            existingProduct.setCategory(category);
        }
        
        return productRepository.save(existingProduct);
    }
    
    /**
     * 根據ID獲取商品
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
    }
    
    /**
     * 獲取所有商品
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    /**
     * 搜索商品
     */
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }
    
    /**
     * 根據分類獲取商品
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("分類不存在"));
        return productRepository.findByCategory(category);
    }
    
    /**
     * 獲取熱銷商品
     */
    public List<Product> getTopSellingProducts() {
        return productRepository.findTopSellingProducts();
    }
    
    /**
     * 獲取庫存不足的商品
     */
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }
    
    /**
     * 刪除商品
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("商品不存在");
        }
        productRepository.deleteById(id);
    }
    
    /**
     * 上傳商品圖片
     */
    public String uploadProductImage(MultipartFile file) throws IOException {
        // 創建上傳目錄
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        // 保存文件
        Files.copy(file.getInputStream(), filePath);
        
        return "/" + UPLOAD_DIR + fileName;
    }
    
    /**
     * 更新商品庫存
     */
    public Product updateStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        product.setStockQuantity(quantity);
        
        // 更新狀態
        if (quantity <= 0) {
            product.setStatus(Product.ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == Product.ProductStatus.OUT_OF_STOCK) {
            product.setStatus(Product.ProductStatus.ACTIVE);
        }
        
        return productRepository.save(product);
    }
    
    /**
     * 減少商品庫存
     */
    public Product reduceStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        product.reduceStock(quantity);
        return productRepository.save(product);
    }
    
    /**
     * 增加商品庫存
     */
    public Product increaseStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        product.increaseStock(quantity);
        return productRepository.save(product);
    }
    
    /**
     * 生成SKU
     */
    private String generateSku() {
        return "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
