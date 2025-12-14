package com.onlineshop.controller;

import com.onlineshop.model.*;
import com.onlineshop.repository.*;
import com.onlineshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理員控制器
 * 處理客戶管理、訂單管理和活動日誌查看
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserActivityLogRepository activityLogRepository;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 管理員儀表板
     */
    @GetMapping
    public String dashboard(Model model) {
        // 統計數據
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long processingOrders = orderRepository.countByStatus(Order.OrderStatus.PROCESSING);
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("processingOrders", processingOrders);
        
        // 最近的訂單
        List<Order> recentOrders = orderRepository.findTop10ByOrderByCreatedAtDesc();
        model.addAttribute("recentOrders", recentOrders);
        
        // 最近的用戶活動
        List<UserActivityLog> recentActivities = activityLogRepository.findTop20ByOrderByCreatedAtDesc();
        model.addAttribute("recentActivities", recentActivities);
        
        return "admin/dashboard";
    }
    
    /**
     * 客戶管理列表
     */
    @GetMapping("/customers")
    public String customerList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) User.UserRole role,
            Model model) {
        
        List<User> customers;
        
        if (search != null && !search.trim().isEmpty()) {
            customers = userRepository.searchUsers(search);
        } else if (role != null) {
            customers = userRepository.findByRole(role);
        } else {
            customers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        model.addAttribute("customers", customers);
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", role);
        model.addAttribute("roles", User.UserRole.values());
        
        return "admin/customers";
    }
    
    /**
     * 客戶詳情
     */
    @GetMapping("/customers/{id}")
    public String customerDetail(@PathVariable Long id, Model model) {
        User customer = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("客戶不存在"));
        
        // 獲取客戶的訂單
        List<Order> orders = orderRepository.findByUser(customer);
        
        // 獲取客戶的活動日誌
        List<UserActivityLog> activities = activityLogRepository.findTop50ByUserOrderByCreatedAtDesc(customer);
        
        // 統計數據
        long totalOrders = orders.size();
        long completedOrders = orders.stream()
            .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
            .count();
        
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orders);
        model.addAttribute("activities", activities);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("completedOrders", completedOrders);
        
        return "admin/customer-detail";
    }
    
    /**
     * 更新客戶狀態
     */
    @PostMapping("/customers/{id}/toggle-status")
    public String toggleCustomerStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User customer = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("客戶不存在"));
        
        customer.setEnabled(!customer.getEnabled());
        userRepository.save(customer);
        
        redirectAttributes.addFlashAttribute("message", 
            "客戶狀態已更新為: " + (customer.getEnabled() ? "啟用" : "停用"));
        
        return "redirect:/admin/customers/" + id;
    }
    
    /**
     * 訂單管理列表
     */
    @GetMapping("/orders")
    public String orderList(
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) Order.PaymentStatus paymentStatus,
            @RequestParam(required = false) String search,
            Model model) {
        
        List<Order> orders;
        
        if (search != null && !search.trim().isEmpty()) {
            orders = orderRepository.findByOrderNumberContaining(search);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else if (paymentStatus != null) {
            orders = orderRepository.findByPaymentStatus(paymentStatus);
        } else {
            orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPaymentStatus", paymentStatus);
        model.addAttribute("search", search);
        model.addAttribute("orderStatuses", Order.OrderStatus.values());
        model.addAttribute("paymentStatuses", Order.PaymentStatus.values());
        
        return "admin/orders";
    }
    
    /**
     * 訂單詳情
     */
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("orderStatuses", Order.OrderStatus.values());
        model.addAttribute("paymentStatuses", Order.PaymentStatus.values());
        
        return "admin/order-detail";
    }
    
    /**
     * 更新訂單狀態
     */
    @PostMapping("/orders/{id}/update-status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status,
            RedirectAttributes redirectAttributes) {
        
        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "訂單狀態已更新");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新失敗: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }
    
    /**
     * 更新支付狀態
     */
    @PostMapping("/orders/{id}/update-payment")
    public String updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Order.PaymentStatus paymentStatus,
            RedirectAttributes redirectAttributes) {
        
        try {
            orderService.updatePaymentStatus(id, paymentStatus);
            redirectAttributes.addFlashAttribute("message", "支付狀態已更新");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新失敗: " + e.getMessage());
        }
        
        return "redirect:/admin/orders/" + id;
    }
    
    /**
     * 用戶活動日誌
     */
    @GetMapping("/activity-logs")
    public String activityLogs(
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer days,
            Model model) {
        
        List<UserActivityLog> logs;
        
        if (days != null && days > 0) {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            logs = activityLogRepository.findByCreatedAtAfter(startDate);
        } else if (activityType != null && !activityType.isEmpty()) {
            logs = activityLogRepository.findByActivityType(activityType);
        } else if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                logs = activityLogRepository.findByUser(user);
            } else {
                logs = activityLogRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
            }
        } else {
            logs = activityLogRepository.findTop100ByOrderByCreatedAtDesc();
        }
        
        model.addAttribute("logs", logs);
        model.addAttribute("selectedActivityType", activityType);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("selectedDays", days);
        model.addAttribute("users", userRepository.findAll());
        
        return "admin/activity-logs";
    }
    
    /**
     * 活動統計
     */
    @GetMapping("/statistics")
    public String statistics(
            @RequestParam(required = false, defaultValue = "30") Integer days,
            Model model) {
        
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();
        
        // 銷售統計
        List<Object[]> salesStats = orderService.getSalesStatistics(startDate, endDate);
        
        // 熱銷商品
        List<Object[]> topProducts = orderService.getTopSellingProducts(startDate, endDate);
        
        // 活動類型統計
        List<Object[]> activityStats = activityLogRepository.getActivityTypeStatistics(startDate, endDate);
        
        model.addAttribute("days", days);
        model.addAttribute("salesStats", salesStats);
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("activityStats", activityStats);
        
        return "admin/statistics";
    }
}