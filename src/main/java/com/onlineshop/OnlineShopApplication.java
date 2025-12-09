package com.onlineshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 線上購物系統主應用程序
 * 啟動類，負責初始化Spring Boot應用
 */
@SpringBootApplication
public class OnlineShopApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OnlineShopApplication.class, args);
    }
}
