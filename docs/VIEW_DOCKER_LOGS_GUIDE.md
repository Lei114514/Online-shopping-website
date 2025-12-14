# Docker 容器日誌查看指南

## 快速開始

### 方法一: 使用提供的腳本 (推薦)

```bash
./view-docker-logs.sh
```

這個腳本提供了多種查看選項:
1. 查看最後 50 行日誌
2. 查看最後 100 行日誌
3. 實時跟蹤日誌 (Ctrl+C 退出)
4. 搜索包含 '圖片' 或 'image' 的日誌
5. 搜索包含 '錯誤' 或 'error' 的日誌
6. 查看完整日誌

### 方法二: 使用 Docker 命令

#### 1. 查看運行中的容器

```bash
docker ps
```

輸出示例:
```
CONTAINER ID   IMAGE                    NAMES                STATUS
abc123def456   onlineshopping-app      onlineshopping-app   Up 5 minutes
```

#### 2. 查看容器日誌

```bash
# 查看最後 50 行
docker logs --tail 50 <容器ID或名稱>

# 查看最後 100 行
docker logs --tail 100 <容器ID或名稱>

# 實時跟蹤日誌
docker logs -f <容器ID或名稱>

# 查看完整日誌
docker logs <容器ID或名稱>
```

#### 3. 搜索特定內容

```bash
# 搜索圖片上傳相關日誌
docker logs <容器ID> 2>&1 | grep -i "image"
docker logs <容器ID> 2>&1 | grep -i "圖片"
docker logs <容器ID> 2>&1 | grep "saveProductImage"
docker logs <容器ID> 2>&1 | grep "createProduct"

# 搜索錯誤日誌
docker logs <容器ID> 2>&1 | grep -i "error"
docker logs <容器ID> 2>&1 | grep -i "exception"
docker logs <容器ID> 2>&1 | grep "錯誤"
docker logs <容器ID> 2>&1 | grep "失敗"
```

## 查看圖片上傳日誌

### 上傳圖片時應該看到的日誌

當您上傳圖片時,應該會看到類似以下的日誌:

```
=== 創建商品開始 ===
imageFile is null: false
imageFile isEmpty: false
imageFile name: product.jpg
imageFile size: 123456
imageFile contentType: image/jpeg

=== saveProductImage 開始 ===
Content Type: image/jpeg
File size: 123456 bytes
Original filename: product.jpg
File extension: .jpg
Generated filename: a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
Upload path from config: src/main/resources/static/images/products
Absolute upload directory: /home/lei/project/onlineShopping/src/main/resources/static/images/products
Directory exists: true
Directory is writable: true
Full file path: /home/lei/project/onlineShopping/src/main/resources/static/images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
File copied successfully
File exists after save: true
File size after save: 123456 bytes
Image URL: /images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
=== saveProductImage 完成 ===

圖片上傳成功,URL: /images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
=== 創建商品完成 ===
```

### 如果沒有看到這些日誌

可能的原因:
1. **imageFile 為 null**: 表單沒有正確提交文件
2. **imageFile isEmpty**: 選擇的文件為空
3. **沒有任何日誌**: 請求沒有到達控制器

## 常見問題排查

### 1. 找不到容器

```bash
# 檢查所有容器(包括停止的)
docker ps -a

# 如果沒有運行中的容器,啟動應用
./docker-run.sh
```

### 2. 日誌太多,難以查找

```bash
# 只查看最近的日誌
docker logs --tail 100 --since 5m <容器ID>

# 只查看特定時間後的日誌
docker logs --since "2024-12-14T13:00:00" <容器ID>

# 保存日誌到文件
docker logs <容器ID> > logs.txt 2>&1
```

### 3. 實時監控日誌

```bash
# 實時跟蹤,只顯示新日誌
docker logs -f --tail 0 <容器ID>

# 在另一個終端上傳圖片,觀察日誌輸出
```

### 4. 查看容器內部文件

```bash
# 進入容器
docker exec -it <容器ID> /bin/bash

# 查看圖片目錄
ls -la /app/src/main/resources/static/images/products/

# 檢查目錄權限
ls -ld /app/src/main/resources/static/images/products/

# 退出容器
exit
```

## 使用 docker-compose 查看日誌

如果使用 docker-compose:

```bash
# 查看所有服務的日誌
docker-compose logs

# 查看特定服務的日誌
docker-compose logs app

# 實時跟蹤日誌
docker-compose logs -f app

# 查看最後 50 行
docker-compose logs --tail 50 app
```

## 日誌分析技巧

### 1. 按時間過濾

```bash
# 查看最近 10 分鐘的日誌
docker logs --since 10m <容器ID>

# 查看特定時間範圍
docker logs --since "2024-12-14T13:00:00" --until "2024-12-14T14:00:00" <容器ID>
```

### 2. 使用 grep 過濾

```bash
# 查找包含特定關鍵字的行
docker logs <容器ID> 2>&1 | grep "關鍵字"

# 查找多個關鍵字(OR)
docker logs <容器ID> 2>&1 | grep -E "關鍵字1|關鍵字2"

# 排除某些行
docker logs <容器ID> 2>&1 | grep -v "排除的內容"

# 顯示匹配行的前後幾行
docker logs <容器ID> 2>&1 | grep -A 5 -B 5 "關鍵字"
```

### 3. 統計和分析

```bash
# 統計錯誤數量
docker logs <容器ID> 2>&1 | grep -i "error" | wc -l

# 查找最常見的錯誤
docker logs <容器ID> 2>&1 | grep -i "error" | sort | uniq -c | sort -rn

# 按時間戳排序
docker logs <容器ID> --timestamps 2>&1 | sort
```

## 保存和分享日誌

### 保存完整日誌

```bash
# 保存到文件
docker logs <容器ID> > application.log 2>&1

# 保存最近的日誌
docker logs --tail 1000 <容器ID> > recent.log 2>&1

# 保存並壓縮
docker logs <容器ID> 2>&1 | gzip > logs-$(date +%Y%m%d).log.gz
```

### 分享日誌片段

```bash
# 只保存圖片上傳相關的日誌
docker logs <容器ID> 2>&1 | grep -A 20 "創建商品開始" > upload-logs.txt

# 只保存錯誤日誌
docker logs <容器ID> 2>&1 | grep -i "error\|exception" > errors.txt
```

## 調試圖片上傳的完整流程

### 步驟 1: 啟動應用並監控日誌

```bash
# 終端 1: 啟動應用
./docker-run.sh

# 終端 2: 實時監控日誌
./view-docker-logs.sh
# 選擇選項 3 (實時跟蹤)
```

### 步驟 2: 上傳圖片

1. 打開瀏覽器訪問 `http://localhost:8080`
2. 登錄商家帳戶
3. 進入商品管理
4. 創建新商品並上傳圖片
5. 觀察終端 2 的日誌輸出

### 步驟 3: 分析日誌

查找以下關鍵信息:
- `imageFile is null`: 是否為 false
- `imageFile isEmpty`: 是否為 false
- `File copied successfully`: 是否出現
- `File exists after save`: 是否為 true
- 任何錯誤或異常信息

### 步驟 4: 驗證文件

```bash
# 檢查圖片是否已保存
./test-image-upload.sh

# 或手動檢查
ls -lh src/main/resources/static/images/products/
```

### 步驟 5: 測試訪問

```bash
# 從日誌中找到圖片 URL,例如:
# Image URL: /images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg

# 測試訪問
curl -I http://localhost:8080/images/products/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
```

## 常用命令速查

```bash
# 查看運行中的容器
docker ps

# 查看最後 50 行日誌
docker logs --tail 50 <容器ID>

# 實時跟蹤日誌
docker logs -f <容器ID>

# 搜索圖片上傳日誌
docker logs <容器ID> 2>&1 | grep -i "image\|圖片"

# 搜索錯誤日誌
docker logs <容器ID> 2>&1 | grep -i "error\|錯誤"

# 進入容器
docker exec -it <容器ID> /bin/bash

# 查看容器內的圖片目錄
docker exec <容器ID> ls -la /app/src/main/resources/static/images/products/

# 停止容器
docker stop <容器ID>

# 重啟容器
docker restart <容器ID>
```

## 相關文件

- 日誌查看腳本: [`view-docker-logs.sh`](../view-docker-logs.sh)
- 圖片上傳測試腳本: [`test-image-upload.sh`](../test-image-upload.sh)
- 圖片上傳存儲指南: [`IMAGE_UPLOAD_STORAGE.md`](IMAGE_UPLOAD_STORAGE.md)
- Docker 運行腳本: [`docker-run.sh`](../docker-run.sh)

## 獲取幫助

如果遇到問題,請提供以下信息:

1. **容器狀態**:
   ```bash
   docker ps -a
   ```

2. **最近的日誌**:
   ```bash
   docker logs --tail 100 <容器ID> > logs.txt 2>&1
   ```

3. **圖片上傳相關日誌**:
   ```bash
   docker logs <容器ID> 2>&1 | grep -A 20 "創建商品開始" > upload-logs.txt
   ```

4. **錯誤日誌**:
   ```bash
   docker logs <容器ID> 2>&1 | grep -i "error\|exception" > errors.txt
   ```

5. **目錄狀態**:
   ```bash
   ./test-image-upload.sh > test-result.txt
   ```

將這些文件分享給開發人員以獲得幫助。