package com.onlineshop.controller;

import com.onlineshop.model.Product;
import com.onlineshop.model.Category;
import com.onlineshop.model.User;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.UserService;
import com.onlineshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 商家商品管理控制器
 * 處理商家對自己商品的增刪改查操作
 */
@Controller
@RequestMapping("/merchant/products")
public class MerchantProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * 獲取當前登錄用戶
     */
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }
    
    /**
     * 顯示商家商品列表
     */
    @GetMapping
    public String listProducts(Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<Product> products = productService.getProductsByMerchant(currentUser.getId());
        
        model.addAttribute("products", products);
        model.addAttribute("currentUser", currentUser);
        
        return "merchant/product-list";
    }
    
    /**
     * 顯示新增商品表單
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "merchant/product-form";
    }
    
    /**
     * 處理新增商品
     */
    @PostMapping
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam(required = false) Long categoryId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            productService.createMerchantProduct(product, categoryId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "商品創建成功！");
            return "redirect:/merchant/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "創建商品失敗: " + e.getMessage());
            return "redirect:/merchant/products/new";
        }
    }
    
    /**
     * 顯示編輯商品表單
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            
            // 驗證商品所有權
            if (!productService.isProductOwnedByUser(id, currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "您沒有權限編輯此商品");
                return "redirect:/merchant/products";
            }
            
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryRepository.findAll());
            
            return "merchant/product-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "商品不存在");
            return "redirect:/merchant/products";
        }
    }
    
    /**
     * 處理更新商品
     */
    @PostMapping("/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(required = false) Long categoryId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            productService.updateMerchantProduct(id, product, categoryId, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "商品更新成功！");
            return "redirect:/merchant/products";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/merchant/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "更新商品失敗: " + e.getMessage());
            return "redirect:/merchant/products/" + id + "/edit";
        }
    }
    
    /**
     * 刪除商品
     */
    @PostMapping("/{id}/delete")
    public String deleteProduct(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            productService.deleteMerchantProduct(id, currentUser.getId());
            
            redirectAttributes.addFlashAttribute("successMessage", "商品刪除成功！");} catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "刪除商品失敗: " + e.getMessage());
        }
        
        return "redirect:/merchant/products";
    }
    
    /**
     * 切換商品狀態（上架/下架）
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleProductStatus(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            // 驗證商品所有權
            if (!productService.isProductOwnedByUser(id, currentUser.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "您沒有權限操作此商品");
                return "redirect:/merchant/products";
            }
            
            Product product = productService.getProductById(id);
            
            // 切換狀態
            if (product.getStatus() == Product.ProductStatus.ACTIVE) {
                product.setStatus(Product.ProductStatus.INACTIVE);
            } else if (product.getStatus() == Product.ProductStatus.INACTIVE) {
                product.setStatus(Product.ProductStatus.ACTIVE);
            }
            
            productService.updateProduct(id, product, null);
            redirectAttributes.addFlashAttribute("successMessage", "商品狀態更新成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "操作失敗: " + e.getMessage());
        }
        
        return "redirect:/merchant/products";
    }
}