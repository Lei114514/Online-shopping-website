# 線上購物系統 - 快速開始指南

## 系統概述

這是一個完整的線上購物網頁系統，基於Spring Boot開發，包含顧客端和銷售端功能。

## 功能特性

### 顧客端功能
✅ 用戶註冊、登錄、註銷  
✅ 商品瀏覽和搜索  
✅ 購物車管理  
✅ 訂單處理和支付  
✅ 訂單狀態查詢  
✅ 郵件確認通知  

### 銷售端功能
✅ 商品目錄管理（增刪改查）  
✅ 訂單管理  
✅ 銷售統計報表  
✅ 客戶管理  
✅ 客戶行為日誌記錄  

## 技術棧

- **後端**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **前端**: HTML5, CSS3, JavaScript, Bootstrap 5, Thymeleaf
- **數據庫**: MySQL 8.0
- **服務器**: Tomcat (內嵌於Spring Boot)
- **容器化**: Docker, Docker Compose
- **構建工具**: Maven

## 快速開始

### 前提條件

1. **Java 17+**
2. **Maven 3.8+**
3. **MySQL 8.0+** (可選，Docker方式不需要)
4. **Docker 20.10+** (可選，用於容器化部署)

### 方法一：使用Docker（推薦）

```bash
# 1. 進入項目目錄
cd online-shop-project

# 2. 運行Docker腳本
./docker-run.sh

# 或者手動運行
docker-compose up -d
```

訪問地址：http://localhost:8080

### 方法二：本地運行

```bash
# 1. 進入項目目錄
cd online-shop-project

# 2. 構建項目
./build.sh

# 3. 運行應用
./run.sh
```

訪問地址：http://localhost:8080

### 方法三：使用Maven直接運行

```bash
# 1. 進入項目目錄
cd online-shop-project

# 2. 運行Spring Boot應用
mvn spring-boot:run
```

## 默認帳號

### 管理員
- **用戶名**: admin
- **密碼**: admin123
- **權限**: 完整系統管理權限

### 銷售人員
- **用戶名**: sales
- **密碼**: sales123
- **權限**: 商品管理、訂單管理、銷售統計

### 顧客
- 需要通過註冊頁面創建新帳號

## 數據庫配置

### 使用Docker Compose
數據庫會自動創建並初始化，包含：
- 示例用戶（admin, sales）
- 示例商品分類
- 示例商品數據

### 手動配置MySQL
1. 創建數據庫：
```sql
CREATE DATABASE online_shop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 運行初始化腳本：
```bash
mysql -u root -p online_shop_db < docs/database-schema.sql
```

3. 修改應用配置：
編輯 `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/online_shop_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## 項目結構

```
online-shop-project/
├── src/main/java/com/onlineshop/  # Java源代碼
├── src/main/resources/           # 資源文件
├── src/test/                     # 測試代碼
├── docs/                         # 文檔
├── uploads/                      # 上傳文件
├── Dockerfile                    # Docker構建
├── docker-compose.yml            # Docker編排
└── pom.xml                       # Maven配置
```

## API端點

### 主要頁面
- `GET /` - 首頁
- `GET /products` - 商品列表
- `GET /cart` - 購物車
- `GET /orders` - 我的訂單
- `GET /login` - 登錄頁
- `GET /register` - 註冊頁

### 銷售管理
- `GET /orders/sales` - 訂單管理
- `GET /orders/sales/statistics` - 銷售統計
- `GET /admin/products` - 商品管理

### 管理員
- `GET /users/admin` - 用戶管理

## 開發指南

### 添加新功能
1. 在 `model/` 目錄創建數據模型
2. 在 `repository/` 目錄創建數據訪問接口
3. 在 `service/` 目錄創建業務邏輯
4. 在 `controller/` 目錄創建控制器
5. 在 `templates/` 目錄創建頁面模板

### 運行測試
```bash
# 運行所有測試
mvn test

# 運行特定測試類
mvn test -Dtest=UserServiceTest

# 生成測試報告
mvn surefire-report:report
```

### 代碼風格
- 使用Lombok減少樣板代碼
- 遵循Spring Boot最佳實踐
- 添加必要的註釋
- 進行單元測試

## 部署到生產環境

### 1. 構建生產版本
```bash
mvn clean package -DskipTests -Pproduction
```

### 2. 配置生產環境
創建 `application-production.properties`:
```properties
spring.profiles.active=production
spring.datasource.url=jdbc:mysql://生產數據庫地址:3306/online_shop_db
spring.datasource.username=生產用戶名
spring.datasource.password=生產密碼
spring.mail.host=smtp生產服務器
# 其他生產配置...
```

### 3. 使用Docker部署
```bash
# 構建生產鏡像
docker build -t online-shop:production .

# 運行容器
docker run -d \
  -p 8080:8080 \
  -v /path/to/uploads:/app/uploads \
  -v /path/to/logs:/app/logs \
  --name online-shop \
  online-shop:production
```

## 故障排除

### 常見問題

1. **應用無法啟動**
   - 檢查端口8080是否被占用
   - 檢查Java版本是否為17+
   - 查看日誌：`tail -f logs/application.log`

2. **數據庫連接失敗**
   - 檢查MySQL服務是否運行
   - 驗證數據庫配置
   - 檢查防火牆設置

3. **文件上傳失敗**
   - 檢查uploads目錄權限
   - 檢查磁盤空間

4. **郵件發送失敗**
   - 檢查郵件服務器配置
   - 檢查網絡連接

### 日誌查看

```bash
# 本地運行
tail -f logs/application.log

# Docker運行
docker-compose logs -f app

# 查看特定服務日誌
docker-compose logs app
docker-compose logs mysql
```

## 擴展功能建議

### 短期改進
1. 添加支付網關集成（Stripe、PayPal）
2. 實現商品評論和評分
3. 添加商品推薦系統
4. 實現優惠券和促銷活動

### 長期規劃
1. 移動應用開發（React Native/Flutter）
2. 微服務架構重構
3. 大數據分析平台
4. AI商品推薦

## 安全建議

1. **生產環境必須啟用HTTPS**
2. **定期更新依賴庫**
3. **實施API速率限制**
4. **配置WAF（Web應用防火牆）**
5. **定期安全審計**
6. **數據備份和恢復計劃**

## 貢獻指南

1. Fork項目倉庫
2. 創建功能分支
3. 提交代碼更改
4. 編寫測試用例
5. 創建Pull Request

## 許可證

MIT License - 詳見LICENSE文件

## 支持與聯繫

如有問題或建議，請：
1. 查看項目文檔
2. 檢查現有Issue
3. 創建新的Issue
4. 聯繫項目維護者

---

**祝您使用愉快！** 🛒

這個線上購物系統為您提供了一個完整的電商解決方案，從商品展示到訂單處理，從客戶管理到銷售分析，一應俱全。系統設計考慮了擴展性和維護性，可以根據業務需求進行定制開發。
