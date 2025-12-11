# 商品圖片目錄

##📂 存儲路徑
**絕對路徑**: `src/main/resources/static/images/products/`

**訪問URL**: `http://localhost:8080/images/products/[圖片檔名]`

## 📋 所需圖片列表

請將以下商品圖片放置在此目錄中：

1. **iphone-15-pro.jpg** - iPhone 15 Pro 手機圖片
2. **macbook-pro.jpg** - MacBook Pro 筆記本電腦圖片
3. **airpods-pro.jpg** - AirPods Pro 耳機圖片
4. **ipad-air.jpg** - iPad Air 平板電腦圖片
5. **apple-watch.jpg** - Apple Watch 手表圖片
6. **magic-keyboard.jpg** - Magic Keyboard 鍵盤圖片

## 📐 圖片要求
- **格式**: JPG、PNG 或 WEBP
- **建議尺寸**: 至少 400x300 像素 (建議比例 4:3)
- **建議大小**: 小於 500KB
- **命名規則**: 使用小寫字母和連字符,不含空格

## 📥 如何獲取圖片

您可以從以下來源獲取商品圖片：
- 產品官方網站
- 免費圖庫網站（如 [Unsplash](https://unsplash.com)、[Pexels](https://pexels.com)）
- 您自己的圖片

**步驟**:
1. 下載圖片到本地
2. 重命名為上述指定的文件名
3. 放置在 `src/main/resources/static/images/products/` 目錄中
4. 重新啟動應用程序以加載圖片

## 🔧 圖片配置說明

商品圖片路徑在以下文件中配置：
- **初始化數據**: `src/main/java/com/onlineshop/config/DataInitializer.java`
- **產品模型**: `src/main/java/com/onlineshop/model/Product.java`

圖片路徑格式: `/images/products/[圖片檔名].jpg`

## ⚠️ 注意事項

- 如果圖片不存在,頁面將顯示預設佔位圖片
- 所有圖片必須放在此目錄下才能正常訪問
- Spring Boot 會自動映射 `static` 目錄下的靜態資源
- 修改圖片後無需重新編譯,但可能需要清除瀏覽器緩存