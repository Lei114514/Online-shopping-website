package com.onlineshop.controller;

import com.onlineshop.model.Product;
import com.onlineshop.model.Category;
import com.onlineshop.service.ProductService;
import com.onlineshop.repository.CategoryRepository;
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
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * 首頁
     */
    @GetMapping("/")
    public String home(Model model) {
        //獲取熱銷商品
        List<Product> topProducts = productService.getTopSellingProducts();
        
        // 如果數據庫沒有數據，顯示空列表
        if (topProducts == null) {
            topProducts = new ArrayList<>();
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
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            // 所有商品
            products = productService.getAllProducts();
        }
        
        // 不再使用測試數據 - 如果沒有商品就顯示空列表
        if (products == null) {
            products = new ArrayList<>();
        }
        
        // 添加分類列表
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
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
            // 商品不存在，返回錯誤頁面
            return "redirect:/products?error=notfound";
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
     *訪問被拒絕頁
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    
}
