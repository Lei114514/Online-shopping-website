# 安全配置指南

本文檔說明項目中的敏感信息配置方式，以及如何安全地推送代碼到 GitHub。

## 敏感文件清單

以下文件包含敏感信息，**不應該推送到 GitHub**：

| 文件 | 內容 | 狀態 |
|------|------|------|
| `.env` | 郵箱帳號和授權碼 | ✅ 已在 .gitignore 中排除 |

## 已安全配置的文件

以下文件使用環境變量佔位符，**可以安全推送**：

| 文件 | 說明 |
|------|------|
| `application.properties` | 使用 `${MAIL_USERNAME}` 和 `${MAIL_PASSWORD}` 環境變量 |
| `docker-compose.yml` | 使用 `${MAIL_USERNAME:-default}` 語法讀取 .env |
| `.env.example` | 範例配置文件，不含真實憑證 |

## 環境變量配置

### 1. 本地開發環境

複製範例文件並填入真實憑證：

```bash
cp .env.example .env
```

編輯 `.env` 文件：

```env
MAIL_USERNAME=your-real-email@163.com
MAIL_PASSWORD=your-real-authorization-code
```

### 2. Docker 環境

Docker Compose 會自動讀取 `.env` 文件中的變量。

### 3. 生產環境

在生產環境中，可以通過以下方式設置環境變量：

**方式一：系統環境變量**
```bash
export MAIL_USERNAME=your-email@163.com
export MAIL_PASSWORD=your-authorization-code
```

**方式二：Docker 運行時參數**
```bash
docker run -e MAIL_USERNAME=your-email@163.com -e MAIL_PASSWORD=your-code ...
```

**方式三：Kubernetes Secrets**
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mail-credentials
type: Opaque
data:
  MAIL_USERNAME: base64-encoded-email
  MAIL_PASSWORD: base64-encoded-password
```

## 配置文件說明

### application.properties

郵件配置使用環境變量：

```properties
spring.mail.username=${MAIL_USERNAME:your-email@163.com}
spring.mail.password=${MAIL_PASSWORD:your-authorization-code}
```

- `${MAIL_USERNAME:default}` 語法表示：
  - 優先使用環境變量 `MAIL_USERNAME`
  - 如果環境變量不存在，使用默認值 `your-email@163.com`

### docker-compose.yml

```yaml
environment:
  MAIL_USERNAME: ${MAIL_USERNAME:-your-email@163.com}
  MAIL_PASSWORD: ${MAIL_PASSWORD:-your-authorization-code}
```

## 推送到 GitHub 前的檢查清單

在推送代碼之前，請確認：

- [ ] `.env` 文件已在 `.gitignore` 中（已配置）
- [ ] 沒有在代碼中硬編碼任何密碼或授權碼
- [ ] `application.properties` 中的敏感配置使用環境變量
- [ ] 運行 `git status` 確認 `.env` 不在待提交列表中

### 驗證 .gitignore 是否生效

```bash
# 檢查 .env 是否被忽略
git check-ignore .env
# 如果輸出 .env，表示已被正確忽略

# 查看待提交的文件
git status
# 確認 .env 不在列表中
```

## 如果不小心提交了敏感信息

如果您不小心將 `.env` 或其他敏感文件提交到了 Git：

### 1. 從 Git 歷史中移除

```bash
# 從所有歷史記錄中移除 .env
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch .env' \
  --prune-empty --tag-name-filter cat -- --all

# 強制推送
git push origin --force --all
```

### 2. 更換憑證

**重要**：即使從 Git 歷史中移除了文件，如果已經推送到公開倉庫，您應該：

1. 立即更換 163 郵箱的授權碼
2. 檢查是否有異常登錄或郵件發送

## 163 郵箱授權碼獲取方式

1. 登錄 163 郵箱 (mail.163.com)
2. 進入「設置」->「POP3/SMTP/IMAP」
3. 開啟「POP3/SMTP服務」
4. 點擊「新增授權密碼」
5. 按照提示完成手機驗證
6. 獲取授權碼（16位字母）

**注意**：授權碼不是您的郵箱登錄密碼！

## 其他安全建議

1. **定期更換授權碼**：建議每 3-6 個月更換一次
2. **使用專用郵箱**：建議為應用創建專用的發送郵箱
3. **監控郵件發送**：定期檢查郵箱的發送記錄
4. **限制發送頻率**：在代碼中實現發送頻率限制，防止濫用

## 相關文件

- [`.env.example`](../.env.example) - 環境變量範例
- [`application.properties`](../src/main/resources/application.properties) - 應用配置
- [`docker-compose.yml`](../docker-compose.yml) - Docker 配置
- [`.gitignore`](../.gitignore) - Git 忽略規則