package com.onlineshop.controller;

import com.onlineshop.model.Order;
import com.onlineshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商家訂單管理控制器
 * 處理商家的訂單管理功能
 */
@Controller
@RequestMapping("/merchant/orders")
public class MerchantOrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 商家訂單管理列表
     */
    @GetMapping
    public String orderManagement(@RequestParam(required = false) String status,
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