package com.onlineshop.service;

import com.onlineshop.model.*;
import com.onlineshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 訂單服務類
 */
@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserActivityLogRepository userActivityLogRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * 創建訂單
     */
    public Order createOrder(Long userId, String shippingAddress, String paymentMethod, String notes) {
        User user = getUserById(userId);
        
        // 獲取購物車項目
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("購物車為空");
        }
        
        // 檢查庫存
        for (CartItem item : cartItems) {
            if (!item.getProduct().hasSufficientStock(item.getQuantity())) {
                throw new IllegalArgumentException("商品 " + item.getProduct().getName() + " 庫存不足");
            }
        }
        
        // 創建訂單
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(shippingAddress); // 默認與配送地址相同
        order.setPaymentMethod(paymentMethod);
        order.setNotes(notes);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        
        // 添加訂單項目並減少庫存
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.fromProduct(cartItem.getProduct(), cartItem.getQuantity());
            order.addOrderItem(orderItem);
            
            // 減少庫存
            cartItem.getProduct().reduceStock(cartItem.getQuantity());
            productRepository.save(cartItem.getProduct());
        }
        
        // 計算總金額
        order.calculateTotalAmount();
        
        // 保存訂單
        Order savedOrder = orderRepository.save(order);
        
        // 清空購物車
        cartItemRepository.deleteByUser(user);
        
        // 記錄用戶活動
        logUserActivity(user, UserActivityLog.ActivityType.PLACE_ORDER, 
                       "下單訂單編號: " + savedOrder.getOrderNumber());
        
        // 發送訂單確認郵件（帶確認收貨連結）
        try {
            emailService.sendOrderConfirmationEmail(user, savedOrder);
        } catch (Exception e) {
            System.err.println("發送確認郵件失敗: " + e.getMessage());
        }
        
        return savedOrder;
    }
    
    /**
     * 確認收貨（通過郵件連結）
     */
    public Order confirmDelivery(String confirmationToken) {
        Order order = orderRepository.findByConfirmationToken(confirmationToken)
            .orElseThrow(() -> new IllegalArgumentException("無效的確認令牌"));
        
        // 檢查是否已確認
        if (order.isConfirmed()) {
            throw new IllegalArgumentException("訂單已確認");
        }
        
        // 確認收貨
        order.confirmDelivery();
        
        // 記錄用戶活動
        logUserActivity(order.getUser(), UserActivityLog.ActivityType.PLACE_ORDER, 
                       "確認收貨: " + order.getOrderNumber());
        
        return orderRepository.save(order);
    }
    
    /**
     * 獲取用戶訂單
     */
    public List<Order> getUserOrders(Long userId) {
        User user = getUserById(userId);
        return orderRepository.findByUser(user);
    }
    
    /**
     * 根據ID獲取訂單
     */
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("訂單不存在"));
    }
    
    /**
     * 根據訂單編號獲取訂單
     */
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new IllegalArgumentException("訂單不存在"));
    }
    
    /**
     * 更新訂單狀態
     */
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        
        // 如果訂單已發貨，記錄活動
        if (status == Order.OrderStatus.SHIPPED) {
            logUserActivity(order.getUser(), UserActivityLog.ActivityType.PLACE_ORDER, 
                           "訂單已發貨: " + order.getOrderNumber());
        }
        
        return orderRepository.save(order);
    }
    
    /**
     * 更新支付狀態
     */
    public Order updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(paymentStatus);
        
        // 如果支付成功，更新訂單狀態
        if (paymentStatus == Order.PaymentStatus.PAID) {
            order.setStatus(Order.OrderStatus.PROCESSING);
            
            // 記錄支付成功活動
            logUserActivity(order.getUser(), UserActivityLog.ActivityType.PLACE_ORDER, 
                           "支付成功: " + order.getOrderNumber());
        }
        
        return orderRepository.save(order);
    }
    
    /**
     * 取消訂單
     */
    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        
        // 檢查是否可以取消
        if (order.getStatus() == Order.OrderStatus.SHIPPED || 
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("訂單已發貨，無法取消");
        }
        
        // 恢復庫存
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.increaseStock(item.getQuantity());
            productRepository.save(product);
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        
        // 記錄取消活動
        logUserActivity(order.getUser(), UserActivityLog.ActivityType.PLACE_ORDER, 
                       "取消訂單: " + order.getOrderNumber());
        
        return orderRepository.save(order);
    }
    
    /**
     * 獲取銷售統計
     */
    public List<Object[]> getSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getSalesStatistics(startDate, endDate);
    }
    
    /**
     * 獲取熱銷商品
     */
    public List<Object[]> getTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getTopSellingProducts(startDate, endDate);
    }
    
    /**
     * 獲取所有訂單
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    /**
     * 根據狀態獲取訂單
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    /**
     * 記錄用戶活動
     */
    private void logUserActivity(User user, String activityType, String details) {
        UserActivityLog log = new UserActivityLog();
        log.setUser(user);
        log.setActivityType(activityType);
        log.setActivityDetails(details);
        userActivityLogRepository.save(log);
    }
    
    /**
     * 根據ID獲取用戶
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("用戶不存在"));
    }
}
