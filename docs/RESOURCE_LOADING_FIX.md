# 資源加載問題修復文檔

## 問題描述

當頁面缺少外部 CDN 資源（如 `data:image/svg+xml` 和 `fa-solid-900.woff2`）時，頁面會出現錯誤或無法正常顯示。

## 根本原因分析

1. **Spring Security 內容安全策略（CSP）限制**
   - 默認的 CSP 策略阻止了從外部 CDN 加載資源
   - 字體檔案（woff2）和 SVG 圖標被阻止

2. **靜態資源處理不完善**
   - 缺少明確的靜態資源處理器配置
   - 圖片控制器缺少適當的錯誤處理

3. **缺少資源加載降級機制**
   - 當主 CDN 失敗時沒有備用方案
   - 資源加載失敗會阻塞頁面渲染

## 解決方案

### 1. 更新 Spring Security 配置 ([`SecurityConfig.java`](../src/main/java/com/onlineshop/security/SecurityConfig.java))

#### 添加內容安全策略（CSP）

```java
.headers(headers -> headers
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://cdn.bootcdn.net https://cdnjs.cloudflare.com; " +
            "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdn.bootcdn.net https://cdnjs.cloudflare.com; " +
            "font-src 'self' data: https://cdnjs.cloudflare.com https://cdn.bootcdn.net; " +
            "img-src 'self' data: https:; " +
            "connect-src 'self'")
    )
)
```

**關鍵配置**：
- `font-src 'self' data: https://cdnjs.cloudflare.com` - 允許從 CDN 加載字體
- `img-src 'self' data: https:` - 允許 data URI 和 HTTPS 圖片
- `style-src 'unsafe-inline'` - 允許內聯樣式（Bootstrap 需要）
- `script-src 'unsafe-inline' 'unsafe-eval'` - 允許內聯腳本

#### 擴展靜態資源訪問權限

```java
.requestMatchers(
    "/", "/home", "/products", "/products/**", "/register", "/login", 
    "/css/**", "/js/**", "/images/**", "/fonts/**", "/static/**",
    "/favicon.ico", "/error"
).permitAll()
```

### 2. 創建 Web MVC 配置 ([`WebMvcConfig.java`](../src/main/java/com/onlineshop/config/WebMvcConfig.java))

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置圖片資源
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(3600);
        
        // 配置 CSS、JS、字體等資源
        // ...
    }
}
```

**作用**：
- 明確定義靜態資源的處理規則
- 設置適當的緩存策略
- 確保所有資源路徑都能正確解析

### 3. 改進圖片控制器 ([`ImageController.java`](../src/main/java/com/onlineshop/controller/ImageController.java))

#### 添加錯誤處理

```java
if (!resource.exists() || !resource.isReadable()) {
    // 返回 404 而不是錯誤，這樣不會破壞頁面
    return ResponseEntity.notFound().build();
}
```

#### 添加緩存控制

```java
CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS)
        .cachePublic();

return ResponseEntity.ok()
        .cacheControl(cacheControl)
        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
        .body(resource);
```

**改進**：
- 優雅處理圖片不存在的情況
- 添加 CORS 頭允許跨域訪問
- 設置緩存策略提高性能

### 4. 更新 application.properties ([`application.properties`](../src/main/resources/application.properties))

```properties
# 靜態資源配置
spring.web.resources.static-locations=classpath:/static/
spring.web.resources.add-mappings=true
spring.web.resources.chain.strategy.content.enabled=true
spring.web.resources.chain.strategy.content.paths=/**
spring.web.resources.cache.cachecontrol.max-age=3600
spring.web.resources.cache.cachecontrol.cache-public=true

# 允許跨域資源共享（CORS）
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

### 5. 前端資源降級機制 ([`layout.html`](../src/main/resources/templates/layout.html))

#### CSS 資源降級

```html
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" 
      rel="stylesheet"
      onerror="this.onerror=null;this.href='https://cdn.bootcdn.net/ajax/libs/bootstrap/5.1.3/css/bootstrap.min.css'">
```

#### 備用樣式

```css
/* 確保頁面始終可見 */
body {
    visibility: visible !important;
    opacity: 1 !important;
}

/* Font Awesome 載入失敗時的備用樣式 */
.no-font-awesome .fas::before {
    content: '•';
}
```

#### JavaScript 錯誤處理

```javascript
// 檢測 Font Awesome 是否加載成功
// 處理圖片加載錯誤
// 全局資源錯誤處理
```

## 測試驗證

### 測試場景

1. **正常情況**：所有 CDN 資源可用
   - ✅ 頁面正常顯示
   - ✅ 圖標正確渲染
   - ✅ 圖片正常加載

2. **CDN 不可用**：主 CDN 失敗
   - ✅ 自動切換到備用 CDN
   - ✅ 頁面仍然可訪問
   - ✅ 使用備用圖標樣式

3. **完全離線**：所有外部資源不可用
   - ✅ 頁面結構完整
   - ✅ 使用簡單符號替代圖標
   - ✅ 本地圖片正常顯示

### 測試步驟

1. **啟動應用**：
   ```bash
   mvn spring-boot:run
   ```

2. **訪問頁面**：
   ```
   http://localhost:8080
   ```

3. **檢查瀏覽器控制台**：
   - 不應有 CSP 違規錯誤
   - 不應有資源加載失敗錯誤
   - 圖片 404 錯誤應被優雅處理

4. **測試網絡條件**：
   - 使用瀏覽器開發工具模擬慢速網絡
   - 使用 AdBlock 阻止 CDN 資源
   - 檢查頁面是否仍然可用

## 性能優化

### 緩存策略

1. **靜態資源緩存**：1小時
2. **圖片資源緩存**：1小時
3. **CDN 資源**：由 CDN 控制

### 加載優化

1. **資源預加載**：關鍵 CSS 和 JS
2. **延遲加載**：非關鍵圖片
3. **降級機制**：多個 CDN 備份

## 故障排除

### 問題：圖片仍然無法顯示

**檢查**：
1. 圖片檔案是否存在於 `src/main/resources/static/images/products/`
2. 檔案名稱是否與數據庫中的 `image_url` 匹配
3. 檢查 ImageController 日誌

**解決**：
```bash
# 檢查圖片目錄
ls -la src/main/resources/static/images/products/

# 查看應用日誌
tail -f logs/application.log
```

### 問題：CSP 違規錯誤

**檢查**：
1. 瀏覽器控制台的 CSP 錯誤訊息
2. SecurityConfig 中的 CSP 策略

**解決**：
根據錯誤訊息調整 CSP 策略，添加缺少的域名

### 問題：字體無法加載

**檢查**：
1. Network 標籤中的字體請求
2. CSP 策略中的 `font-src` 指令

**解決**：
確保 `font-src` 包含 `data:` 和相關 CDN 域名

## 最佳實踐

1. **始終提供降級方案**：主 CDN + 備用 CDN + 本地備份
2. **優雅處理錯誤**：不要讓資源加載失敗破壞頁面
3. **適當的緩存策略**：平衡性能和更新頻率
4. **監控資源加載**：記錄失敗情況以便優化
5. **定期測試**：在不同網絡條件下測試

## 相關文件

- [`SecurityConfig.java`](../src/main/java/com/onlineshop/security/SecurityConfig.java) - 安全配置
- [`WebMvcConfig.java`](../src/main/java/com/onlineshop/config/WebMvcConfig.java) - Web MVC 配置
- [`ImageController.java`](../src/main/java/com/onlineshop/controller/ImageController.java) - 圖片控制器
- [`layout.html`](../src/main/resources/templates/layout.html) - 頁面佈局模板
- [`application.properties`](../src/main/resources/application.properties) - 應用配置

## 更新日誌

- **2024-12-11**：初始版本，解決資源加載問題
  - 添加 CSP 配置
  - 創建 WebMvcConfig
  - 改進 ImageController
  - 添加前端降級機制