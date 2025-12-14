#商家產品圖片上傳功能指南

## 功能概述

商家可以在創建或編輯產品時上傳產品圖片。圖片將存儲在本地服務器的 `src/main/resources/static/images/products` 目錄中,並通過 `/images/products/` URL 路徑訪問。

## 存儲路徑

### 開發環境
- **物理路徑**: `src/main/resources/static/images/products/`
- **訪問 URL**: `http://localhost:8080/images/products/{filename}`

### 生產環境 (Docker)
- **容器內路徑**: `/app/static/images/products/`
- **訪問 URL**: `http://your-domain/images/products/{filename}`

## 功能特性

### 1. 支持的文件格式
- JPG/JPEG
- PNG
- GIF

### 2. 文件限制
- **最大文件大小**: 5MB
- **推薦尺寸**: 800x800像素
- **文件命名**: 自動生成 UUID 唯一文件名

### 3. 驗證規則
- 文件類型驗證(必須是圖片)
- 文件大小驗證(不超過 5MB)
- 文件名安全性驗證

## 使用方法

### 商家端操作步驟

1. **訪問產品管理頁面**
   ```
   登錄 → 商家管理 → 我的商品→ 新增商品 或 編輯商品
   ```

2. **上傳圖片**
   - 在"商品圖片"區塊,點擊"上傳圖片"按鈕
   - 選擇本地圖片文件(JPG, PNG, GIF)
   - 系統會自動驗證文件格式和大小

3. **查看預覽**
   - 編輯現有產品時,會顯示當前圖片
   - 上傳新圖片將替換舊圖片

4. **保存產品**
   - 點擊"創建商品"或"更新商品"按鈕
   - 圖片將自動上傳並保存

## 技術實現

### 1. 前端表單 ([`merchant/product-form.html`](../src/main/resources/templates/merchant/product-form.html))

```html
<form method="post" enctype="multipart/form-data">
    <input type="file" name="imageFile" 
           accept="image/jpeg,image/png,image/jpg,image/gif">
</form>
```

**重要**: 表單必須設置 `enctype="multipart/form-data"` 以支持文件上傳。

### 2. 後端控制器 ([`MerchantProductController.java`](../src/main/java/com/onlineshop/controller/MerchantProductController.java))

#### 創建產品時上傳圖片
```java
@PostMapping
public String createProduct(
        @ModelAttribute Product product,
        @RequestParam(required = false) MultipartFile imageFile,
        ...) {
    if (imageFile != null && !imageFile.isEmpty()) {
        String imageUrl = saveProductImage(imageFile);
        product.setImageUrl(imageUrl);
    }
    //保存產品
}
```

#### 更新產品時上傳圖片
```java
@PostMapping("/{id}")
public String updateProduct(
        @PathVariable Long id,
        @ModelAttribute Product product,
        @RequestParam(required = false) MultipartFile imageFile,
        ...) {
    if (imageFile != null && !imageFile.isEmpty()) {
        String imageUrl = saveProductImage(imageFile);
        product.setImageUrl(imageUrl);
    }
    // 更新產品
}
```

####圖片保存邏輯
```java
private String saveProductImage(MultipartFile file) throws IOException {
    // 1. 驗證文件
    if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
    
    // 2. 驗證文件類型
    String contentType = file.getContentType();
    if (!contentType.startsWith("image/")) {
        throw new IllegalArgumentException("File must be an image");
    }
    
    // 3. 驗證文件大小 (max 5MB)
    if (file.getSize() > 5 * 1024 * 1024) {
        throw new IllegalArgumentException("File size must not exceed 5MB");
    }
    
    // 4. 生成唯一文件名
    String filename = UUID.randomUUID().toString() + extension;
    
    // 5. 保存文件
    Path filePath = Paths.get("src/main/resources/static/images/products", filename);
    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    
    // 6. 返回訪問 URL
    return "/images/products/" + filename;
}
```

### 3. 配置文件 ([`application.properties`](../src/main/resources/application.properties))

```properties
# File Upload Configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB
spring.servlet.multipart.file-size-threshold=2KB
```

## 文件命名規則

系統使用 UUID 自動生成唯一文件名,格式如下:

```
原始文件名: product-image.jpg
生成文件名: 550e8400-e29b-41d4-a716-446655440000.jpg
```

這確保了:
- **唯一性**: 避免文件名衝突
- **安全性**: 防止惡意文件名注入
- **可追溯性**: 每個文件都有唯一標識

## 圖片訪問

### 在模板中顯示圖片

```html
<!-- 產品列表頁面 -->
<img th:src="@{${product.imageUrl}}" 
     alt="Product Image"
     onerror="this.src='/images/placeholder.png'">

<!-- 產品詳情頁面 -->
<img th:src="@{${product.imageUrl}}" 
     class="img-fluid"
     alt="Product Image">
```

### 在前端 JavaScript 中訪問

```javascript
const imageUrl = `/images/products/${filename}`;
const fullUrl = `${window.location.origin}${imageUrl}`;
```

## 錯誤處理

### 1. 文件類型錯誤
```
錯誤信息: File must be an image
解決方法: 確保上傳的是圖片文件(JPG, PNG, GIF)
```

### 2. 文件過大
```
錯誤信息: File size must not exceed 5MB
解決方法: 壓縮圖片或選擇較小的文件
```

### 3. 文件為空
```
錯誤信息: File is empty
解決方法: 確保選擇了有效的文件
```

### 4. 無效文件名
```
錯誤信息: Invalid filename
解決方法: 系統會自動生成文件名,這個錯誤很少發生
```

## 最佳實踐

### 1. 圖片優化建議
- **尺寸**: 推薦 800x800 像素,保持正方形比例
- **格式**: JPG 適合照片,PNG 適合透明背景或圖標
- **壓縮**: 使用工具壓縮圖片以減小文件大小
- **命名**: 原始文件名應簡潔明了(系統會自動重命名)

### 2. 使用流程
```
1. 準備高質量產品圖片
2. 確保圖片符合規格(格式、大小、尺寸)
3. 在產品表單中上傳圖片
4. 系統自動驗證並保存
5. 預覽確認圖片正確
6. 提交表單完成上傳
```

### 3. 圖片管理
- 定期清理未使用的圖片文件
- 更新產品時可以替換圖片
- 刪除產品時,圖片文件仍保留在服務器(可手動清理)

## Docker 部署注意事項

在Docker 環境中,需要確保:

1. **Dockerfile 配置**```dockerfile
   #複製靜態資源
   COPY src/main/resources/static /app/static
   
   # 創建圖片目錄
   RUN mkdir -p /app/static/images/products
   ```

2. **卷映射** (可選)
   ```yaml
   volumes:
     - ./uploads:/app/static/images/products
   ```

3. **權限設置**
   ```bash
   # 確保目錄有寫入權限
   chmod 777 /app/static/images/products
   ```

## 故障排查

### 問題 1: 圖片上傳後無法訪問
**原因**: 靜態資源配置不正確
**解決**:
1. 檢查 [`application.properties`](../src/main/resources/application.properties) 中的靜態資源配置
2. 確認 [`WebMvcConfig.java`](../src/main/java/com/onlineshop/config/WebMvcConfig.java) 配置正確
3. 重啟應用程序

### 問題 2: 文件上傳失敗
**原因**: 文件大小超過限制或格式不支持
**解決**:
1. 檢查文件大小是否超過 5MB
2. 確認文件格式為JPG, PNG, 或 GIF
3. 查看應用程序日誌獲取詳細錯誤信息

### 問題 3: 目錄權限問題
**原因**: 應用程序沒有寫入權限
**解決**:
```bash
# Linux/Mac
chmod 755 src/main/resources/static/images/products

# Docker
docker exec -it container_name chmod 755 /app/static/images/products
```

## 相關文件

- [`MerchantProductController.java`](../src/main/java/com/onlineshop/controller/MerchantProductController.java) - 圖片上傳處理邏輯
- [`merchant/product-form.html`](../src/main/resources/templates/merchant/product-form.html) - 產品表單頁面
- [`application.properties`](../src/main/resources/application.properties) - 文件上傳配置
- [`Product.java`](../src/main/java/com/onlineshop/model/Product.java) - 產品模型(imageUrl 字段)

## 未來改進建議

1. **圖片處理**
   - 自動生成縮略圖
   - 自動調整圖片尺寸
   - 添加水印功能

2. **存儲優化**
   - 集成雲存儲服務(如 AWS S3,阿里雲 OSS)
   - 實現 CDN 加速
   - 圖片懶加載

3. **功能增強**
   - 支持多圖片上傳
   - 圖片裁剪功能
   - 圖片編輯器集成

4. **安全性**
   - 添加防病毒掃描
   -圖片內容審核
   - 更嚴格的文件驗證