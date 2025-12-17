package com.onlineshop.service;

import com.onlineshop.model.Order;
import com.onlineshop.model.OrderItem;
import com.onlineshop.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * 郵件服務類
 * 支持異步發送郵件，避免阻塞主線程
 */
@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${spring.mail.enabled:true}")
    private boolean mailEnabled;
    
    /**
     * 異步發送訂單確認郵件（帶確認收貨連結）
     * 使用 @Async 註解使郵件發送在後台執行，不阻塞主線程
     */
    @Async
    public void sendOrderConfirmationEmail(User user, Order order) {
        // 檢查郵件服務是否可用
        if (!isMailServiceAvailable()) {
            System.out.println("郵件服務未配置或不可用，跳過發送郵件");
            System.out.println("訂單確認連結: " + baseUrl + "/orders/confirm/" + order.getConfirmationToken());
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("訂單確認 - " + order.getOrderNumber());
            String confirmationUrl = baseUrl + "/orders/confirm/" + order.getConfirmationToken();
            
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("<html><body style='font-family: Arial, sans-serif;'>");
            emailContent.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>");
            emailContent.append("<h2 style='color: #333;'>訂單確認</h2>");
            emailContent.append("<p>親愛的 ").append(user.getFirstName()).append(" ").append(user.getLastName()).append(",</p>");
            emailContent.append("<p>感謝您的訂購！以下是您的訂單詳情：</p>");
            
            emailContent.append("<div style='background-color: #f5f5f5; padding: 15px; margin: 20px 0; border-radius: 5px;'>");
            emailContent.append("<p><strong>訂單編號:</strong> ").append(order.getOrderNumber()).append("</p>");
            emailContent.append("<p><strong>訂單日期:</strong> ").append(order.getCreatedAt()).append("</p>");
            emailContent.append("<p><strong>總金額:</strong> $").append(order.getTotalAmount()).append("</p>");
            emailContent.append("</div>");
            
            emailContent.append("<h3 style='color: #333;'>訂單項目:</h3>");
            emailContent.append("<table style='width: 100%; border-collapse: collapse;'>");
            emailContent.append("<thead><tr style='background-color: #f0f0f0;'>");
            emailContent.append("<th style='padding: 10px; text-align: left; border: 1px solid #ddd;'>商品名稱</th>");
            emailContent.append("<th style='padding: 10px; text-align: center; border: 1px solid #ddd;'>數量</th>");
            emailContent.append("<th style='padding: 10px; text-align: right; border: 1px solid #ddd;'>小計</th>");
            emailContent.append("</tr></thead><tbody>");
            
            for (OrderItem item : order.getOrderItems()) {
                emailContent.append("<tr>");
                emailContent.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(item.getProductName()).append("</td>");
                emailContent.append("<td style='padding: 10px; text-align: center; border: 1px solid #ddd;'>").append(item.getQuantity()).append("</td>");
                emailContent.append("<td style='padding: 10px; text-align: right; border: 1px solid #ddd;'>$").append(item.getSubtotal()).append("</td>");
                emailContent.append("</tr>");
            }
            
            emailContent.append("</tbody></table>");
            
            emailContent.append("<div style='background-color: #f5f5f5; padding: 15px; margin: 20px 0; border-radius: 5px;'>");
            emailContent.append("<p><strong>配送地址:</strong><br>").append(order.getShippingAddress().replace("\n", "<br>")).append("</p>");
            emailContent.append("<p><strong>訂單狀態:</strong> ").append(getOrderStatusText(order.getStatus())).append("</p>");
            emailContent.append("<p><strong>支付狀態:</strong> ").append(getPaymentStatusText(order.getPaymentStatus())).append("</p>");
            emailContent.append("</div>");
            
            emailContent.append("<div style='margin: 30px 0; padding: 20px; background-color: #e8f4f8; border-left: 4px solid #2196F3; border-radius: 5px;'>");
            emailContent.append("<h3 style='color: #2196F3; margin-top: 0;'>確認收貨</h3>");
            emailContent.append("<p>當您收到商品後，請點擊下面的按鈕確認收貨。確認後，訂單將自動標記為已付款和已送達。</p>");
            emailContent.append("<div style='text-align: center; margin: 20px 0;'>");
            emailContent.append("<a href='").append(confirmationUrl).append("' ");
            emailContent.append("style='display: inline-block; padding: 12px 30px; background-color: #4CAF50; color: white; ");
            emailContent.append("text-decoration: none; border-radius: 5px; font-weight: bold;'>");
            emailContent.append("確認收貨</a>");
            emailContent.append("</div>");
            emailContent.append("<p style='font-size: 12px; color: #666;'>如果按鈕無法點擊，請複製以下連結到瀏覽器：<br>");
            emailContent.append("<a href='").append(confirmationUrl).append("' style='color: #2196F3;'>").append(confirmationUrl).append("</a></p>");
            emailContent.append("</div>");
            
            emailContent.append("<p style='color: #666; font-size: 14px;'>如有任何問題，請聯繫客服。</p>");
            emailContent.append("<p style='color: #666; font-size: 14px;'>感謝您的惠顧！</p>");
            emailContent.append("<hr style='border: none; border-top: 1px solid #ddd; margin: 20px 0;'>");
            emailContent.append("<p style='color: #999; font-size: 12px; text-align: center;'>此郵件由系統自動發送，請勿直接回覆。</p>");
            emailContent.append("</div></body></html>");
            
            helper.setText(emailContent.toString(), true);
            
            mailSender.send(message);
            System.out.println("訂單確認郵件已發送至: " + user.getEmail());
        } catch (MessagingException e) {
            System.err.println("發送郵件失敗: " + e.getMessage());
            // 輸出確認連結到控制台，方便測試
            System.out.println("確認收貨連結: " + baseUrl + "/orders/confirm/" + order.getConfirmationToken());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("發送郵件時發生未知錯誤: " + e.getMessage());
            System.out.println("確認收貨連結: " + baseUrl + "/orders/confirm/" + order.getConfirmationToken());
        }
    }
    
    /**
     * 檢查郵件服務是否可用
     */
    private boolean isMailServiceAvailable() {
        System.out.println("=== 檢查郵件服務配置 ===");
        System.out.println("mailSender: " + (mailSender != null ? "已配置" : "未配置"));
        System.out.println("fromEmail: " + fromEmail);
        System.out.println("mailEnabled: " + mailEnabled);
        
        if (mailSender == null) {
            System.out.println("郵件服務不可用: mailSender 為 null");
            return false;
        }
        if (fromEmail == null || fromEmail.isEmpty() || fromEmail.contains("your-email")) {
            System.out.println("郵件服務不可用: fromEmail 未正確配置");
            return false;
        }
        if (!mailEnabled) {
            System.out.println("郵件服務不可用: mailEnabled 為 false");
            return false;
        }
        System.out.println("郵件服務可用！");
        return true;
    }
    
    /**
     * 異步發送簡單文本郵件
     */
    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!isMailServiceAvailable()) {
            System.out.println("郵件服務未配置，跳過發送");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("發送郵件失敗: " + e.getMessage());
        }
    }
    
    /**
     * 獲取訂單狀態文本
     */
    private String getOrderStatusText(Order.OrderStatus status) {
        switch (status) {
            case PENDING: return "待處理";
            case PROCESSING: return "處理中";
            case SHIPPED: return "已發貨";
            case DELIVERED: return "已送達";
            case CANCELLED: return "已取消";
            default: return status.name();
        }
    }
    
    /**
     * 獲取支付狀態文本
     */
    private String getPaymentStatusText(Order.PaymentStatus status) {
        switch (status) {
            case PENDING: return "待支付";
            case PAID: return "已支付";
            case FAILED: return "支付失敗";
            default: return status.name();
        }
    }
}