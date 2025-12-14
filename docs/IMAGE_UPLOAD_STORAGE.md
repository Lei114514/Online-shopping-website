# 商品圖片上傳存儲配置指南

## 功能概述

本系統支持商家在創建或編輯商品時上傳商品圖片。圖片將被存儲在服務器的本地靜態資源目錄中,並通過 Spring Boot 的靜態資源服務提供訪問。

## 圖片存儲路徑說明

### 實際存儲位置

**開發環境和生產環境統一使用:**
```
src/main/resources/static/images/products/
```

這是項目源代碼中的靜態資源目錄,圖片會直接存儲在這裡。

### 訪問 URL

上傳後的圖片通過以下 URL 訪問:
```
http://localhost:8080/images/products/{filename}
```

例如:
```
http://localhost:8080/images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
```

### 配置說明

在 [`application.properties`](../src/main/resources/application.properties) 中配置:

```properties
# 圖片上傳路徑配置
upload.path=src/main/resources/static/images/products
```

**重要提示:**
- 路徑是相對於項目根目錄的相對路徑
- 系統會自動創建目錄(如果不存在)
- 圖片直接存儲在源代碼目錄中,便於開發和部署

## 技術實現細節

### 文件存儲邏輯

#### 1. 路徑配置

```java
@Value("${upload.path:src/main/resources/static/images/products}")
private String uploadPath;
```

使用 `@Value` 注入配置的上傳路徑,支持通過配置文件或環境變量指定。

#### 2. 存儲流程

```java
private String saveProductImage(MultipartFile file) throws IOException {
    // 1. 驗證文件(類型、大小、格式)
    
    // 2. 生成唯一文件名
    String filename = UUID.randomUUID().toString() + extension;
    
    // 3. 確定上傳目錄(使用絕對路徑)
    Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
    
    // 4. 創建目錄(如果不存在)
    if (!Files.exists(uploadDir)) {
        Files.createDirectories(uploadDir);
    }
    
    // 5. 保存文件
    Path filePath = uploadDir.resolve(filename);
    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    
    // 6. 返回 URL 路徑
    return "/images/products/" + filename;
}
```

#### 3. 文件驗證

系統會自動驗證上傳的文件:

- **文件類型檢查**: 必須是圖片格式 (`image/*`)
- **文件大小限制**: 最大 5MB
- **文件名生成**: 使用 UUID 確保唯一性和安全性

### URL訪問

上傳後的圖片通過 Spring Boot 的靜態資源服務自動提供訪問:
```
/images/products/{uuid-filename}.jpg
```

Spring Boot 會自動將 `/images/**` 映射到 `classpath:/static/images/`。

## 使用說明

### 商家上傳圖片

1. 登錄商家帳戶
2. 進入商品管理頁面: `/merchant/products`
3. 點擊"新增商品"或編輯現有商品
4. 在商品表單中選擇圖片文件:
   - 支持格式: JPG, PNG, GIF
   - 最大大小: 5MB
   - 系統會自動生成唯一文件名
5. 填寫其他商品信息並提交
6. 圖片將自動上傳並存儲到 `src/main/resources/static/images/products/`

### 圖片預覽

- 上傳後,系統會在商品列表中顯示圖片
- 編輯商品時可以看到當前圖片預覽
- 可以重新上傳替換現有圖片

### 查看已上傳的圖片

```bash
# 列出所有已上傳的圖片
ls -la src/main/resources/static/images/products/

# 查看圖片數量
ls src/main/resources/static/images/products/*.jpg | wc -l
```

## 文件上傳配置

在 [`application.properties`](../src/main/resources/application.properties) 中的相關配置:

```properties
# 啟用文件上傳
spring.servlet.multipart.enabled=true

# 單個文件最大大小
spring.servlet.multipart.max-file-size=5MB

# 整個請求最大大小
spring.servlet.multipart.max-request-size=5MB

# 文件寫入磁盤的閾值
spring.servlet.multipart.file-size-threshold=2KB

# 圖片上傳路徑
upload.path=src/main/resources/static/images/products
```

## 部署說明

### 開發環境

圖片直接存儲在源代碼目錄中:
```
src/main/resources/static/images/products/
```

優點:
- 簡單直接,無需額外配置
- 圖片隨代碼一起管理
- 便於開發和測試

### 生產環境 (Docker)

#### 方式一: 使用源代碼目錄 (當前實現)

保持默認配置,圖片存儲在容器內的源代碼目錄:

```yaml
# docker-compose.yml
services:
  app:
    build: .
    ports:
      - "8080:8080"
```

**注意**: 容器重啟後上傳的圖片會丟失,建議使用 Volume 掛載。

#### 方式二: 使用 Volume 掛載 (推薦)

修改 [`docker-compose.yml`](../docker-compose.yml):

```yaml
services:
  app:
    volumes:
      # 掛載圖片目錄到宿主機
      - ./product-images:/app/src/main/resources/static/images/products
    environment:
      # 可選:覆蓋上傳路徑
      - UPLOAD_PATH=/app/src/main/resources/static/images/products
```

創建外部目錄:
```bash
mkdir -p ./product-images
chmod 755 ./product-images
```

#### 方式三: 使用外部存儲目錄

修改配置使用容器外的目錄:

```yaml
services:
  app:
    volumes:
      - ./uploads:/uploads
    environment:
      - UPLOAD_PATH=/uploads/images/products
```

同時需要配置 Spring Boot 提供靜態資源訪問。

### 文件權限設置

確保應用程序有權限寫入圖片目錄:

```bash
# 開發環境
chmod 755 src/main/resources/static/images/products

# 生產環境 (Docker)
chmod 755 ./product-images
```

### 目錄結構示例

```
onlineShopping/
├── src/main/resources/static/images/products/
│   ├── a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
│   ├── b2c3d4e5-f6g7-8901-bcde-fg2345678901.png
│   ├── c3d4e5f6-g7h8-9012-cdef-gh3456789012.jpg
│   ├── iphone-15-pro.jpg  (初始示例圖片)
│   └── README.md
└── product-images/  (可選: Docker volume 掛載點)
    ├── d4e5f6g7-h8i9-0123-defg-hi4567890123.jpg
    └── ...
```

## 錯誤處理

### 常見錯誤

1. **"檔案為空"**
   - 原因: 未選擇文件或文件上傳失敗
   - 解決: 重新選擇文件並確保文件不為空

2. **"檔案必須是圖片格式"**
   - 原因: 上傳的不是圖片文件
   - 解決: 只上傳 JPG, PNG, GIF 格式的圖片

3. **"檔案大小不能超過 5MB"**
   - 原因: 圖片文件太大
   - 解決: 壓縮圖片或選擇更小的文件

4. **"無效的檔案名稱"**
   - 原因: 文件名為空或無效
   - 解決: 重新選擇有效的文件

5. **目錄權限錯誤**
   - 原因: 應用程序沒有寫入權限
   - 解決: 
     ```bash
     chmod 755 src/main/resources/static/images/products
     ```

6. **圖片無法顯示**
   - 檢查文件是否成功上傳:
     ```bash
     ls -la src/main/resources/static/images/products/
     ```
   - 檢查數據庫中的 imageUrl 字段
   - 檢查瀏覽器控制台是否有 404 錯誤

### 調試日誌

系統會在控制台輸出上傳信息:

```
Created upload directory: /path/to/onlineShopping/src/main/resources/static/images/products
Image saved successfully: /path/to/onlineShopping/src/main/resources/static/images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
Image URL: /images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
```

啟用詳細日誌:

```properties
# application.properties
logging.level.com.onlineshop.controller.MerchantProductController=DEBUG
```

## 性能優化建議

### 1. 圖片優化

- **壓縮**: 使用工具壓縮圖片以減小文件大小
  - 推薦工具: TinyPNG, ImageOptim, Squoosh
- **尺寸**:
  - 列表頁縮略圖: 300x300px
  - 詳情頁大圖: 800x800px
  - 原圖: 最大 1200x1200px
- **格式**: 
  - JPG: 適合照片
  - PNG: 適合透明背景
  - WebP: 更小的文件大小 (推薦)

### 2. 存儲優化

- **定期清理**: 刪除未使用的圖片文件
  ```bash
  # 查找超過 30 天未訪問的圖片
  find src/main/resources/static/images/products -type f -atime +30
  ```
- **備份**: 定期備份圖片目錄
  ```bash
  tar -czf product-images-backup-$(date +%Y%m%d).tar.gz src/main/resources/static/images/products/
  ```
- **監控**: 監控磁盤空間使用情況
  ```bash
  du -sh src/main/resources/static/images/products/
  ```

### 3. 訪問優化

- **CDN**: 對於生產環境,考慮使用 CDN 服務
- **緩存**: Spring Boot 自動為靜態資源設置緩存頭
- **多尺寸**: 考慮自動生成不同尺寸的縮略圖

## 安全注意事項

### 1. 文件類型驗證

系統會驗證上傳文件的 MIME 類型,只允許圖片格式:

```java
if (contentType == null || !contentType.startsWith("image/")) {
    throw new IllegalArgumentException("檔案必須是圖片格式");
}
```

### 2. 文件名安全

使用 UUID 生成唯一文件名,避免:
- 路徑遍歷攻擊 (../)
- 文件名衝突
- 惡意文件名
- 特殊字符問題

### 3. 大小限制

限制文件大小為 5MB,防止:
- 磁盤空間耗盡
- 上傳超時
- 拒絕服務攻擊 (DoS)

### 4. 權限控制

- 只有登錄的商家可以上傳圖片
- 商家只能管理自己的商品圖片
- 圖片訪問是公開的 (通過 URL)

## 未來改進建議

1. **圖片自動處理**:
   - 自動壓縮和優化
   - 自動生成多種尺寸
   - 自動轉換為 WebP 格式

2. **多圖上傳**:
   - 支持一次上傳多張圖片
   - 圖片排序和管理
   - 主圖和副圖設置

3. **圖片編輯**:
   - 在線裁剪
   - 旋轉和翻轉
   - 濾鏡和調整

4. **雲存儲集成**:
   - AWS S3
   - 阿里雲 OSS
   - 騰訊雲 COS
   - 七牛雲

5. **CDN 加速**:
   - 使用 CDN 服務加速圖片訪問
   - 自動同步到 CDN
   - 智能 DNS 解析

6. **自動清理**:
   - 刪除商品時自動刪除關聯圖片
   - 定期清理未使用的圖片
   - 圖片使用統計

## 測試建議

### 功能測試

1. **上傳測試**:
   ```bash
   # 測試不同格式
   - 上傳 JPG 圖片
   - 上傳 PNG 圖片
   - 上傳 GIF 圖片
   ```

2. **驗證測試**:
   ```bash
   # 測試文件大小限制
   - 上傳 < 5MB 的圖片 (應該成功)
   - 上傳 > 5MB 的圖片 (應該失敗)
   
   # 測試文件類型
   - 上傳圖片文件 (應該成功)
   - 上傳 PDF 文件 (應該失敗)
   - 上傳 TXT 文件 (應該失敗)
   ```

3. **訪問測試**:
   ```bash
   # 驗證圖片可以訪問
   curl -I http://localhost:8080/images/products/{filename}.jpg
   
   # 應該返回 200 OK
   ```

### 性能測試

```bash
# 測試並發上傳
ab -n 100 -c 10 -p image.jpg -T multipart/form-data http://localhost:8080/merchant/products
```

## 相關文件

- **控制器**: [`src/main/java/com/onlineshop/controller/MerchantProductController.java`](../src/main/java/com/onlineshop/controller/MerchantProductController.java)
- **模板**: [`src/main/resources/templates/merchant/product-form.html`](../src/main/resources/templates/merchant/product-form.html)
- **配置**: [`src/main/resources/application.properties`](../src/main/resources/application.properties)
- **圖片目錄**: `src/main/resources/static/images/products/`
- **安全配置**: [`src/main/java/com/onlineshop/security/SecurityConfig.java`](../src/main/java/com/onlineshop/security/SecurityConfig.java)

## 參考文檔

- [Spring Boot File Upload](https://spring.io/guides/gs/uploading-files/)
- [Thymeleaf File Upload](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#multipart-file-upload)
- [Spring Boot Static Resources](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc.static-content)
- [Docker Volumes](https://docs.docker.com/storage/volumes/)

## 總結

本系統的圖片上傳功能特點:

✅ **簡單直接**: 圖片存儲在源代碼的靜態資源目錄中  
✅ **自動管理**: 自動創建目錄、生成唯一文件名  
✅ **安全可靠**: 文件類型和大小驗證、UUID 文件名  
✅ **易於訪問**: 通過標準的靜態資源 URL 訪問  
✅ **便於部署**: 支持開發環境和 Docker 部署  
✅ **可擴展**: 易於擴展到雲存儲或 CDN

**存儲路徑**: `src/main/resources/static/images/products/`  
**訪問 URL**: `/images/products/{filename}`  
**最大大小**: 5MB  
**支持格式**: JPG, PNG, GIF