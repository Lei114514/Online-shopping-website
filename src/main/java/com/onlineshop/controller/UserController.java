package com.onlineshop.controller;

import com.onlineshop.model.User;
import com.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 用戶控制器
 * 處理用戶註冊、登錄、個人資料等操作
 */
@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
    
    /**
     * 顯示個人資料頁面
     */
    @GetMapping("/users/profile")
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }
        
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }
    
    /**
     * 更新個人資料
     */
    @PostMapping("/users/profile/update")
    public String updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }
            
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            
            // 創建更新用戶對象
            User updatedUser = new User();
            updatedUser.setFirstName(firstName);
            updatedUser.setLastName(lastName);
            updatedUser.setEmail(email);
            updatedUser.setPhone(phone);
            updatedUser.setAddress(address);
            
            userService.updateUser(user.getId(), updatedUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "個人資料更新成功！");
            return "redirect:/users/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "更新失敗: " + e.getMessage());
            return "redirect:/users/profile";
        }
    }
    
    /**
     * 更新密碼
     */
    @PostMapping("/users/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login";
            }
            
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            
            // 驗證當前密碼
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "當前密碼不正確");
                return "redirect:/users/profile";
            }
            
            // 驗證新密碼確認
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "兩次輸入的新密碼不一致");
                return "redirect:/users/profile";
            }
            
            // 驗證新密碼長度
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("errorMessage", "新密碼長度至少為6個字符");
                return "redirect:/users/profile";
            }
            
            // 更新密碼 - 使用 changePassword 方法
            userService.changePassword(user.getId(), currentPassword, newPassword);
            
            redirectAttributes.addFlashAttribute("successMessage", "密碼修改成功！");
            return "redirect:/users/profile";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "密碼修改失敗: " + e.getMessage());
            return "redirect:/users/profile";
        }
    }
    
    /**
     * 處理帳號注銷請求
     */
    @PostMapping("/users/deactivate")
    public String deactivateAccount(
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        try {
            // 獲取當前登錄用戶
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return "redirect:/login?error=notLoggedIn";
            }
            
            String username = auth.getName();
            User user = userService.getUserByUsername(username);
            
            // 注銷帳號（軟刪除）
            userService.deactivateAccount(user.getId(), password);
            
            // 完全清除會話並登出
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.setInvalidateHttpSession(true);
            logoutHandler.setClearAuthentication(true);
            logoutHandler.logout(request, response, auth);
            
            // 清除 SecurityContext
            SecurityContextHolder.clearContext();
            
            // 注銷成功後直接重定向到登錄頁，不使用 flash attributes（因為會話已被清除）
            return "redirect:/login?deactivated=true";
            
        } catch (IllegalArgumentException e) {
            // 密碼錯誤或其他業務邏輯錯誤，保持登錄狀態
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "注銷失敗: " + e.getMessage());
            return "redirect:/";
        }
    }
}
