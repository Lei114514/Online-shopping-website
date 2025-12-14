# 中文字符編碼問題解決方案

## 問題描述

在 Docker 環境中,產品分類的中文名稱顯示為亂碼(如 `é››遙"¢å"` 而不是 "電子產品")。

## 根本原因

1. **Docker 構建過程中的編碼問題**:在 Docker 容器內編譯 Java 源代碼時,中文字符被錯誤地轉換
2. **數據持久化**:即使使用 `docker compose down -v`,舊的亂碼數據可能仍然存在於數據庫中
3. **初始化邏輯**:DataInitializer 會檢查數據庫中是否已有數據,如果有則跳過初始化

## 解決方案

### 1. 使用 Unicode 轉義序列

在 Java 源代碼中直接使用中文字符會在 Docker 構建時產生編碼問題。解決方法是使用 Unicode 轉義序列:

```java
// 錯誤方式 - 中文字符會在Docker 構建時變成亂碼
electronics.setName("電子產品");

// 正確方式 - 使用 Unicode 轉義序列
electronics.setName("\u96FB\u5B50\u7522\u54C1");  // 電子產品
```

### 2. 完全重置數據庫

要確保舊數據被完全清除,需要執行以下步驟:

```bash
# 1. 停止並刪除所有容器和卷
docker compose down -v

# 2. 清理 Docker 系統(可選,但推薦)
docker system prune -f

# 3. 重新構建並啟動
./docker-run.sh
```

### 3. 強制重新初始化數據

如果數據庫已經存在但需要重新初始化,可以修改 [`DataInitializer.java`](../src/main/java/com/onlineshop/config/DataInitializer.java) 的檢查邏輯:

```java
// 臨時註釋掉這個檢查以強制重新初始化
/*
if (productRepository.count() > 0) {
    System.out.println("數據庫已有數據,跳過初始化");
    return;
}
*/
```

重新構建後再恢復這個檢查。

### 4. 直接清除數據庫表

也可以通過 SQL 命令直接清除數據:

```bash
docker compose exec mysql mysql -uroot -prootpassword onlineshop -e "
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE products;
TRUNCATE TABLE categories;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;"

# 然後重啟應用以觸發初始化
docker compose restart app
```

## 當前實現

當前代碼已經在 [`DataInitializer.java`](../src/main/java/com/onlineshop/config/DataInitializer.java) 中使用 Unicode 轉義序列:

```java
// 電子產品
Category electronics = new Category();
electronics.setName("\u96FB\u5B50\u7522\u54C1");
electronics.setDescription("\u667A\u80FD\u624B\u6A5F\u3001\u96FB\u8166\u53CA\u5176\u4ED6\u96FB\u5B50\u8A2D\u5099");

// 配件
Category accessories = new Category();
accessories.setName("\u914D\u4EF6");
accessories.setDescription("\u96FB\u5B50\u7522\u54C1\u914D\u4EF6");

// 服裝
Category clothing = new Category();
clothing.setName("\u670D\u88DD");
clothing.setDescription("\u6642\u5C1A\u670D\u98FE");

// 書籍
Category books = new Category();
books.setName("\u66F8\u7C4D");
books.setDescription("\u66F8\u7C4D\u548C\u51FA\u7248\u7269");

// 家居用品
Category home = new Category();
home.setName("\u5BB6\u5C45\u7528\u54C1");
home.setDescription("\u5BB6\u96FB\u548C\u751F\u6D3B\u7528\u54C1");
```

## Unicode 轉義序列對照表

| 中文 | Unicode 轉義序列 |
|------|------------------|
| 電子產品 | \u96FB\u5B50\u7522\u54C1 |
| 配件 | \u914D\u4EF6 |
| 服裝 | \u670D\u88DD |
| 書籍 | \u66F8\u7C4D |
| 家居用品 | \u5BB6\u5C45\u7528\u54C1 |

##轉換工具

要將中文字符轉換為 Unicode 轉義序列,可以使用:

1. **在線工具**: https://www.branah.com/unicode-converter
2. **Python腳本**:
```python
text = "電子產品"
unicode_escape = ''.join(f'\\u{ord(c):04X}' for c in text)
print(unicode_escape)
```

3. **Java 代碼**:
```java
String text = "電子產品";
for (char c : text.toCharArray()) {
    System.out.printf("\\u%04X", (int) c);
}
```

##驗證步驟

1. 確保所有中文字符都使用 Unicode 轉義序列
2. 完全清除舊數據(`docker compose down -v`)
3. 重新構建並啟動系統
4. 訪問 http://localhost:8080/products
5. 點擊分類下拉選單驗證中文顯示正確

## 預防措施

1. **避免在 Java 源代碼中直接使用中文**:特別是在 Docker 環境中
2. **使用 properties 文件**:對於大量文本,考慮使用 i18n 資源文件
3. **確保編碼配置**:在 [`application.properties`](../src/main/resources/application.properties) 中設置 UTF-8
4. **數據庫編碼**:確保 MySQL 使用 UTF-8MB4 字符集

## 相關文件

- [`DataInitializer.java`](../src/main/java/com/onlineshop/config/DataInitializer.java) - 數據初始化邏輯
- [`application.properties`](../src/main/resources/application.properties) - 編碼配置
- [`Dockerfile`](../Dockerfile) - Docker 構建配置