# 數據初始化說明

## 概述

本文檔說明系統啟動時的數據初始化行為。

## 初始化內容

### 1. 商品分類（Categories）

系統會自動創建以下預設分類（僅在數據庫中沒有分類時）：

- **電子產品** - 智能手機、電腦及其他電子設備
- **配件** - 電子產品配件
- **服裝** - 時尚服飾
- **書籍** - 書籍和出版物
- **家居用品** - 家電和生活用品

### 2. 測試用戶（Test Users）

系統會自動創建以下測試用戶（僅在數據庫中沒有用戶時）：

| 用戶名 | 密碼 | 角色 | 用途 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 管理員 - 可訪問後台管理功能 |
| merchant | merchant123 | MERCHANT | 商家 - 可管理商品和訂單 |
| customer | customer123 | CUSTOMER | 顧客 - 可瀏覽和購買商品 |

### 3. 商品數據（Products）

**重要變更：** 系統**不再**自動創建默認商品數據。

- ✅ 所有商品需要由銷售人員手動添加
- ✅ 這確保了所有商品信息都是真實和最新的
- ✅ 避免了測試數據污染生產環境

## 數據初始化邏輯

### 啟動檢查

系統在每次啟動時會執行以下檢查：

1. **分類檢查**
   - 如果 `categoryRepository.count() == 0`，則創建預設分類
   - 否則跳過分類初始化

2. **用戶檢查**
   - 如果 `userRepository.count() == 0`，則創建測試用戶
   - 否則跳過用戶初始化

3. **商品檢查**
   - 不再自動創建任何商品
   - 商品數量僅用於日誌輸出

## 如何添加商品

### 方式一：使用銷售人員界面（推薦）

1. 使用銷售人員賬號登錄（sales / sales123）
2. 訪問產品管理頁面
3. 點擊「新增商品」
4. 填寫商品信息並上傳圖片
5. 保存商品

### 方式二：使用管理員界面

1. 使用管理員賬號登錄（admin / admin123）
2. 訪問後台管理系統
3. 通過管理界面添加或管理商品

## 清除現有數據

如果需要清除數據庫中的所有數據並重新初始化：

### 方法一：刪除數據庫文件（簡單）

```bash
# 停止容器
docker-compose down

# 刪除數據庫文件
rm -rf ./data/h2db

# 重新啟動
docker-compose up -d
```

### 方法二：使用 H2 控制台（精確）

1. 訪問 H2 控制台：http://localhost:8080/h2-console
2. 使用以下連接信息：
   - JDBC URL: `jdbc:h2:file:./data/h2db/onlineshop`
   - Username: `sa`
   - Password: `password`
3. 執行 SQL 清除特定表的數據

## 圖片存儲說明

商品圖片存儲在：
- **容器內路徑**: `/app/src/main/resources/static/images/products`
- **主機掛載路徑**: `./src/main/resources/static/images/products`

詳細信息請參考：[圖片存儲配置指南](IMAGE_STORAGE_GUIDE.md)

## 相關文件

- **數據初始化器**: `src/main/java/com/onlineshop/config/DataInitializer.java`
- **商家控制器**: `src/main/java/com/onlineshop/controller/MerchantProductController.java`
- **圖片控制器**: `src/main/java/com/onlineshop/controller/ImageController.java`

## 版本歷史

### v2.0 (2025-12-14)
- ❌ 移除默認商品數據創建
- ✅ 保留分類和測試用戶創建
- ✅ 添加銷售人員測試用戶（SALES角色）
- ✅ 改進初始化日誌輸出

### v1.0 (初始版本)
- ✅ 自動創建6個默認商品
- ✅ 創建5個商品分類
- ✅ 創建管理員和顧客測試用戶

## 注意事項

1. **生產環境部署前**，請修改所有測試用戶的密碼
2. **商品分類**可以通過管理界面進行修改或添加
3. **測試用戶**僅用於開發和測試，不應在生產環境中使用
4. 系統使用 H2 內存數據庫時，每次重啟會丟失所有數據
5. 使用文件數據庫時，數據會持久化到 `./data/h2db` 目錄

## 技術細節

### DataInitializer 類

```java
@Component
public class DataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        //僅創建分類和測試用戶
        // 不再創建默認商品
    }
}
```

### 執行時機

- Spring Boot 應用啟動後
- 在 `CommandLineRunner` 接口的 `run()` 方法中執行
- 在所有 Bean 初始化完成後執行

##疑難排解

### Q: 為什麼首頁沒有顯示商品？
A: 因為系統不再自動創建商品。請使用銷售人員賬號（sales/sales123）登錄並手動添加商品。

### Q: 如何重置所有數據？
A: 停止容器後刪除 `./data/h2db` 目錄，然後重新啟動容器。

### Q: 測試用戶無法登錄？
A: 確認數據庫已成功初始化，檢查應用啟動日誌中的用戶創建信息。

### Q: 商品分類顯示亂碼？
A: 請參考 [中文字符編碼解決方案](CHINESE_ENCODING_SOLUTION.md)。