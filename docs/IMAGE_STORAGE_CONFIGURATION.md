# 圖片存儲配置完整說明

## 概述

本系統已配置為使用**本地文件系統存儲**來管理商品圖片，而不是使用外部URL。所有圖片數據都存儲在項目目錄中，並通過Docker卷映射實現持久化。

## 存儲路徑結構

### 1. 本地開發環境路徑

```
項目根目錄/
└── src/
    └── main/
        └── resources/
            └── static/
                └── images/
                    └── products/     ← 商品圖片存儲目錄
                        ├── iphone-15-pro.jpg
                        ├── product-123.jpg
                        └── ...
```

**絕對路徑**: `/home/lei/project/onlineShopping/src/main/resources/static/images/products/`

### 2. Docker容器內部路徑

```
/app/
└── uploads/
    └── products/     ← 容器內圖片存儲目錄
        ├── iphone-15-pro.jpg
        ├── product-123.jpg
        └── ...
```

**容器內路徑**: `/app/uploads/products/`

### 3. 路徑映射關係

通過 `docker-compose.yml` 配置的卷映射：

```yaml
volumes:
  - ./src/main/resources/static/images/products:/app/uploads/products
```

這意味著：
- 本地的 `./src/main/resources/static/images/products` 
- 映射到容器的 `/app/uploads/products`
- 任何一方的修改都會同步到另一方

## 配置文件說明

### application.properties

```properties
# 圖片上傳配置
file.upload-dir=/app/uploads/products
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**關鍵配置**:
- `file.upload-dir`: 指定上傳文件的存儲目錄（容器內路徑）
- `max-file-size`: 單個文件最大 10MB
- `max-request-size`: 整個請求最大 10MB

## 數據庫存儲方式

### Product實體中的 imageUrl 欄位

```java
@Column(name = "image_url", length = 500)
private String imageUrl;
```

**存儲內容**: 只存儲**文件名**，不存儲完整路徑

**示例**:
-✅ 正確: `iphone-15-pro.jpg`
- ❌ 錯誤: `/images/products/iphone-15-pro.jpg`
- ❌ 錯誤: `http://example.com/images/iphone-15-pro.jpg`

### 為什麼只存儲文件名？

1. **靈活性**: 如果更改存儲路徑或域名，無需更新數據庫
2. **簡潔性**: 減少數據冗餘
3. **安全性**: 不暴露實際文件系統路徑

## 圖片訪問流程

### 1. 圖片上傳流程

```
用戶上傳圖片↓
MerchantProductController.saveProduct()
    ↓
處理 MultipartFile
    ↓
生成唯一文件名（時間戳 + 原文件名）
    ↓
保存到 /app/uploads/products/
    ↓
數據庫只存儲文件名
```

### 2. 圖片顯示流程

```
前端模板請求圖片
    ↓
使用路徑: /images/products/{filename}
    ↓
Spring Static Resources 處理
    ↓
從 /app/uploads/products/{filename} 讀取
    ↓
返回圖片給瀏覽器
```

### 3. 實際代碼示例

**上傳代碼** (`MerchantProductController.java`):
```java
@PostMapping("/save")
public String saveProduct(@ModelAttribute Product product,
                         @RequestParam("imageFile") MultipartFile imageFile) {
    if (!imageFile.isEmpty()) {
        String uploadDir = "/app/uploads/products";
        String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);
        Files.write(filepath, imageFile.getBytes());
        
        // 只存儲文件名
        product.setImageUrl(filename);
    }
    productService.saveProduct(product);
}
```

**模板顯示代碼** (`product-detail.html`):
```html
<img th:src="@{/images/products/{filename}(filename=${product.imageUrl})}" 
     class="img-fluid" 
     th:alt="${product.name}">
```

**生成的HTML**:
```html
<img src="/images/products/1702560000000_iphone-15-pro.jpg" 
     class="img-fluid" 
     alt="iPhone 15 Pro">
```

## 靜態資源配置

### Spring Boot 默認靜態資源路徑

Spring Boot 自動映射以下路徑為靜態資源：
- `/static/`
- `/public/`
- `/resources/`
- `/META-INF/resources/`

### 當前配置

由於我們使用了卷映射，實際的靜態資源訪問路徑為：

**URL路徑**: `/images/products/filename.jpg`  
**物理路徑**: `/app/uploads/products/filename.jpg`

這通過 Docker 卷映射實現，無需額外的資源處理器配置。

## 重要注意事項

### ⚠️ 已移除的配置

以下配置已被**刪除**，因為它們與Spring Boot 的默認靜態資源處理衝突：

```java
//❌ 已刪除 - 不要使用
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/products/**")
                .addResourceLocations("file:/app/uploads/products/");
    }
}
```

### ✅ 正確做法

直接使用 Spring Boot 的默認靜態資源處理：
- 將文件存儲在 `/app/uploads/products/`
- 通過 Docker 卷映射到本地
- 使用 `/images/products/**` 路徑訪問

## 默認商品數據

### 已完全移除

系統不再創建任何默認/測試商品數據：

1. ✅ `DataInitializer.java` - 已移除所有商品創建代碼
2. ✅ `HomeController.java` - 已移除 `createSampleProducts()` 方法
3. ✅ 數據庫 - 已清空所有舊的測試商品

### 初始數據

系統啟動時只會創建：
- **5個分類**: 手機、電腦、穿戴裝置、耳機、配件
- **3個用戶**:
  - 管理員: admin / admin123
  - 銷售員: sales / sales123
  - 客戶: customer / customer123

### 商品管理

所有商品必須通過以下方式手動添加：
-銷售員登入後台
- 訪問 `/merchant/products`
- 點擊「新增商品」
- 上傳圖片並填寫商品信息

## 故障排除

### 問題1: 圖片無法顯示

**可能原因**:
1. 文件不存在於 `/app/uploads/products/`
2. 文件權限問題
3. 文件名在數據庫中記錄錯誤

**檢查步驟**:
```bash
# 查看容器內文件
docker exec online-shop-app ls -la /app/uploads/products/

# 查看本地文件
ls -la src/main/resources/static/images/products/

# 檢查數據庫
docker exec online-shop-mysql mysql -uroot -ppassword -e \
  "USE online_shop_db; SELECT id, name, image_url FROM products;"
```

### 問題2: 上傳失敗

**可能原因**:
1. 目錄不存在
2. 權限不足
3. 文件大小超過限制

**解決方法**:
```bash
# 確保目錄存在
mkdir -p src/main/resources/static/images/products

# 檢查容器日誌
docker logs online-shop-app

# 重啟容器
docker compose restart
```

### 問題3: 舊圖片仍然顯示

**原因**:瀏覽器緩存

**解決方法**:
1. 硬刷新頁面 (Ctrl+F5 或 Cmd+Shift+R)
2. 清除瀏覽器緩存
3. 使用隱私模式測試

## 系統架構圖

```
┌─────────────────────────────────────────────────────────┐
│                      用戶瀏覽器                          │
│                                                          │
│  請求: GET /images/products/iphone-15-pro.jpg           │
└────────────────────────┬────────────────────────────────┘
                         │
                         ↓
┌─────────────────────────────────────────────────────────┐
│                  Docker Container│
│                  (online-shop-app)                       │
│                                                          │
│  ┌────────────────────────────────────────┐            │
│  │  Spring Boot Application                │            │
│  │                         │            │
│  │  靜態資源處理                            │            │
│  │  /images/products/** → static目錄      │            │
│  └────────────────┬───────────────────────┘            │
│                   │                                │
│                   ↓                                      │
│┌────────────────────────────────────────┐            │
│  │  文件系統: /app/uploads/products/       │            │
│  │  ├── iphone-15-pro.jpg                  │            │
│  │  └── product-123.jpg                    │            │
│  └────────────────┬───────────────────────┘            │
│                   │                                      │
│                   │ Docker Volume 映射                   │
└───────────────────┼──────────────────────────────────────┘
                    │
                    ↓
┌─────────────────────────────────────────────────────────┐
│              本地文件系統                                 │
│                                                          │
│  /home/lei/project/onlineShopping/                │
│    src/main/resources/static/images/products/           │
│      ├── iphone-15-pro.jpg                              │
│      └── product-123.jpg                                │
└─────────────────────────────────────────────────────────┘
```

## 最佳實踐

### 1. 文件命名

使用時間戳前綴避免文件名衝突：
```java
String filename = System.currentTimeMillis() + "_" + originalFilename;
// 結果: 1702560000000_iphone-15-pro.jpg
```

### 2. 文件驗證

上傳前檢查文件類型和大小：
```java
if (!imageFile.getContentType().startsWith("image/")) {
    throw new IllegalArgumentException("只允許上傳圖片文件");
}
```

### 3. 錯誤處理

提供友好的錯誤消息：
```java
try {
    Files.write(filepath, imageFile.getBytes());
} catch (IOException e) {
    logger.error("圖片上傳失敗", e);
    return "redirect:/merchant/products?error=upload";
}
```

### 4. 圖片優化

建議在上傳前或上傳後進行圖片優化：
- 調整大小（建議 800x800 或 1200x1200）
-壓縮質量（JPEG 質量 80-85%）
- 使用適當格式（JPEG用於照片，PNG用於圖標）

## 總結

### 核心要點

1. ✅ **圖片存儲在本地文件系統**，不使用外部URL
2. ✅ **數據庫只存儲文件名**，提高靈活性
3. ✅ **Docker卷映射**實現本地與容器同步
4. ✅ **Spring Boot靜態資源**自動處理圖片訪問
5. ✅ **無默認商品數據**，所有商品需手動添加

### 快速參考

| 項目 | 值 |
|------|-----|
| 本地存儲路徑 | `src/main/resources/static/images/products/` |
|容器存儲路徑 | `/app/uploads/products/` |
| 訪問URL格式 | `/images/products/{filename}` |
| 數據庫存儲 | 只存文件名，如 `iphone-15-pro.jpg` |
| 上傳限制 | 10MB |
| 支持格式 | JPG, JPEG, PNG, GIF, WebP |

---

**最後更新**: 2025-12-14
**系統版本**: 1.0.0