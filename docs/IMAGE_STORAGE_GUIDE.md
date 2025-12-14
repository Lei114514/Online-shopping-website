#圖片存儲配置指南

## 概述

本系統使用本地文件系統存儲商品圖片,通過專用的API端點提供訪問。

## 存儲路徑

### 物理存儲位置
```
src/main/resources/static/images/products/
```

這個目錄包含所有商品圖片文件。例如:
- `iphone-15-pro.jpg`
- `macbook-pro.jpg`
- 等等...

### 訪問路徑

圖片通過以下API端點訪問:
```
http://localhost:8080/api/images/{filename}
```

例如:
```
http://localhost:8080/api/images/iphone-15-pro.jpg
```

## 技術實現

### 1. ImageController

位置: `src/main/java/com/onlineshop/controller/ImageController.java`

提供 `/api/images/{filename}` 端點來讀取並返回圖片文件。

關鍵功能:
- 從classpath 讀取圖片資源
- 設置正確的 Content-Type (image/jpeg, image/png 等)
- 設置緩存控制頭 (Cache-Control: max-age=86400)
- 404錯誤處理

### 2. 數據庫存儲

在 `Product` 實體中,`imageUrl` 字段只存儲**文件名**,不包含完整路徑:

```java
product.setImageUrl("iphone-15-pro.jpg");// ✓ 正確
product.setImageUrl("/images/products/iphone-15-pro.jpg");  // ✗ 錯誤
product.setImageUrl("http://...jpg");  // ✗ 錯誤
```

### 3. 模板使用

在 Thymeleaf 模板中使用圖片:

```html
<img th:if="${product.imageUrl}"
     th:src="@{'/api/images/' + ${product.imageUrl}}"
     th:alt="${product.name}">
```

已修改的模板文件:
- `src/main/resources/templates/home.html`
- `src/main/resources/templates/products.html`
- `src/main/resources/templates/product-detail.html`

## 添加新圖片

### 步驟 1: 準備圖片文件

1. 將圖片文件放入 `src/main/resources/static/images/products/` 目錄
2. 建議的文件命名格式: `product-name.jpg`
3. 支持的格式: JPG, PNG, GIF, WebP

### 步驟 2: 在數據庫中引用

在 `DataInitializer.java` 或通過管理界面添加商品時:

```java
Product product = new Product();
product.setName("iPhone 15 Pro");
product.setImageUrl("iphone-15-pro.jpg");  // 只使用文件名
// ... 其他屬性
```

## 安全配置

在 `SecurityConfig.java` 中,`/api/images/**` 端點已配置為公開訪問:

```java
.requestMatchers("/api/images/**").permitAll()
```

## 性能優化

###緩存設置

圖片響應包含以下緩存頭:
```
Cache-Control: max-age=86400
```

這意味著瀏覽器會緩存圖片 24 小時。

### 建議

1. **圖片優化**: 使用適當的圖片尺寸和壓縮- 列表頁縮略圖: 300x300px
   -詳情頁大圖: 800x800px
   - 推薦格式: WebP (更小的文件大小)

2. **文件命名**: 使用有意義的文件名
   - ✓ `iphone-15-pro.jpg`
   - ✗ `img001.jpg`

3. **備份**: 定期備份 `images/products/` 目錄

## 故障排除

### 圖片無法顯示

1. **檢查文件是否存在**
   ```bash
   ls -la src/main/resources/static/images/products/
   ```

2. **檢查文件名是否正確**
   - 數據庫中的 `imageUrl` 值必須與實際文件名完全匹配
   - 注意大小寫敏感

3. **檢查控制台日誌**
   - 查看是否有 404 或文件讀取錯誤

4. **清除瀏覽器緩存**
   -如果最近更換了圖片,可能需要清除緩存

### 開發環境 vs 生產環境

- **開發環境**: 圖片在`src/main/resources/static/images/products/`
- **打包後**: 圖片會被包含在 JAR 文件的 classpath 中
- **Docker**: 確保 Dockerfile 正確複製了資源文件

## 未來改進建議

1. **CDN集成**: 對於生產環境,考慮使用CDN服務
2. **圖片上傳功能**: 添加管理界面的圖片上傳功能
3. **多尺寸支持**: 自動生成不同尺寸的縮略圖
4. **雲存儲**: 考慮使用 AWS S3 或阿里雲 OSS 等雲存儲服務

## 相關文件

- ImageController: `src/main/java/com/onlineshop/controller/ImageController.java`
- 圖片目錄: `src/main/resources/static/images/products/`
- 數據初始化: `src/main/java/com/onlineshop/config/DataInitializer.java`
- 安全配置: `src/main/java/com/onlineshop/security/SecurityConfig.java`