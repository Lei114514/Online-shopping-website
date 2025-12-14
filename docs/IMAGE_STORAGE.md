# 圖片存儲說明文檔

## 概述

本系統使用本地文件系統存儲產品圖片，而不是使用外部 URL。圖片存儲在項目的靜態資源目錄中。

## 圖片存儲路徑結構

### 物理存儲路徑

```
項目根目錄/
└── src/
    └── main/
        └── resources/
            └── static/
                └── images/
                    └── products/
                        ├── iphone-15-pro.jpg
                        ├── macbook-pro.jpg
                        ├── airpods-pro.jpg
                        ├── ipad-air.jpg
                        ├── apple-watch.jpg
                        └── magic-keyboard.jpg
```

**完整路徑**: `src/main/resources/static/images/products/`

### 數據庫存儲格式

在數據庫中，`Product` 表的 `image_url` 欄位只存儲檔案名稱，不包含路徑：

```
例如: "iphone-15-pro.jpg"
而不是: "/images/products/iphone-15-pro.jpg"
```

### 訪問 URL

前端通過以下 URL 格式訪問圖片：

```
http://localhost:8080/images/products/{filename}
```

例如:
- `http://localhost:8080/images/products/iphone-15-pro.jpg`
- `http://localhost:8080/images/products/macbook-pro.jpg`

## 工作原理

### 1. 圖片控制器 (ImageController)

[`ImageController`](../src/main/java/com/onlineshop/controller/ImageController.java) 負責處理圖片請求：

```java
@Controller
@RequestMapping("/images/products")
public class ImageController {
    // 從 classpath 讀取圖片並返回
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String filename)
}
```

### 2. 數據初始化 (DataInitializer)

[`DataInitializer`](../src/main/java/com/onlineshop/config/DataInitializer.java) 在創建產品時只設置檔案名稱：

```java
product.setImageUrl("iphone-15-pro.jpg");  // 只存儲檔案名
```

### 3. 前端模板

模板使用 Thymeleaf 構建完整的圖片 URL：

```html
<img th:src="@{'/images/products/' + ${product.imageUrl}}" 
     th:alt="${product.name}">
```

## 支持的圖片格式

系統支持以下圖片格式：

- **JPEG/JPG** (image/jpeg)
- **PNG** (image/png)
- **GIF** (image/gif)
- **WebP** (image/webp)
- **SVG** (image/svg+xml)

## 添加新圖片

### 步驟 1: 準備圖片檔案

1. 確保圖片格式為支持的格式之一
2. 使用有意義的檔案名稱（例如：`product-name.jpg`）
3. 建議圖片尺寸：
   - 列表頁面：至少 400x400 像素
   - 詳情頁面：至少 800x800 像素

### 步驟 2: 放置圖片檔案

將圖片檔案複製到以下目錄：

```
src/main/resources/static/images/products/
```

### 步驟 3: 更新產品數據

在創建或更新產品時，設置 `imageUrl` 欄位為檔案名稱：

```java
product.setImageUrl("your-image-filename.jpg");
```

## 範例

### 完整範例：添加新產品圖片

1. **準備圖片**：
   - 檔案名稱：`samsung-galaxy-s24.jpg`
   - 尺寸：1000x1000 像素
   - 格式：JPEG

2. **放置檔案**：
   ```
   複製到: src/main/resources/static/images/products/samsung-galaxy-s24.jpg
   ```

3. **創建產品**：
   ```java
   Product product = new Product();
   product.setName("Samsung Galaxy S24");
   product.setImageUrl("samsung-galaxy-s24.jpg");
   // ... 其他屬性
   productRepository.save(product);
   ```

4. **訪問圖片**：
   ```
   URL: http://localhost:8080/images/products/samsung-galaxy-s24.jpg
   ```

## 注意事項

1. **檔案名稱唯一性**：確保每個圖片檔案名稱是唯一的，避免覆蓋現有圖片

2. **檔案大小**：建議單個圖片檔案不超過 2MB，以確保快速加載

3. **圖片優化**：上傳前建議壓縮圖片以提高網站性能

4. **備份**：定期備份 `static/images/products/` 目錄

5. **權限**：確保應用程序有讀取圖片目錄的權限

## 故障排除

### 圖片無法顯示

1. **檢查檔案是否存在**：
   ```bash
   ls -la src/main/resources/static/images/products/
   ```

2. **檢查檔案名稱**：
   - 確保數據庫中的 `image_url` 與實際檔案名稱完全匹配
   - 注意大小寫敏感

3. **檢查檔案格式**：
   - 確保檔案副檔名正確
   - 確保檔案未損壞

4. **查看應用日誌**：
   - 檢查是否有檔案讀取錯誤
   - 檢查 ImageController 的日誌輸出

### 圖片顯示但樣式不正確

檢查 CSS 樣式設置：
```html
<img style="height: 250px; object-fit: cover;">
```

## 未來改進建議

1. **圖片上傳功能**：實現管理後台的圖片上傳介面
2. **圖片縮放**：自動生成不同尺寸的縮圖
3. **CDN 整合**：考慮使用 CDN 加速圖片加載
4. **圖片壓縮**：自動壓縮上傳的圖片
5. **圖片驗證**：驗證上傳圖片的格式和大小

## 相關檔案

- 圖片控制器：[`src/main/java/com/onlineshop/controller/ImageController.java`](../src/main/java/com/onlineshop/controller/ImageController.java)
- 數據初始化：[`src/main/java/com/onlineshop/config/DataInitializer.java`](../src/main/java/com/onlineshop/config/DataInitializer.java)
- 產品模型：[`src/main/java/com/onlineshop/model/Product.java`](../src/main/java/com/onlineshop/model/Product.java)
- 圖片目錄：`src/main/resources/static/images/products/`