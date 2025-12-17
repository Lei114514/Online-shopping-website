package com.onlineshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 線上購物系統主應用程序
 * 啟動類，負責初始化Spring Boot應用
 *
 * @EnableAsync 啟用異步方法執行支持，用於郵件發送等耗時操作
 */
@SpringBootApplication
@EnableAsync
public class OnlineShopApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OnlineShopApplication.class, args);
    }
}
