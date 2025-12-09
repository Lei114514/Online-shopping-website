package com.onlineshop.controller;

import com.onlineshop.model.Product;
import com.onlineshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        model.addAttribute("topProducts", topProducts);
        return "home";
    }
    
    /**
     * 商品列表頁
     */
    @GetMapping("/products")
    public String products(@RequestParam(required = false) String search, 
                          @RequestParam(required = false) Long categoryId,
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
        
        model.addAttribute("products", products);
        return "products";
    }
    
    /**
     * 商品詳情頁
     */
    @GetMapping("/products/{id}")
    public String productDetail(@RequestParam Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }
    
    /**
     * 登錄頁
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * 註冊頁
     */
    @GetMapping("/register")
    public String register() {
        return "register";
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
}
