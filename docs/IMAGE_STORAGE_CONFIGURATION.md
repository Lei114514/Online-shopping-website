# 圖片存儲配置說明

## 概述

本系統已配置為使用本地文件系統存儲商品圖片,而非從網絡 URL 獲取。

## 存儲路徑

### 主要存儲位置

**本地存儲根目錄:**
```
src/main/resources/static/images/products/
```

**完整路徑:**
```
/home/lei/project/onlineShopping/src/main/resources/static/images/products/
```

### 訪問路徑

圖片可通過以下 URL 路徑訪問:
- API 路徑: `/api/images/{filename}`
- 靜態資源路徑: `/images/products/{filename}`

## 文件結構

```
src/main/resources/static/images/products/
├── iphone-15-pro.jpg          # 示例商品圖片
├── README.md                  # 圖片目錄說明
└── [其他商品圖片...]
```

## 配置細節

### 1.圖片控制器 (ImageController)

位置: `src/main/java/com/onlineshop/controller/ImageController.java`

功能:
- 處理圖片請求
- 從本地文件系統讀取圖片
- 設置正確的 Content-Type
- 返回圖片字節流

```java
@GetMapping("/{filename:.+}")
public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
    // 從本地讀取圖片文件
    Path imagePath = Paths.get("src/main/resources/static/images/products/" + filename);
    // ...
}
```

### 2. 靜態資源配置 (WebMvcConfig)

位置: `src/main/java/com/onlineshop/config/WebMvcConfig.java`

功能:
- 配置靜態資源處理器
- 映射 `/images/**` 到本地目錄
- 啟用資源緩存

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/images/")
            .setCachePeriod(3600);
}
```

### 3. 安全配置 (SecurityConfig)

位置: `src/main/java/com/onlineshop/security/SecurityConfig.java`

圖片訪問路徑已配置為公開訪問:
```java
.requestMatchers("/api/images/**", "/images/**").permitAll()
```

### 4. 數據初始化 (DataInitializer)

位置: `src/main/java/com/onlineshop/config/DataInitializer.java`

示例商品使用本地圖片路徑:
```java
Product iphone = new Product();
iphone.setImageUrl("/api/images/iphone-15-pro.jpg");
```

## 使用方法

### 添加新商品圖片

1. **將圖片文件放入存儲目錄:**
   ```bash
   cp your-product-image.jpg src/main/resources/static/images/products/
   ```

2. **在數據庫中引用圖片:**
   ```java
   product.setImageUrl("/api/images/your-product-image.jpg");
   ```

3. **在模板中顯示圖片:**
   ```html
   <img th:src="${product.imageUrl}" alt="商品圖片">
   ```

### 支持的圖片格式

- JPEG/JPG (`.jpg`, `.jpeg`)
- PNG (`.png`)
- GIF (`.gif`)
- WebP (`.webp`)
- SVG (`.svg`)
- BMP (`.bmp`)
- ICO (`.ico`)
- TIFF (`.tif`, `.tiff`)
- AVIF (`.avif`)

##訪問示例

### 通過 API 訪問
```
GET http://localhost:8080/api/images/iphone-15-pro.jpg
```

### 通過靜態資源訪問
```
GET http://localhost:8080/images/products/iphone-15-pro.jpg
```

### 在 HTML 模板中使用
```html
<!-- 使用 API 路徑 (推薦) -->
<img th:src="${product.imageUrl}" alt="商品圖片">

<!-- 或直接使用靜態路徑 -->
<img src="/images/products/iphone-15-pro.jpg" alt="商品圖片">
```

## Docker 環境配置

在 Docker 容器中,圖片文件會被包含在應用 JAR 包內:

### Dockerfile 配置
```dockerfile
# 複製資源文件(包括圖片)
COPY src ./src

# 打包時會包含所有資源
RUN mvn clean package -DskipTests
```

### 注意事項
- 圖片文件會被打包到 JAR 文件中
- 容器啟動後,圖片從JAR 內的資源目錄提供
- 大型圖片庫建議使用外部存儲卷掛載

## 性能優化建議

### 1. 圖片壓縮
建議在上傳前壓縮圖片:
- JPEG: 85% 質量
- PNG: 使用工具如 pngquant
- 推薦尺寸: 最大 1920x1080

### 2. 使用 CDN (生產環境)
生產環境建議:
- 將圖片上傳到 CDN
- 更新`imageUrl` 為 CDN 地址
- 保留本地備份

### 3. 緩存配置
已在 `WebMvcConfig` 中設置緩存:
```java
.setCachePeriod(3600)  // 緩存 1 小時
```

## 故障排查

### 圖片無法顯示

1. **檢查文件是否存在:**
   ```bash
   ls -la src/main/resources/static/images/products/
   ```

2. **檢查文件權限:**
   ```bash
   chmod 644 src/main/resources/static/images/products/*.jpg
   ```

3. **檢查路徑配置:**
   - 確認 `imageUrl` 格式正確
   - 檢查 `WebMvcConfig` 資源映射
   - 驗證 `SecurityConfig` 權限設置

4. **查看應用日誌:**
   ```bash
   docker logs onlineshopping-app
   ```

### 404 錯誤

如果訪問圖片返回 404:
- 檢查圖片文件名是否正確
- 確認路徑大小寫匹配
- 驗證文件確實存在於指定目錄

### 權限錯誤

如果返回 403:
- 檢查 `SecurityConfig` 中的權限配置
- 確認路徑已添加到 `permitAll()` 列表

## 遷移現有數據

如果需要從網絡 URL 遷移到本地存儲:

```sql
-- 下載圖片後更新數據庫
UPDATE products
SET image_url = '/api/images/product-' || id || '.jpg' 
WHERE image_url LIKE 'http%';
```

## 相關文檔

- [圖片存儲指南](IMAGE_STORAGE_GUIDE.md)
- [本地資源配置](LOCAL_RESOURCES.md)
- [資源加載修復](RESOURCE_LOADING_FIX.md)

## 總結

本系統已完整配置為使用本地文件系統存儲商品圖片:

✅ 存儲路徑: `src/main/resources/static/images/products/`  
✅ API訪問: `/api/images/{filename}`  
✅ 靜態訪問: `/images/products/{filename}`  
✅ 安全配置: 公開訪問  
✅ Docker 支持: JAR 包內資源  

所有圖片均從本地存儲讀取,不再依賴外部網絡 URL。