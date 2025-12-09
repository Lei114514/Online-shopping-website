# 線上購物系統 - 項目結構

## 目錄結構

```
online-shop-project/
├── src/main/java/com/onlineshop/
│   ├── config/                    # 配置類
│   │   └── (待創建)
│   ├── controller/                # 控制器層
│   │   ├── HomeController.java    # 主控制器
│   │   ├── UserController.java    # 用戶控制器
│   │   ├── CartController.java    # 購物車控制器
│   │   └── OrderController.java   # 訂單控制器
│   ├── service/                   # 服務層
│   │   ├── UserService.java       # 用戶服務
│   │   ├── ProductService.java    # 商品服務
│   │   ├── CartService.java       # 購物車服務
│   │   └── OrderService.java      # 訂單服務
│   ├── repository/                # 數據訪問層
│   │   ├── UserRepository.java
│   │   ├── ProductRepository.java
│   │   ├── CategoryRepository.java
│   │   ├── CartItemRepository.java
│   │   ├── OrderRepository.java
│   │   └── UserActivityLogRepository.java
│   ├── model/                     # 數據模型
│   │   ├── User.java
│   │   ├── Category.java
│   │   ├── Product.java
│   │   ├── CartItem.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── UserActivityLog.java
│   ├── dto/                       # 數據傳輸對象
│   │   └── (待創建)
│   ├── security/                  # 安全配置
│   │   ├── SecurityConfig.java
│   │   └── CustomUserDetailsService.java
│   └── OnlineShopApplication.java # 應用程序主類
│
├── src/main/resources/
│   ├── static/                    # 靜態資源
│   │   ├── css/
│   │   │   └── style.css
│   │   ├── js/
│   │   │   └── main.js
│   │   └── images/               # 圖片資源
│   ├── templates/                 # Thymeleaf模板
│   │   ├── layout.html           # 基礎佈局
│   │   ├── home.html             # 首頁
│   │   ├── login.html            # 登錄頁
│   │   ├── register.html         # 註冊頁
│   │   ├── products.html         # 商品列表
│   │   └── cart.html             # 購物車頁
│   └── application.properties     # 應用程序配置
│
├── docs/                          # 文檔
│   └── database-schema.sql        # 數據庫設計
│
├── uploads/                       # 文件上傳目錄
│   └── products/                  # 商品圖片
│
├── Dockerfile                     # Docker構建文件
├── docker-compose.yml             # Docker編排配置
├── pom.xml                        # Maven配置
├── README.md                      # 項目說明
├── PROJECT_STRUCTURE.md           # 項目結構文檔
├── build.sh                       # 構建腳本
├── run.sh                         # 運行腳本
└── docker-run.sh                  # Docker運行腳本
```

## 數據庫設計

### 主要數據表
1. **users** - 用戶表
2. **categories** - 商品分類表
3. **products** - 商品表
4. **cart_items** - 購物車項目表
5. **orders** - 訂單表
6. **order_items** - 訂單項目表
7. **user_activity_logs** - 用戶活動日誌表

### 視圖
1. **sales_statistics** - 銷售統計視圖
2. **low_stock_products** - 低庫存商品視圖

## 功能模塊

### 顧客端功能
1. **用戶管理**
   - 註冊、登錄、註銷
   - 個人資料管理
   - 密碼更改

2. **商品瀏覽**
   - 商品列表展示
   - 商品搜索和篩選
   - 商品詳情查看

3. **購物車管理**
   - 添加商品到購物車
   - 修改商品數量
   - 移除商品
   - 清空購物車

4. **訂單處理**
   - 結賬流程
   - 訂單創建
   - 訂單狀態查詢
   - 訂單取消

5. **郵件通知**
   - 訂單確認郵件
   - 發貨通知

### 銷售端功能
1. **商品管理**
   - 商品增刪改查
   - 庫存管理
   - 商品分類管理

2. **訂單管理**
   - 訂單狀態更新
   - 訂單查詢
   - 訂單處理

3. **銷售統計**
   - 銷售數據分析
   - 熱銷商品統計
   - 銷售報表生成

4. **客戶管理**
   - 客戶信息查看
   - 客戶活動日誌
   - 購買歷史記錄

## 技術棧

### 後端
- **Spring Boot 3.2.0** - 後端框架
- **Spring Security** - 安全認證
- **Spring Data JPA** - 數據訪問
- **Spring Mail** - 郵件發送
- **MySQL 8.0** - 數據庫
- **Maven** - 構建工具

### 前端
- **HTML5** - 頁面結構
- **CSS3** - 樣式設計
- **JavaScript** - 交互邏輯
- **Bootstrap 5** - UI框架
- **Thymeleaf** - 模板引擎
- **Font Awesome** - 圖標庫

### 部署
- **Docker** - 容器化
- **Docker Compose** - 服務編排
- **Tomcat** - Web服務器（內嵌）

## 安全特性

1. **密碼加密** - 使用BCrypt加密存儲
2. **角色權限** - 基於角色的訪問控制
3. **CSRF防護** - 跨站請求偽造防護
4. **會話管理** - 安全的會話處理
5. **輸入驗證** - 服務端和客戶端驗證

## 部署方式

### 本地開發
```bash
# 1. 安裝依賴
./build.sh

# 2. 運行應用
./run.sh
```

### Docker部署
```bash
# 1. 使用Docker Compose
./docker-run.sh

# 2. 或手動運行
docker-compose up -d
```

### 生產環境建議
1. 使用HTTPS
2. 配置數據庫備份
3. 設置監控和日誌
4. 使用負載均衡
5. 定期安全更新

## API端點

### 公開API
- `GET /` - 首頁
- `GET /products` - 商品列表
- `GET /products/{id}` - 商品詳情
- `GET /login` - 登錄頁
- `GET /register` - 註冊頁

### 顧客API（需要認證）
- `GET /cart` - 購物車
- `POST /cart/add` - 添加商品
- `POST /cart/update` - 更新數量
- `POST /cart/remove` - 移除商品
- `GET /orders` - 我的訂單
- `POST /orders/place-order` - 下單

### 銷售API（需要SALES角色）
- `GET /sales/orders` - 訂單管理
- `GET /sales/statistics` - 銷售統計
- `GET /admin/products` - 商品管理

### 管理員API（需要ADMIN角色）
- `GET /admin/users` - 用戶管理
- `POST /admin/users/{id}/toggle-status` - 啟用/禁用用戶

## 開發指南

### 代碼規範
1. 使用有意義的變量名和方法名
2. 添加必要的註釋
3. 遵循Java命名規範
4. 使用Lombok減少樣板代碼
5. 進行單元測試

### 數據庫操作
1. 使用Repository進行數據訪問
2. 添加事務管理
3. 處理異常情況
4. 使用樂觀鎖處理併發

### 前端開發
1. 響應式設計
2. 移動端優先
3. 無障礙訪問
4. 性能優化

## 故障排除

### 常見問題
1. **數據庫連接失敗** - 檢查MySQL服務和配置
2. **應用無法啟動** - 檢查端口占用和日誌
3. **文件上傳失敗** - 檢查目錄權限
4. **郵件發送失敗** - 檢查郵件配置

### 日誌查看
```bash
# 查看應用日誌
tail -f logs/application.log

# Docker日誌
docker-compose logs -f app
```

## 貢獻指南

1. Fork項目
2. 創建功能分支
3. 提交更改
4. 創建Pull Request
5. 等待代碼審查

## 許可證

MIT License
