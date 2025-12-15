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
        System.out.println("開始初始化數據庫...");

        // 創建分類（僅在沒有分類時創建）
        if (categoryRepository.count() == 0) {
            Category electronics = new Category();
            electronics.setName("\u96FB\u5B50\u7522\u54C1");
            electronics.setDescription("\u667A\u80FD\u624B\u6A5F\u3001\u96FB\u8166\u53CA\u5176\u4ED6\u96FB\u5B50\u8A2D\u5099");
            categoryRepository.save(electronics);

            Category accessories = new Category();
            accessories.setName("\u914D\u4EF6");
            accessories.setDescription("\u96FB\u5B50\u7522\u54C1\u914D\u4EF6");
            categoryRepository.save(accessories);
            
            Category clothing = new Category();
            clothing.setName("\u670D\u88DD");
            clothing.setDescription("\u6642\u5C1A\u670D\u98FE");
            categoryRepository.save(clothing);
            
            Category books = new Category();
            books.setName("\u66F8\u7C4D");
            books.setDescription("\u66F8\u7C4D\u548C\u51FA\u7248\u7269");
            categoryRepository.save(books);
            
            Category home = new Category();
            home.setName("\u5BB6\u5C45\u7528\u54C1");
            home.setDescription("\u5BB6\u96FB\u548C\u751F\u6D3B\u7528\u54C1");
            categoryRepository.save(home);

            System.out.println("已創建 " + categoryRepository.count() + " 個商品分類");
        } else {
            System.out.println("分類已存在，跳過分類初始化");
        }

        // 創建測試用戶（僅在沒有用戶時創建）
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);

            User sales = new User();
            sales.setUsername("sales");
            sales.setPassword(passwordEncoder.encode("sales123"));
            sales.setEmail("sales@example.com");
            sales.setFirstName("Test");
            sales.setLastName("Sales");
            sales.setRole(User.UserRole.SALES);
            userRepository.save(sales);

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
            System.out.println("銷售人員 - username: sales, password: sales123");
            System.out.println("顧客 - username: customer, password: customer123");
        } else {
            System.out.println("用戶已存在，跳過用戶初始化");
        }

        System.out.println("數據初始化完成！");
        System.out.println("當前商品數量: " + productRepository.count());
        System.out.println("所有商品需要由商家用戶自行添加");
    }
}