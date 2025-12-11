package com.onlineshop.config;

import com.onlineshop.model.Category;
import com.onlineshop.model.Product;
import com.onlineshop.model.User;
import com.onlineshop.repository.CategoryRepository;
import com.onlineshop.repository.ProductRepository;
import com.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 數據初始化器
 * 在應用啟動時自動填充測試數據
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 檢查是否已有數據
        if (productRepository.count() > 0) {
            System.out.println("數據庫已有數據，跳過初始化");
            return;
        }

        System.out.println("開始初始化數據庫...");

        // 創建分類
        Category electronics = new Category();
        electronics.setName("電子產品");
        electronics.setDescription("手機、電腦及其他電子設備");
        electronics = categoryRepository.save(electronics);

        Category accessories = new Category();
        accessories.setName("配件");
        accessories.setDescription("電子產品配件");
        accessories = categoryRepository.save(accessories);

        // 創建商品
        Product product1 = new Product();
        product1.setName("iPhone 15 Pro");
        product1.setDescription("最新款 iPhone 15 Pro，搭載 A17 Pro 芯片，配備 6.5 英寸超視網膜 XDR 顯示屏，128GB 存儲空間");
        product1.setPrice(new BigDecimal("699.99"));
        product1.setSku("IPHONE-15-PRO-128");
        product1.setStockQuantity(100);
        product1.setImageUrl("/images/products/iphone-15-pro.jpg");
        product1.setStatus(Product.ProductStatus.ACTIVE);
        product1.setCategory(electronics);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("MacBook Pro 16\"");
        product2.setDescription("強大的 MacBook Pro 16英寸，配備 M3 Pro 芯片，16GB RAM，512GB SSD");
        product2.setPrice(new BigDecimal("1299.99"));
        product2.setSku("MACBOOK-PRO-16-512");
        product2.setStockQuantity(50);
        product2.setImageUrl("/images/products/macbook-pro.jpg");
        product2.setStatus(Product.ProductStatus.ACTIVE);
        product2.setCategory(electronics);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("AirPods Pro");
        product3.setDescription("主動降噪 AirPods Pro，提供沉浸式音質體驗");
        product3.setPrice(new BigDecimal("249.99"));
        product3.setSku("AIRPODS-PRO-2");
        product3.setStockQuantity(200);
        product3.setImageUrl("/images/products/airpods-pro.jpg");
        product3.setStatus(Product.ProductStatus.ACTIVE);
        product3.setCategory(accessories);
        productRepository.save(product3);

        Product product4 = new Product();
        product4.setName("iPad Air");
        product4.setDescription("輕薄的 iPad Air，配備 10.9 英寸 Liquid Retina 顯示屏，M1 芯片");
        product4.setPrice(new BigDecimal("599.99"));
        product4.setSku("IPAD-AIR-64");
        product4.setStockQuantity(75);
        product4.setImageUrl("/images/products/ipad-air.jpg");
        product4.setStatus(Product.ProductStatus.ACTIVE);
        product4.setCategory(electronics);
        productRepository.save(product4);

        Product product5 = new Product();
        product5.setName("Apple Watch Series 9");
        product5.setDescription("健康和運動追踪的終極工具，配備先進的健康傳感器");
        product5.setPrice(new BigDecimal("399.99"));
        product5.setSku("WATCH-S9-45");
        product5.setStockQuantity(120);
        product5.setImageUrl("/images/products/apple-watch.jpg");
        product5.setStatus(Product.ProductStatus.ACTIVE);
        product5.setCategory(accessories);
        productRepository.save(product5);

        Product product6 = new Product();
        product6.setName("Magic Keyboard");
        product6.setDescription("無線藍牙鍵盤，內置可充電電池，優雅設計");
        product6.setPrice(new BigDecimal("99.99"));
        product6.setSku("KEYBOARD-MAGIC");
        product6.setStockQuantity(150);
        product6.setImageUrl("/images/products/magic-keyboard.jpg");
        product6.setStatus(Product.ProductStatus.ACTIVE);
        product6.setCategory(accessories);
        productRepository.save(product6);

        // 創建測試用戶
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("管理員");
            admin.setLastName("系統");
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);

            User customer = new User();
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setEmail("customer@example.com");
            customer.setFirstName("測試");
            customer.setLastName("用戶");
            customer.setRole(User.UserRole.CUSTOMER);
            userRepository.save(customer);

            System.out.println("已創建測試用戶:");
            System.out.println("管理員 - username: admin, password: admin123");
            System.out.println("  顧客 - username: customer, password: customer123");
        }

        System.out.println("數據初始化完成！已創建 " + productRepository.count() + " 個商品");
    }
}