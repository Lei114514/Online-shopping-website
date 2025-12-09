package com.onlineshop.controller;

import com.onlineshop.model.Order;
import com.onlineshop.model.User;
import com.onlineshop.service.OrderService;
import com.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 訂單控制器
 * 處理訂單相關請求
 */
@Controller
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 查看我的訂單
     */
    @GetMapping
    public String myOrders(Model model) {
        Long userId = getCurrentUserId();
        List<Order> orders = orderService.getUserOrders(userId);
        model.addAttribute("orders", orders);
        return "orders/my-orders";
    }
    
    /**
     * 訂單詳情
     */
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        
        // 檢查訂單是否屬於當前用戶
        Long userId = getCurrentUserId();
        if (!order.getUser().getId().equals(userId)) {
            return "redirect:/access-denied";
        }
        
        model.addAttribute("order", order);
        return "orders/order-detail";
    }
    
    /**
     * 結賬頁面
     */
    @GetMapping("/checkout")
    public String checkout(Model model) {
        Long userId = getCurrentUserId();
        User user = userService.getUserById(userId);
        
        model.addAttribute("user", user);
        return "orders/checkout";
    }
    
    /**
     * 提交訂單
     */
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String shippingAddress,
                            @RequestParam String paymentMethod,
                            @RequestParam(required = false) String notes,
                            RedirectAttributes redirectAttributes) {
        try {
            Long userId = getCurrentUserId();
            Order order = orderService.createOrder(userId, shippingAddress, paymentMethod, notes);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "訂單創建成功！訂單編號：" + order.getOrderNumber());
            return "redirect:/orders/" + order.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }
    }
    
    /**
     * 取消訂單
     */
    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.cancelOrder(id);
            
            // 檢查訂單是否屬於當前用戶
            Long userId = getCurrentUserId();
            if (!order.getUser().getId().equals(userId)) {
                return "redirect:/access-denied";
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "訂單已取消！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/orders/" + id;
    }
    
    /**
     * 銷售端：訂單管理
     */
    @GetMapping("/sales")
    public String salesOrderManagement(@RequestParam(required = false) String status,
                                      Model model) {
        List<Order> orders;
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrdersByStatus(orderStatus);
                model.addAttribute("selectedStatus", status);
            } catch (IllegalArgumentException e) {
                orders = orderService.getAllOrders();
            }
        } else {
            orders = orderService.getAllOrders();
        }
        
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", Order.OrderStatus.values());
        return "sales/order-management";
    }
    
    /**
     * 銷售端：更新訂單狀態
     */
    @PostMapping("/sales/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                   @RequestParam String status,
                                   RedirectAttributes redirectAttributes) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orderService.updateOrderStatus(id, orderStatus);
            redirectAttributes.addFlashAttribute("successMessage", "訂單狀態更新成功！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/orders/sales";
    }
    
    /**
     * 銷售端：銷售統計
     */
    @GetMapping("/sales/statistics")
    public String salesStatistics(@RequestParam(required = false) String period,
                                 Model model) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;
        
        // 根據期間設置開始日期
        if ("daily".equals(period)) {
            startDate = endDate.minusDays(1);
        } else if ("weekly".equals(period)) {
            startDate = endDate.minusWeeks(1);
        } else if ("monthly".equals(period)) {
            startDate = endDate.minusMonths(1);
        } else {
            // 默認最近30天
            startDate = endDate.minusDays(30);
            period = "monthly";
        }
        
        List<Object[]> salesStats = orderService.getSalesStatistics(startDate, endDate);
        List<Object[]> topProducts = orderService.getTopSellingProducts(startDate, endDate);
        
        model.addAttribute("salesStats", salesStats);
        model.addAttribute("topProducts", topProducts);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("selectedPeriod", period);
        
        return "sales/statistics";
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
    
    // 這些方法需要在OrderService中添加
    private List<Order> getAllOrders() {
        // 實現獲取所有訂單的邏輯
        return List.of();
    }
    
    private List<Order> getOrdersByStatus(Order.OrderStatus status) {
        // 實現根據狀態獲取訂單的邏輯
        return List.of();
    }
}
