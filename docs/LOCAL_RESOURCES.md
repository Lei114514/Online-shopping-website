# 本地資源配置說明

## 概述

本專案已將所有外部 CDN 資源(Bootstrap 和 Font Awesome)下載到本地,實現完全本地化部署,不依賴任何外部網路資源。

## 資源存儲路徑

所有本地資源存儲在 `src/main/resources/static/vendor/` 目錄下:

```
src/main/resources/static/vendor/
├── bootstrap/
│   ├── css/
│   │   └── bootstrap.min.css (160KB)
│   └── js/
│       └── bootstrap.bundle.min.js (78 KB)
└── fontawesome/
    ├── css/
    │   └── all.min.css (89 KB)
    └── webfonts/
        ├── fa-solid-900.woff2 (123 KB)
        ├── fa-regular-400.woff2 (24 KB)
        └── fa-brands-400.woff2 (103 KB)
```

## 資源版本

- **Bootstrap**: 5.1.3
- **Font Awesome**: 6.0.0

## 配置文件

### 1. 模板配置 (layout.html)

```html
<!-- Bootstrap CSS (本地) -->
<link rel="stylesheet" th:href="@{/vendor/bootstrap/css/bootstrap.min.css}">

<!-- Font Awesome (本地) -->
<link rel="stylesheet" th:href="@{/vendor/fontawesome/css/all.min.css}">

<!-- Bootstrap JS (本地) -->
<script th:src="@{/vendor/bootstrap/js/bootstrap.bundle.min.js}"></script>
```

### 2. Spring MVC 配置 (WebMvcConfig.java)

```java
// 配置 vendor 資源（Bootstrap, Font Awesome 等）
registry.addResourceHandler("/vendor/**").addResourceLocations("classpath:/static/vendor/")
        .setCachePeriod(86400); // 24小時緩存
```

### 3. 安全配置 (SecurityConfig.java)

```java
// 允許公開訪問 vendor 資源
.requestMatchers("/", "/home", "/products", "/products/**", "/register", "/login",
    "/css/**", "/js/**", "/images/**", "/fonts/**", "/vendor/**", "/static/**",
    "/favicon.ico", "/error"
).permitAll()

// 內容安全策略（僅使用本地資源）
.contentSecurityPolicy(csp -> csp
    .policyDirectives("default-src 'self'; " +
        "script-src 'self' 'unsafe-inline'; " +
        "style-src 'self' 'unsafe-inline'; " +
        "font-src 'self' data:; " +
        "img-src 'self' data:; " +
        "connect-src 'self'")
)
```

## 訪問 URL

在瀏覽器中,這些資源通過以下 URL 訪問:

- Bootstrap CSS: `http://localhost:8080/vendor/bootstrap/css/bootstrap.min.css`
- Bootstrap JS: `http://localhost:8080/vendor/bootstrap/js/bootstrap.bundle.min.js`
- Font Awesome CSS: `http://localhost:8080/vendor/fontawesome/css/all.min.css`
- Font Awesome 字體: `http://localhost:8080/vendor/fontawesome/webfonts/*.woff2`

## 優勢

1. **完全離線運行**: 不需要網路連接即可正常顯示頁面
2. **更快的加載速度**: 本地資源加載比CDN 更快
3. **更好的安全性**: 不依賴外部資源,減少安全風險
4. **穩定性**: 不受CDN 服務中斷影響
5. **隱私保護**: 不會向第三方 CDN 洩露用戶訪問信息

## 缓存策略

- **Vendor 資源**: 24小時緩存 (86400秒)
- **其他靜態資源**: 1小時緩存 (3600秒)

## 更新資源

如需更新 Bootstrap 或 Font Awesome 版本:

1. 從官方網站下載最新版本
2. 替換 `src/main/resources/static/vendor/` 下對應的文件
3. 重新編譯並部署應用

## 注意事項

1. 所有字體文件必須使用 `.woff2` 格式以獲得最佳兼容性和壓縮率
2. Font Awesome CSS 中的字體路徑使用相對路徑 `../webfonts/`,確保目錄結構正確
3. 如果添加新的 vendor 資源,需要在 `WebMvcConfig.java` 中配置對應的資源處理器

## 故障排除

###圖標不顯示

1. 檢查字體文件是否存在於 `src/main/resources/static/vendor/fontawesome/webfonts/`
2. 檢查瀏覽器控制台是否有 404 錯誤
3. 確認 `all.min.css` 中的字體路徑是否正確

### Bootstrap樣式不生效

1. 確認 `bootstrap.min.css` 文件存在且完整
2. 檢查瀏覽器開發工具的Network 標籤,確認 CSS 文件成功加載
3. 清除瀏覽器緩存後重試

### JavaScript 功能不正常

1. 確認 `bootstrap.bundle.min.js` 已正確加載
2. 檢查瀏覽器控制台是否有 JavaScript 錯誤
3. 確認 script標籤在 HTML 中的位置(應在 body 底部)