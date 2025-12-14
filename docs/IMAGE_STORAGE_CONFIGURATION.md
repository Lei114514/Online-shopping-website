# 圖片存儲配置說明

## 概述

本文檔說明如何配置圖片存儲，使圖片數據存儲在本地而不是從網址獲取。

## 存儲路徑

### 本地開發環境

**圖片存儲位置：**
```
項目根目錄/src/main/resources/static/images/products/
```

**完整路徑示例：**
```
/home/lei/project/onlineShopping/src/main/resources/static/images/products/
```

### Docker 容器環境

**容器內部路徑：**
```
/app/src/main/resources/static/images/products/
```

**重要：** 為了讓容器內上傳的圖片在本地可見，需要配置 Docker volume 掛載。

## Docker Volume 配置

### 1. 修改 docker-compose.yml

在 `docker-compose.yml` 文件中添加 volume 掛載：

```yaml
services:
  app:
    volumes:
      - uploads:/app/uploads
      # 掛載本地圖片目錄到容器
      - ./src/main/resources/static/images/products:/app/src/main/resources/static/images/products
```

### 2. 重啟容器並同步現有圖片

使用提供的腳本重啟容器：

```bash
chmod +x restart-with-volume.sh
./restart-with-volume.sh
```

該腳本會：
1. 停止現有容器
2. 從容器複製現有圖片到本地
3. 使用新的 volume 配置重啟容器
4. 驗證同步是否成功

## 圖片訪問方式

### URL 路徑

圖片通過以下 URL 訪問：

```
http://localhost:8080/images/products/{filename}
```

**示例：**
```
http://localhost:8080/images/products/iphone-15-pro.jpg
http://localhost:8080/images/products/e136ab3b-b953-4ce0-b9c7-1f8aced4ea34.png
```

### 在模板中使用

```html
<!-- 使用 Thymeleaf -->
<img th:src="@{/images/products/{filename}(filename=${product.imageUrl})}" 
     alt="Product Image">

<!-- 直接使用 -->
<img src="/images/products/iphone-15-pro.jpg" alt="Product">
```

## 圖片上傳流程

### 1. 商家上傳圖片

商家在產品表單中選擇圖片文件：

```html
<input type="file" name="imageFile" accept="image/*">
```

### 2. 後端處理

[`MerchantProductController.java`](../src/main/java/com/onlineshop/controller/MerchantProductController.java) 處理上傳：

```java
private String saveProductImage(MultipartFile imageFile) throws IOException {
    // 生成唯一文件名
    String filename = UUID.randomUUID().toString() + 
                     getFileExtension(imageFile.getOriginalFilename());
    
    // 構建完整路徑
    Path uploadPath = Paths.get(uploadDir);
    Path filePath = uploadPath.resolve(filename);
    
    // 保存文件
    Files.copy(imageFile.getInputStream(), filePath, 
               StandardCopyOption.REPLACE_EXISTING);
    
    return filename;
}
```

### 3. 存儲到數據庫

只存儲文件名（不是完整路徑）：

```java
product.setImageUrl(filename);  // 例如: "e136ab3b-b953-4ce0-b9c7-1f8aced4ea34.png"
```

## 配置文件

### application.properties

```properties
# 圖片上傳配置
upload.path=src/main/resources/static/images/products
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## 目錄結構

```
onlineShopping/
├── src/
│   └── main/
│       └── resources/
│           └── static/
│               └── images/
│                   └── products/          # 圖片存儲目錄
│                       ├── iphone-15-pro.jpg
│                       ├── e136ab3b-xxx.png
│                       └── README.md
├── docker-compose.yml                     # Docker 配置
└── restart-with-volume.sh                 # 重啟腳本
```

## 驗證配置

### 1. 檢查本地目錄

```bash
ls -la src/main/resources/static/images/products/
```

### 2. 檢查容器內目錄

```bash
docker exec <container_id> ls -la /app/src/main/resources/static/images/products/
```

### 3. 測試上傳

1. 登入商家帳號
2. 進入產品管理頁面
3. 創建或編輯產品
4. 上傳圖片
5. 檢查本地目錄是否出現新圖片

### 4. 測試訪問

在瀏覽器訪問：
```
http://localhost:8080/images/products/<filename>
```

## 常見問題

### Q1: 上傳的圖片在容器內但本地看不到？

**原因：** 沒有配置 Docker volume 掛載

**解決：**
1. 修改 `docker-compose.yml` 添加 volume 掛載
2. 運行 `./restart-with-volume.sh` 重啟容器

### Q2: 圖片無法訪問（404 錯誤）？

**檢查：**
1. 文件是否存在於 `static/images/products/` 目錄
2. 文件名是否正確
3. Spring Boot 靜態資源配置是否正確

### Q3: 上傳失敗？

**檢查：**
1. 目錄權限是否正確
2. 文件大小是否超過限制（默認 10MB）
3. 查看應用日誌獲取詳細錯誤信息

## 安全考慮

### 1. 文件類型驗證

只允許圖片文件：

```java
private static final Set<String> ALLOWED_EXTENSIONS = 
    Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
```

### 2. 文件大小限制

在 `application.properties` 中配置：

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### 3. 文件名處理

使用 UUID 生成唯一文件名，避免：
- 文件名衝突
- 路徑遍歷攻擊
- 特殊字符問題

## 優化建議

### 1. 圖片壓縮

考慮在上傳時自動壓縮圖片以節省空間和提高加載速度。

### 2. CDN 部署

生產環境建議使用 CDN 服務（如阿里雲 OSS、AWS S3）來存儲和分發圖片。

### 3. 縮略圖生成

自動生成不同尺寸的縮略圖以優化頁面加載。

### 4. 定期清理

定期清理未使用的圖片文件以節省存儲空間。

## 相關文檔

- [圖片上傳功能說明](IMAGE_UPLOAD_STORAGE.md)
- [本地資源配置](LOCAL_RESOURCES.md)
- [Docker 日誌查看指南](VIEW_DOCKER_LOGS_GUIDE.md)

## 總結

通過配置 Docker volume 掛載，圖片數據現在：
- ✅ 存儲在本地文件系統
- ✅ 容器和主機之間自動同步
- ✅ 重啟容器後數據不會丟失
- ✅ 可以直接在本地查看和管理圖片文件

**存儲路徑：** `src/main/resources/static/images/products/`