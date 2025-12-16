# 帳號注銷功能指南

## 功能概述

本系統提供完整的帳號注銷功能，允許用戶自主注銷其帳號。注銷採用軟刪除方式，保留用戶的訂單歷史和相關數據，但禁止該帳號再次登錄。

## 功能特點

### 1. 安全的注銷流程
- **密碼驗證**：注銷前必須輸入當前密碼進行身份驗證
- **二次確認**：使用 Bootstrap Modal 彈窗進行二次確認，防止誤操作
- **即時登出**：注銷成功後立即清除會話並登出
- **禁止再登錄**：被注銷的帳號無法再次登錄系統

### 2. 用戶體驗優化
- **便捷入口**：在頁面右上角用戶下拉菜單中提供注銷選項
- **清晰提示**：每個步驟都有明確的提示信息
- **友好反饋**：操作成功或失敗都會顯示相應的提示消息
- **自動跳轉**：注銷後自動跳轉到首頁（/home）

### 3. 數據保護
- **軟刪除**：只禁用帳號（enabled = false），不刪除用戶數據
- **訂單保留**：用戶的歷史訂單和交易記錄完整保留
- **可恢復性**：管理員可以重新啟用被注銷的帳號

## 使用方法

### 用戶端操作

1. **訪問注銷功能**
   - 登錄系統後，點擊頁面右上角的用戶名
   - 在下拉菜單中選擇「注銷帳號」選項

2. **確認注銷**
   - 系統會彈出確認對話框
   - 仔細閱讀注銷說明和警告信息
   - 在密碼輸入框中輸入當前帳號密碼
   - 點擊「確認注銷」按鈕

3. **完成注銷**
   - 系統驗證密碼正確後執行注銷
   - 自動登出當前會話
   - 跳轉到首頁並顯示成功消息
   - 該帳號將無法再次登錄

### 管理員操作

管理員可以通過後台管理界面重新啟用被注銷的帳號：

1. 訪問客戶管理頁面
2. 找到被禁用的用戶（enabled = false）
3. 修改用戶狀態為啟用（enabled = true）
4. 用戶即可重新登錄系統

## 技術實現

### 後端實現

#### 1. UserService.java

```java
/**
 * 注銷帳號（軟刪除）
 * @param userId 用戶ID
 * @param password 用戶密碼（用於驗證）
 */
public void deactivateAccount(Long userId, String password) {
    User user = getUserById(userId);
    
    // 驗證密碼
    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new IllegalArgumentException("密碼不正確，無法注銷帳號");
    }
    
    // 禁用帳號
    user.setEnabled(false);
    userRepository.save(user);
}
```

#### 2. UserController.java

```java
@PostMapping("/users/deactivate")
public String deactivateAccount(
        @RequestParam String password,
        HttpServletRequest request,
        HttpServletResponse response,
        RedirectAttributes redirectAttributes) {
    try {
        // 獲取當前登錄用戶
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        
        // 注銷帳號
        userService.deactivateAccount(user.getId(), password);
        
        // 完全清除會話並登出
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.setInvalidateHttpSession(true);
        logoutHandler.setClearAuthentication(true);
        logoutHandler.logout(request, response, auth);
        
        // 清除 SecurityContext
        SecurityContextHolder.clearContext();
        
        redirectAttributes.addFlashAttribute("successMessage",
            "您的帳號已成功注銷。如需重新啟用，請聯繫管理員。");
        return "redirect:/home";
        
    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return "redirect:/";
    }
}
```

#### 3. CustomUserDetailsService.java

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("用戶名或密碼錯誤"));
    
    // 檢查用戶是否啟用
    if (!user.getEnabled()) {
        throw new UsernameNotFoundException("該帳號已被注銷，無法登錄。如需重新啟用，請聯繫管理員。");
    }
    
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        user.getEnabled(),  // 啟用狀態
        true,   // 帳號未過期
        true,   // 憑證未過期
        true,   // 帳號未鎖定
        getAuthorities(user)
    );
}
```

### 前端實現

#### layout.html - 用戶菜單

```html
<li sec:authorize="isAuthenticated()">
    <a class="dropdown-item text-danger" href="#" data-bs-toggle="modal" data-bs-target="#deactivateModal">
        <i class="fas fa-user-times me-2"></i>注銷帳號
    </a>
</li>
```

#### layout.html - 確認彈窗

```html
<div class="modal fade" id="deactivateModal" tabindex="-1" sec:authorize="isAuthenticated()">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">
                    <i class="fas fa-exclamation-triangle me-2"></i>確認注銷帳號
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="alert alert-warning">
                    <i class="fas fa-info-circle me-2"></i>
                    <strong>注意：</strong>注銷帳號後，您將無法再次登錄此帳號。
                    如需重新啟用，請聯繫管理員。
                </div>
                <p class="mb-3">請輸入您的密碼以確認注銷：</p>
                <form id="deactivateForm" action="/users/deactivate" method="post">
                    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
                    <div class="mb-3">
                        <label for="deactivatePassword" class="form-label">密碼</label>
                        <input type="password" class="form-control" id="deactivatePassword" 
                               name="password" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="button" class="btn btn-danger" id="confirmDeactivate">確認注銷</button>
            </div>
        </div>
    </div>
</div>
```

#### JavaScript 處理

```javascript
document.getElementById('confirmDeactivate')?.addEventListener('click', function() {
    const password = document.getElementById('deactivatePassword').value;
    if (!password) {
        alert('請輸入密碼');
        return;
    }
    
    if (confirm('您確定要注銷帳號嗎？此操作不可撤銷！')) {
        document.getElementById('deactivateForm').submit();
    }
});
```

## 安全考慮

### 1. 密碼驗證
- 使用 BCrypt 加密算法驗證密碼
- 防止未授權的帳號注銷操作

### 2. 會話管理
- 注銷後立即清除所有會話數據
- 使用 `SecurityContextLogoutHandler` 確保完全登出
- 清除 `SecurityContext` 防止會話殘留

### 3. CSRF 保護
- 表單包含 CSRF token
- 防止跨站請求偽造攻擊

### 4. 權限控制
- 只有已登錄用戶才能看到注銷選項
- 使用 `sec:authorize="isAuthenticated()"` 控制顯示

### 5. 登錄驗證
- 被注銷的帳號嘗試登錄時會收到明確的錯誤提示
- 使用 `UsernameNotFoundException` 阻止登錄

## 數據庫設計

### User 表結構

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,  -- 帳號啟用狀態
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 注銷狀態
- `enabled = true`：帳號正常，可以登錄
- `enabled = false`：帳號已注銷，無法登錄

## 測試場景

### 1. 正常注銷流程
1. 登錄系統
2. 點擊用戶菜單中的「注銷帳號」
3. 輸入正確密碼
4. 確認注銷
5. 驗證：
   - 自動登出
   - 跳轉到首頁
   - 顯示成功消息
   - 無法再次登錄

### 2. 密碼錯誤
1. 嘗試注銷帳號
2. 輸入錯誤密碼
3. 驗證：
   - 顯示錯誤消息
   - 帳號未被注銷
   - 仍然保持登錄狀態

### 3. 被注銷帳號嘗試登錄
1. 使用被注銷的帳號登錄
2. 驗證：
   - 登錄失敗
   - 顯示「該帳號已被注銷，無法登錄」消息

### 4. 管理員重新啟用
1. 管理員登錄後台
2. 找到被禁用的用戶
3. 修改 enabled 狀態為 true
4. 驗證：
   - 用戶可以重新登錄

## 常見問題

### Q1: 注銷後能否恢復？
A: 可以。注銷採用軟刪除方式，管理員可以通過後台重新啟用帳號。

### Q2: 注銷後訂單數據會丟失嗎？
A: 不會。注銷只是禁用帳號，所有歷史數據都會保留。

### Q3: 忘記密碼能否注銷？
A: 不能。必須輸入正確的密碼才能注銷帳號，這是為了防止未授權操作。

### Q4: 注銷後會自動登出嗎？
A: 是的。注銷成功後系統會立即清除會話並登出，然後跳轉到首頁。

### Q5: 被注銷的帳號嘗試登錄會看到什麼？
A: 會看到錯誤消息：「該帳號已被注銷，無法登錄。如需重新啟用，請聯繫管理員。」

## 相關文件

- **後端服務**：`src/main/java/com/onlineshop/service/UserService.java`
- **控制器**：`src/main/java/com/onlineshop/controller/UserController.java`
- **認證服務**：`src/main/java/com/onlineshop/security/CustomUserDetailsService.java`
- **前端模板**：`src/main/resources/templates/layout.html`
- **用戶模型**：`src/main/java/com/onlineshop/model/User.java`

## 更新日誌

### v1.1 (2025-12-16)
- 修復注銷後跳轉路徑，從 `/login` 改為 `/home`
- 增強會話清除邏輯，確保完全登出
- 改進被禁用帳號的登錄錯誤提示
- 優化 `CustomUserDetailsService` 的帳號狀態檢查

### v1.0 (初始版本)
- 實現基本的帳號注銷功能
- 添加密碼驗證和二次確認
- 實現軟刪除機制
- 創建用戶界面和確認彈窗