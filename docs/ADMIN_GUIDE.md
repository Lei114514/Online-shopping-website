# 管理員功能使用指南

## 概述

本系統提供完整的管理員後台功能,包括客戶管理、訂單管理和用戶活動日誌追蹤。

## 訪問管理員後台

###訪問地址
```
http://localhost:8080/admin
```

### 權限要求
只有擁有 `ADMIN` 角色的用戶才能訪問管理員後台。

### 默認管理員帳號
在 [`DataInitializer.java`](../src/main/java/com/onlineshop/config/DataInitializer.java) 中創建。

## 功能模塊

### 1. 管理員儀表板 (`/admin`)

**功能:**
- 顯示系統整體統計數據
- 快速訪問各個管理模塊
- 查看最近訂單
- 查看最近用戶活動

**統計指標:**
- 總用戶數
- 總訂單數
- 待處理訂單數
- 處理中訂單數

**快速導航:**
- 客戶管理
- 訂單管理
- 活動日誌
- 統計報表

### 2. 客戶管理 (`/admin/customers`)

#### 客戶列表
**訪問路徑:** `/admin/customers`

**功能:**
- 查看所有註冊客戶
- 搜索客戶(用戶名、郵箱、姓名)
- 按角色篩選(CUSTOMER, SALES, ADMIN)
- 查看客戶基本信息
- 查看客戶狀態(啟用/停用)

**顯示信息:**
- 用戶ID
- 用戶名
- 姓名
- 郵箱
- 電話
- 角色
- 帳戶狀態
- 註冊日期

#### 客戶詳情
**訪問路徑:** `/admin/customers/{id}`

**功能:**
- 查看客戶完整資料
- 查看客戶訂單歷史
- 查看客戶活動日誌
- 啟用/停用客戶帳戶

**詳細信息:**

**基本信息:**
- 用戶名
- 完整姓名
- 郵箱地址
- 電話號碼
- 配送地址
- 用戶角色
- 帳戶狀態
- 註冊時間
- 最後更新時間

**統計數據:**
- 總訂單數
- 完成訂單數
- 活動記錄數

**訂單記錄:**
- 所有訂單列表
- 訂單狀態
- 訂單金額
- 訂單日期

**活動日誌:**
- 最近50條活動記錄
- 活動類型
- 活動詳情
- IP地址
- 時間戳

**操作:**
- 啟用/停用帳戶: 點擊對應按鈕即可切換客戶帳戶狀態

### 3. 訂單管理 (`/admin/orders`)

#### 訂單列表
**訪問路徑:** `/admin/orders`

**功能:**
- 查看所有訂單
- 按訂單編號搜索
- 按訂單狀態篩選
- 按支付狀態篩選

**篩選選項:**

**訂單狀態:**
- PENDING - 待處理
- PROCESSING - 處理中
- SHIPPED - 已發貨
- DELIVERED - 已送達
- CANCELLED - 已取消

**支付狀態:**
- PENDING - 待支付
- PAID - 已支付
- FAILED - 支付失敗

**顯示信息:**
- 訂單編號
- 客戶名稱
- 訂單金額
- 訂單狀態
- 支付狀態
- 配送地址
- 訂單日期

#### 訂單詳情
**訪問路徑:** `/admin/orders/{id}`

**功能:**
- 查看訂單完整信息
- 更新訂單狀態
- 更新支付狀態
- 查看訂單項目明細
- 查看配送信息

**訂單信息:**
- 訂單編號
- 訂單日期
- 客戶信息(可點擊查看客戶詳情)
- 總金額
- 訂單狀態
- 支付狀態
- 支付方式
- 最後更新時間

**配送信息:**
- 配送地址
- 帳單地址
- 訂單備註

**訂單項目:**
- 商品名稱
- 商品SKU
- 單價
- 數量
- 小計
- 總計

**操作:**

1. **更新訂單狀態:**
   - 選擇新的訂單狀態
   - 點擊"更新狀態"按鈕
   - 系統會自動記錄此操作

2. **更新支付狀態:**
   - 選擇新的支付狀態
   - 點擊"更新支付"按鈕
   - 如果支付成功,訂單狀態會自動更新為"處理中"

### 4. 用戶活動日誌 (`/admin/activity-logs`)

**訪問路徑:** `/admin/activity-logs`

**功能:**
- 查看所有用戶活動記錄
- 按活動類型篩選
- 按用戶篩選
- 按時間範圍篩選

**篩選選項:**

**活動類型:**
- LOGIN - 登入
- LOGOUT - 登出
- VIEW_PRODUCT - 瀏覽商品
- ADD_TO_CART - 加入購物車
- REMOVE_FROM_CART - 從購物車移除
- PLACE_ORDER - 下訂單
- SEARCH - 搜索
- REGISTER - 註冊

**時間範圍:**
- 今天
- 最近7天
- 最近30天
- 最近90天
- 全部

**顯示信息:**
- 記錄ID
- 用戶名稱
- 活動類型
- 活動詳情
- IP地址
- 時間戳

**用途:**
- 監控用戶行為
- 分析用戶興趣
- 發現異常活動
- 追蹤訂單流程
- 優化用戶體驗

### 5. 統計報表 (`/admin/statistics`)

**訪問路徑:** `/admin/statistics`

**功能:**
- 銷售統計
- 熱銷商品分析
- 活動類型統計

**時間範圍選項:**
- 最近7天
- 最近30天
- 最近90天

## 技術實現

### 後端組件

#### 控制器
[`AdminController.java`](../src/main/java/com/onlineshop/controller/AdminController.java)
- 處理所有管理員請求
- 提供數據給前端頁面
- 執行管理操作

#### 服務層
- [`OrderService.java`](../src/main/java/com/onlineshop/service/OrderService.java) - 訂單業務邏輯
- [`UserService.java`](../src/main/java/com/onlineshop/service/UserService.java) - 用戶業務邏輯

#### 數據訪問層
- [`UserRepository.java`](../src/main/java/com/onlineshop/repository/UserRepository.java)
- [`OrderRepository.java`](../src/main/java/com/onlineshop/repository/OrderRepository.java)
- [`UserActivityLogRepository.java`](../src/main/java/com/onlineshop/repository/UserActivityLogRepository.java)

#### 安全配置
[`SecurityConfig.java`](../src/main/java/com/onlineshop/security/SecurityConfig.java)
- 配置 `/admin/**` 路徑只允許 ADMIN 角色訪問
- 使用 `@PreAuthorize("hasRole('ADMIN')")` 註解保護控制器

### 前端頁面

所有管理員頁面位於 `src/main/resources/templates/admin/` 目錄:

- [`dashboard.html`](../src/main/resources/templates/admin/dashboard.html) - 管理員儀表板
- [`customers.html`](../src/main/resources/templates/admin/customers.html) - 客戶列表
- [`customer-detail.html`](../src/main/resources/templates/admin/customer-detail.html) - 客戶詳情
- [`orders.html`](../src/main/resources/templates/admin/orders.html) - 訂單列表
- [`order-detail.html`](../src/main/resources/templates/admin/order-detail.html) - 訂單詳情
- [`activity-logs.html`](../src/main/resources/templates/admin/activity-logs.html) - 活動日誌

## 使用流程示例

### 場景1: 處理新訂單

1. 登入管理員帳戶
2. 進入儀表板,查看"待處理訂單"數量
3. 點擊"待處理訂單"或進入訂單管理
4. 找到新訂單,點擊"查看"
5. 檢查訂單詳情
6. 確認庫存和配送信息
7. 更新訂單狀態為"處理中"
8. 確認收款後,更新支付狀態為"已支付"
9. 發貨後,更新訂單狀態為"已發貨"

### 場景2: 查看客戶購買歷史

1. 進入客戶管理
2. 搜索或找到目標客戶
3. 點擊客戶名稱進入詳情頁
4. 查看"訂單記錄"區域
5. 點擊訂單編號查看訂單詳情

### 場景3: 監控用戶活動

1. 進入活動日誌
2. 選擇時間範圍(例如:今天)
3. 選擇活動類型(例如:VIEW_PRODUCT)
4. 查看哪些商品被瀏覽最多
5. 根據數據優化商品推薦

### 場景4: 處理問題客戶

1. 接到客戶投訴
2. 進入客戶管理,搜索客戶
3. 查看客戶詳情和訂單歷史
4. 檢查活動日誌了解行為模式
5. 如需要,可暫時停用帳戶
6. 處理完畢後重新啟用

## 安全注意事項

1. **角色權限:**
   - 只有 ADMIN 角色可訪問
   - 定期審查管理員帳戶

2. **操作記錄:**
   - 所有管理員操作應被記錄
   - 定期檢查異常操作

3. **數據保護:**
   - 不要在不安全的網絡環境下訪問
   - 定期更改管理員密碼
   - 保護客戶隱私數據

4. **帳戶管理:**
   - 謹慎使用停用帳戶功能
   - 停用前應通知客戶
   - 記錄停用原因

## 常見問題

**Q: 如何創建新的管理員帳戶?**
A: 在 [`DataInitializer.java`](../src/main/java/com/onlineshop/config/DataInitializer.java) 中添加,或在數據庫中直接創建並設置角色為 ADMIN。

**Q: 客戶看不到訂單狀態更新怎麼辦?**
A: 檢查是否正確保存了狀態更新,並確保前端頁面有刷新。

**Q: 活動日誌顯示不完整?**
A: 活動日誌預設顯示最近100條記錄,可調整篩選條件查看更多。

**Q: 如何導出數據?**
A: 當前版本暫不支持導出,可通過數據庫直接查詢導出。

## 未來改進建議

1. **批量操作:**
   - 批量更新訂單狀態
   - 批量導出數據

2. **高級報表:**
   - 銷售趨勢圖表
   - 客戶分析報表
   - 商品表現分析

3. **通知系統:**
   - 新訂單通知
   - 庫存預警
   - 異常活動警報

4. **數據導出:**
   - CSV/Excel 導出
   - PDF 報表生成

5. **審計日誌:**
   - 管理員操作記錄
   - 數據變更追蹤

## 相關文檔

- [系統架構文檔](../PROJECT_STRUCTURE.md)
- [快速開始指南](../QUICK_START.md)
- [圖片存儲指南](IMAGE_STORAGE_GUIDE.md)
- [本地資源配置](LOCAL_RESOURCES.md)