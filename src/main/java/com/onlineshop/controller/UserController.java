package com.onlineshop.controller;

import com.onlineshop.model.User;
import com.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 用戶控制器
 * 處理用戶註冊、登錄等操作
 */
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 顯示註冊頁面
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    /**
     * 處理用戶註冊
     */
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute User user,
            @RequestParam String confirmPassword,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        try {
            // 驗證密碼確認
            if (!user.getPassword().equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "兩次輸入的密碼不一致");
                return "redirect:/register";
            }
            
            // 設置用戶角色
            if ("SALES".equals(role)) {
                user.setRole(User.UserRole.SALES);
            } else {
                user.setRole(User.UserRole.CUSTOMER);
            }
            
            // 註冊用戶
            userService.registerUser(user);
            
            String roleText = "SALES".equals(role) ? "商家" : "普通用戶";
            redirectAttributes.addFlashAttribute("successMessage",
                "註冊成功！您已成功註冊為" + roleText + ",請登錄");
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "註冊失敗: " + e.getMessage());
            return "redirect:/register";
        }
    }
    
    /**
     * 顯示登錄頁面
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
