package com.onlineshop.controller;

import com.onlineshop.model.User;
import com.onlineshop.service.CartService;
import com.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 購物車控制器
 * 處理購物車相關請求
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 查看購物車
     */
    @GetMapping
    public String viewCart(Model model) {
        Long userId = getCurrentUserId();
        
        model.addAttribute("cartItems", cartService.getCartItems(userId));
        model.addAttribute("cartTotal", cartService.calculateCartTotal(userId));
        model.addAttribute("itemCount", cartService.getCartItemCount(userId));
        
        return "cart";
    }
    
    /**
     * 添加商品到購物車
     */
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") Integer quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            cartService.addToCart(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "商品已添加到購物車！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    /**
     * 更新購物車項目數量
     */
    @PostMapping("/update")
    public String updateCartItem(@RequestParam Long productId,
                                @RequestParam Integer quantity,
                                RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            cartService.updateCartItemQuantity(userId, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "購物車已更新！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    /**
     * 從購物車移除商品
     */
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            cartService.removeFromCart(userId, productId);
            redirectAttributes.addFlashAttribute("successMessage", "商品已從購物車移除！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    /**
     * 清空購物車
     */
    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            cartService.clearCart(userId);
            redirectAttributes.addFlashAttribute("successMessage", "購物車已清空！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "清空購物車失敗");
        }
        
        return "redirect:/cart";
    }
    
    /**
     * 獲取當前用戶ID
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        return user.getId();
    }
}
