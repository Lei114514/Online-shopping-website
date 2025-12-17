package com.onlineshop.controller;

import com.onlineshop.model.Order;
import com.onlineshop.model.User;
import com.onlineshop.service.OrderService;
import com.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商家訂單管理控制器
 * 處理商家的訂單管理功能
 */
@Controller
@RequestMapping("/merchant/orders")
public class MerchantOrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 商家訂單管理列表
     */
    @GetMapping
    public String orderManagement(@RequestParam(required = false) String status,
                                 @RequestParam(required = false) Long customerId,
                                 Model model) {
        List<Order> orders;
        
        // 根據篩選條件獲取訂單
        if (customerId != null && status != null && !status.trim().isEmpty()) {
            // 同時按客戶和狀態篩選
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrdersByUserIdAndStatus(customerId, orderStatus);
                model.addAttribute("selectedStatus", status);
            } catch (IllegalArgumentException e) {
                orders = orderService.getOrdersByUserId(customerId);
            }
            model.addAttribute("selectedCustomerId", customerId);
        } else if (customerId != null) {
            // 只按客戶篩選
            orders = orderService.getOrdersByUserId(customerId);
            model.addAttribute("selectedCustomerId", customerId);
        } else if (status != null && !status.trim().isEmpty()) {
            // 只按狀態篩選
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrdersByStatus(orderStatus);
                model.addAttribute("selectedStatus", status);
            } catch (IllegalArgumentException e) {
                orders = orderService.getAllOrders();
            }
        } else {
            // 無篩選條件，獲取所有訂單
            orders = orderService.getAllOrders();
        }
        
        // 獲取所有有訂單的客戶列表（用於下拉選單）
        List<Order> allOrders = orderService.getAllOrders();
        List<User> customersWithOrders = allOrders.stream()
            .map(Order::getUser)
            .distinct()
            .collect(Collectors.toList());
        
        model.addAttribute("orders", orders);
        model.addAttribute("orderStatuses", Order.OrderStatus.values());
        model.addAttribute("customers", customersWithOrders);
        return "merchant/order-management";
    }
    
    /**
     * 更新訂單狀態
     */
    @PostMapping("/{id}/update-status")
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
        
        return "redirect:/merchant/orders";
    }
    
    /**
     * 訂單詳情
     */
    @GetMapping("/{id}")
    public String orderDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "merchant/order-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "訂單不存在");
            return "redirect:/merchant/orders";
        }
    }
    
    /**
     * 銷售統計
     */
    @GetMapping("/statistics")
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
        
        return "merchant/statistics";
    }
}