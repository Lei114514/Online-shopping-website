package com.onlineshop.config;

import com.onlineshop.model.Category;
import com.onlineshop.model.User;
import com.onlineshop.repository.CategoryRepository;
import com.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer
 * Creates default users on application startup
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting database initialization...");

        // Create default categories (only if none exist)
        if (categoryRepository.count() == 0) {
            Category electronics = new Category();
            electronics.setName("Electronics");
            electronics.setDescription("Smartphones, computers and other electronic devices");
            categoryRepository.save(electronics);

            Category accessories = new Category();
            accessories.setName("Accessories");
            accessories.setDescription("Electronic accessories");
            categoryRepository.save(accessories);
            
            Category clothing = new Category();
            clothing.setName("Clothing");
            clothing.setDescription("Fashion and apparel");
            categoryRepository.save(clothing);
            
            Category books = new Category();
            books.setName("Books");
            books.setDescription("Books and publications");
            categoryRepository.save(books);
            
            Category home = new Category();
            home.setName("Home");
            home.setDescription("Home appliances and daily necessities");
            categoryRepository.save(home);

            System.out.println("Created " + categoryRepository.count() + " product categories");
        } else {
            System.out.println("Categories already exist, skipping category initialization");
        }

        // Create default users (only if none exist)
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

            System.out.println("Created test users:");
            System.out.println("Admin - username: admin, password: admin123");
            System.out.println("Sales - username: sales, password: sales123");
            System.out.println("Customer - username: customer, password: customer123");
        } else {
            System.out.println("Users already exist, skipping user initialization");
        }

        System.out.println("Database initialization complete!");
        System.out.println("All products must be added by merchant users.");
    }
}