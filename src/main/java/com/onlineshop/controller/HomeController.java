package com.onlineshop.controller;

import com.onlineshop.model.Product;
import com.onlineshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 主控制器
 * 處理首頁和公共頁面請求
 */
@Controller
public class HomeController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 首頁
     */
    @GetMapping("/")
    public String home(Model model) {
        // 獲取熱銷商品
        List<Product> topProducts = productService.getTopSellingProducts();
        
        // 如果數據庫沒有數據，使用測試數據
        if (topProducts == null || topProducts.isEmpty()) {
            topProducts = createSampleProducts();
        }
        
        model.addAttribute("topProducts", topProducts);
        return "home";
    }
    
    /**
     * 別名路由到首頁
     */
    @GetMapping("/home")
    public String homeAlias(Model model) {
        return home(model);
    }
    
    /**
     * 商品列表頁
     */
    @GetMapping("/products")
    public String products(@RequestParam(required = false) String search,@RequestParam(required = false) Long categoryId,
                          Model model) {
        List<Product> products;
        
        if (search != null && !search.trim().isEmpty()) {
            // 搜索商品
            products = productService.searchProducts(search);
            model.addAttribute("searchQuery", search);
        } else if (categoryId != null) {
            // 按分類篩選
            products = productService.getProductsByCategory(categoryId);
        } else {
            // 所有商品
            products = productService.getAllProducts();
        }
        
        // 如果沒有數據，使用測試數據
        if (products == null || products.isEmpty()) {
            products = createSampleProducts();
        }
        
        model.addAttribute("products", products);
        return "products";
    }
    
    /**
     * 商品詳情頁
     */
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
        } catch (Exception e) {
            // 如果找不到商品，使用測試數據
            Product product = createSampleProducts().get(0);
            product.setId(id);
            model.addAttribute("product", product);
        }
        return "product-detail";
    }
    
    /**
     * 關於我們頁
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    /**
     * 聯繫我們頁
     */
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    /**
     * 訪問被拒絕頁
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    
    /**
     * 創建測試商品數據
     */
    private List<Product> createSampleProducts() {
        List<Product> products = new ArrayList<>();
        
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("iPhone 15 Pro");
        product1.setDescription("最新款 iPhone 15 Pro，搭載 A17 Pro 芯片，配備 6.5 英寸超視網膜 XDR 顯示屏，128GB 存儲空間");
        product1.setPrice(new BigDecimal("699.99"));
        product1.setSku("IPHONE-15-PRO-128");
        product1.setStockQuantity(100);
        product1.setImageUrl("https://images.unsplash.com/photo-1678685888221-cda1673efe2e?w=400&h=300&fit=crop");
        product1.setStatus(Product.ProductStatus.ACTIVE);
        products.add(product1);
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("MacBook Pro 16\"");
        product2.setDescription("強大的 MacBook Pro 16 英寸，配備 M3 Pro 芯片，16GB RAM，512GB SSD");
        product2.setPrice(new BigDecimal("1299.99"));
        product2.setSku("MACBOOK-PRO-16-512");
        product2.setStockQuantity(50);
        product2.setImageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=300&fit=crop");
        product2.setStatus(Product.ProductStatus.ACTIVE);
        products.add(product2);
        
        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("AirPods Pro");
        product3.setDescription("主動降噪 AirPods Pro，提供沉浸式音質體驗");
        product3.setPrice(new BigDecimal("19.99"));
        product3.setSku("AIRPODS-PRO-2");
        product3.setStockQuantity(200);
        product3.setImageUrl("https://images.unsplash.com/photo-1606841837239-c5a1a4a07af7?w=400&h=300&fit=crop");
        product3.setStatus(Product.ProductStatus.ACTIVE);
        products.add(product3);
        
        return products;
    }
}
