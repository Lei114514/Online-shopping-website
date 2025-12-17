# 郵件確認收貨功能指南

## 功能概述

本系統實現了郵件確認收貨功能，當用戶下單後，系統會自動發送一封包含確認收貨連結的郵件。用戶點擊連結確認收貨後，訂單會自動標記為已付款和已送達。

**重要優化**：郵件發送採用異步方式，不會阻塞結賬流程，確保用戶體驗流暢。

## 功能流程

```
用戶下單 → 訂單創建成功 → 顯示確認頁面（含確認連結）
                      ↓
              異步發送確認郵件 → 用戶收到郵件 → 點擊確認連結 → 訂單標記為已付款/已送達
```

## 技術實現

### 1. 數據庫欄位

在 `Order` 模型中添加了以下欄位：

```java
@Column(name = "confirmation_token", unique = true, length = 100)
private String confirmationToken;

@Column(name = "confirmed_at")
private LocalDateTime confirmedAt;
```

- `confirmation_token`: 唯一的確認令牌，用於郵件連結
- `confirmed_at`: 確認收貨的時間戳

### 2. 郵件服務 (EmailService)

位置: `src/main/java/com/onlineshop/service/EmailService.java`

主要功能：
- 發送 HTML 格式的訂單確認郵件
- 郵件包含訂單詳情和確認收貨按鈕
- 支持中文內容

### 3. 確認收貨端點

位置: `src/main/java/com/onlineshop/controller/OrderController.java`

```java
@GetMapping("/confirm/{token}")
public String confirmDelivery(@PathVariable String token, Model model, RedirectAttributes redirectAttributes)
```

- URL: `/orders/confirm/{token}`
- 公開訪問（無需登錄）
- 驗證令牌有效性
- 更新訂單狀態

### 4. 安全配置

在 `SecurityConfig.java` 中添加了公開訪問權限：

```java
.requestMatchers("/orders/confirm/**").permitAll()
```

## 郵件配置

### application.properties 配置

```properties
# 郵件配置
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
# 郵件發送超時配置（毫秒）
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000
# 是否啟用郵件發送
spring.mail.enabled=true

# 應用程序基礎 URL（用於生成確認連結）
app.base-url=http://localhost:8080
```

### Gmail 配置說明

1. 登錄 Google 帳號
2. 前往 [Google 帳號安全設置](https://myaccount.google.com/security)
3. 啟用兩步驗證
4. 生成應用程式密碼：
   - 選擇「應用程式密碼」
   - 選擇「郵件」和「其他」
   - 輸入應用名稱（如：OnlineShop）
   - 複製生成的 16 位密碼
5. 將密碼填入 `spring.mail.password`

### 其他郵件服務配置

#### QQ 郵箱
```properties
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=your-qq@qq.com
spring.mail.password=your-authorization-code
```

#### 163 郵箱
```properties
spring.mail.host=smtp.163.com
spring.mail.port=25
spring.mail.username=your-email@163.com
spring.mail.password=your-authorization-code
```

### Docker 環境配置

在 Docker 環境中，可以通過環境變量配置郵件服務：

#### 方法一：使用 .env 文件

在項目根目錄創建 `.env` 文件：

```bash
MAIL_USERNAME=your-email@163.com
MAIL_PASSWORD=your-authorization-code
```

然後運行：
```bash
docker compose up -d
```

#### 方法二：命令行傳遞環境變量

```bash
MAIL_USERNAME=your-email@163.com MAIL_PASSWORD=your-authorization-code docker compose up -d
```

#### 方法三：使用配置腳本

運行項目提供的配置腳本：

```bash
./configure-email.sh
```

腳本會引導您輸入郵箱地址和授權碼，並自動更新配置。

### 163 郵箱授權碼獲取方法

1. 登錄 163 郵箱網頁版 (https://mail.163.com)
2. 進入「設置」→「POP3/SMTP/IMAP」
3. 開啟「SMTP 服務」
4. 按照提示發送短信驗證
5. 獲取授權碼（16位字符）
6. 將授權碼填入配置

**注意**：授權碼不是郵箱登錄密碼，是專門用於第三方應用的密碼。

## 郵件內容

發送的郵件包含：

1. **訂單基本信息**
   - 訂單編號
   - 訂單日期
   - 總金額

2. **訂單項目列表**
   - 商品名稱
   - 數量
   - 小計

3. **配送信息**
   - 配送地址
   - 訂單狀態
   - 支付狀態

4. **確認收貨按鈕**
   - 醒目的綠色按鈕
   - 備用文字連結

## 確認收貨流程

### 用戶操作

1. 收到訂單確認郵件
2. 收到商品後，點擊郵件中的「確認收貨」按鈕
3. 瀏覽器打開確認頁面
4. 看到確認成功的提示

### 系統處理

1. 驗證確認令牌
2. 檢查訂單是否已確認
3. 更新訂單狀態：
   - `paymentStatus` → `PAID`
   - `status` → `DELIVERED`
   - `confirmedAt` → 當前時間
4. 記錄用戶活動日誌
5. 顯示確認成功頁面

## 錯誤處理

### 無效令牌
- 顯示錯誤提示
- 重定向到首頁

### 已確認訂單
- 顯示「訂單已確認」提示
- 防止重複確認

## 測試方法

### 1. 配置測試郵箱

在 `application.properties` 中配置有效的郵件服務器信息。

### 2. 創建測試訂單

1. 使用測試帳號登錄
2. 添加商品到購物車
3. 完成結賬流程
4. 檢查郵箱收到確認郵件

### 3. 測試確認連結

1. 點擊郵件中的確認按鈕
2. 驗證訂單狀態更新
3. 檢查確認成功頁面

### 4. 手動測試（開發環境）

如果郵件發送失敗，可以從數據庫獲取確認令牌：

```sql
SELECT order_number, confirmation_token FROM orders WHERE id = ?;
```

然後訪問：`http://localhost:8080/orders/confirm/{token}`

## 性能優化

### 異步郵件發送

為了避免郵件發送阻塞結賬流程，系統採用了以下優化措施：

1. **@Async 異步處理**：郵件發送在後台線程執行，不阻塞主請求
2. **超時配置**：設置了 5 秒的連接和發送超時，避免長時間等待
3. **優雅降級**：如果郵件服務不可用，系統會在控制台輸出確認連結，不影響訂單創建
4. **頁面顯示連結**：訂單確認頁面直接顯示確認收貨連結，用戶無需等待郵件

### 郵件服務不可用時的處理

如果郵件服務器未配置或不可用：
- 訂單仍然正常創建
- 確認連結會顯示在訂單確認頁面上
- 確認連結也會輸出到應用程序日誌中
- 用戶可以直接使用頁面上的連結確認收貨

## 注意事項

1. **郵件發送失敗不影響訂單創建**
   - 郵件發送在 try-catch 中執行
   - 失敗時只記錄錯誤日誌
   - 確認連結會顯示在訂單確認頁面

2. **確認令牌安全性**
   - 使用 UUID 生成，難以猜測
   - 每個訂單唯一
   - 只能使用一次

3. **生產環境配置**
   - 使用環境變量存儲郵件密碼
   - 配置正確的服務器 URL（修改 `app.base-url`）
   - 考慮使用專業郵件服務（如 SendGrid、Mailgun）

4. **結賬時間優化**
   - 確認 `@EnableAsync` 註解已添加到主應用程序類
   - 檢查郵件超時配置是否生效
   - 如果不需要郵件功能，可以設置 `spring.mail.enabled=false`

## 相關文件

- `src/main/java/com/onlineshop/model/Order.java` - 訂單模型
- `src/main/java/com/onlineshop/service/EmailService.java` - 郵件服務
- `src/main/java/com/onlineshop/service/OrderService.java` - 訂單服務
- `src/main/java/com/onlineshop/controller/OrderController.java` - 訂單控制器
- `src/main/java/com/onlineshop/security/SecurityConfig.java` - 安全配置
- `src/main/resources/templates/order-delivery-confirmed.html` - 確認成功頁面