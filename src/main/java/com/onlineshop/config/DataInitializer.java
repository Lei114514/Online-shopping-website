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
        electronics.setName("\u96FB\u5B50\u7522\u54C1");
        electronics.setDescription("\u667A\u80FD\u624B\u6A5F\u3001\u96FB\u8166\u53CA\u5176\u4ED6\u96FB\u5B50\u8A2D\u5099");
        electronics = categoryRepository.save(electronics);

        Category accessories = new Category();
        accessories.setName("\u914D\u4EF6");
        accessories.setDescription("\u96FB\u5B50\u7522\u54C1\u914D\u4EF6");
        accessories = categoryRepository.save(accessories);
        
        Category clothing = new Category();
        clothing.setName("\u670D\u88DD");
        clothing.setDescription("\u6642\u5C1A\u670D\u98FE");
        clothing = categoryRepository.save(clothing);
        
        Category books = new Category();
        books.setName("\u66F8\u7C4D");
        books.setDescription("\u66F8\u7C4D\u548C\u51FA\u7248\u7269");
        books = categoryRepository.save(books);
        
        Category home = new Category();
        home.setName("\u5BB6\u5C45\u7528\u54C1");
        home.setDescription("\u5BB6\u96FB\u548C\u751F\u6D3B\u7528\u54C1");
        home = categoryRepository.save(home);

        // 創建商品
        Product product1 = new Product();
        product1.setName("iPhone 15 Pro");
        product1.setDescription("Latest iPhone15 Pro with A17 Pro chip, 6.5-inch Super Retina XDR display, 128GB storage");
        product1.setPrice(new BigDecimal("699.99"));
        product1.setSku("IPHONE-15-PRO-128");
        product1.setStockQuantity(100);
        product1.setImageUrl("/images/products/iphone-15-pro.jpg");
        product1.setStatus(Product.ProductStatus.ACTIVE);
        product1.setCategory(electronics);
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("MacBook Pro 16\"");
        product2.setDescription("Powerful MacBook Pro 16-inch with M3 Pro chip, 16GB RAM, 512GB SSD");
        product2.setPrice(new BigDecimal("1299.99"));
        product2.setSku("MACBOOK-PRO-16-512");
        product2.setStockQuantity(50);
        product2.setImageUrl("macbook-pro.jpg");
        product2.setStatus(Product.ProductStatus.ACTIVE);
        product2.setCategory(electronics);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("AirPods Pro");
        product3.setDescription("Active Noise Cancelling AirPods Pro with immersive audio experience");
        product3.setPrice(new BigDecimal("249.99"));
        product3.setSku("AIRPODS-PRO-2");
        product3.setStockQuantity(200);
        product3.setImageUrl("airpods-pro.jpg");
        product3.setStatus(Product.ProductStatus.ACTIVE);
        product3.setCategory(accessories);
        productRepository.save(product3);

        Product product4 = new Product();
        product4.setName("iPad Air");
        product4.setDescription("Lightweight iPad Air with 10.9-inch Liquid Retina display, M1 chip");
        product4.setPrice(new BigDecimal("599.99"));
        product4.setSku("IPAD-AIR-64");
        product4.setStockQuantity(75);
        product4.setImageUrl("ipad-air.jpg");
        product4.setStatus(Product.ProductStatus.ACTIVE);
        product4.setCategory(electronics);
        productRepository.save(product4);

        Product product5 = new Product();
        product5.setName("Apple Watch Series 9");
        product5.setDescription("Ultimate health and fitness tracking tool with advanced health sensors");
        product5.setPrice(new BigDecimal("399.99"));
        product5.setSku("WATCH-S9-45");
        product5.setStockQuantity(120);
        product5.setImageUrl("apple-watch.jpg");
        product5.setStatus(Product.ProductStatus.ACTIVE);
        product5.setCategory(accessories);
        productRepository.save(product5);

        Product product6 = new Product();
        product6.setName("Magic Keyboard");
        product6.setDescription("Wireless Bluetooth keyboard with rechargeable battery, elegant design");
        product6.setPrice(new BigDecimal("99.99"));
        product6.setSku("KEYBOARD-MAGIC");
        product6.setStockQuantity(150);
        product6.setImageUrl("magic-keyboard.jpg");
        product6.setStatus(Product.ProductStatus.ACTIVE);
        product6.setCategory(accessories);
        productRepository.save(product6);

        // 創建測試用戶
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);

            User customer = new User();
            customer.setUsername("customer");
            customer.setPassword(passwordEncoder.encode("customer123"));
            customer.setEmail("customer@example.com");
            customer.setFirstName("Test");
            customer.setLastName("Customer");
            customer.setRole(User.UserRole.CUSTOMER);
            userRepository.save(customer);

            System.out.println("已創建測試用戶:");
            System.out.println("管理員 - username: admin, password: admin123");
            System.out.println("顧客 - username: customer, password: customer123");
        }

        System.out.println("數據初始化完成！已創建 " + productRepository.count() + " 個商品");
    }
}