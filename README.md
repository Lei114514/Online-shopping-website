# 線上購買網頁系統

## 專案概述
這是一個基於Spring Boot的線上購買網頁系統，包含顧客端和銷售端功能。

## 技術棧
- **後端**: Spring Boot 3.x, Spring Security, Spring Data JPA
- **前端**: HTML, CSS, JavaScript, Thymeleaf
- **數據庫**: MySQL 8.0
- **服務器**: Tomcat (內嵌於Spring Boot)
- **構建工具**: Maven
- **容器化**: Docker
- **郵件服務**: Spring Mail

## 功能模塊

### 顧客端功能
1. 用戶註冊、登錄、註銷
2. 產品瀏覽與搜索
3. 購物車管理
4. 訂單處理與支付
5. 訂單狀態查詢與歷史記錄
6. 郵件確認通知

### 銷售端功能
1. 商品目錄管理（增刪改查）
2. 訂單管理
3. 銷售統計報表
4. 客戶管理
5. 客戶行為日誌記錄

## 目錄結構
```
online-shop-project/
├── src/
│   ├── main/
│   │   ├── java/com/onlineshop/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── model/
│   │   │   ├── dto/
│   │   │   ├── security/
│   │   │   └── OnlineShopApplication.java
│   │   ├── resources/
│   │   │   ├── static/
│   │   │   ├── templates/
│   │   │   └── application.properties
│   │   └── webapp/
│   └── test/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## 快速開始
1. 安裝Java 17+, Maven, Docker
2. 配置MySQL數據庫
3. 運行 `mvn spring-boot:run`
4. 訪問 http://localhost:8080

## 數據庫設計
詳細的數據庫設計請參考 `docs/database-schema.sql`
```