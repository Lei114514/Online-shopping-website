#!/bin/bash

echo "=== 重啟 Docker 容器並掛載本地圖片目錄 ==="
echo ""

# 停止並刪除現有容器
echo "1. 停止現有容器..."
docker-compose down

echo ""
echo "2. 從容器中複製現有圖片到本地..."
# 檢查容器是否還在運行
if docker ps -a | grep -q "online-shop-app"; then
    CONTAINER_ID=$(docker ps -a | grep "online-shop-app" | awk '{print $1}')
    echo "   找到容器: $CONTAINER_ID"
    
    # 創建本地目錄
    mkdir -p src/main/resources/static/images/products
    
    # 複製圖片
    docker cp $CONTAINER_ID:/app/src/main/resources/static/images/products/. src/main/resources/static/images/products/ 2>/dev/null || echo "   沒有圖片需要複製"
else
    echo "   容器已停止"
fi

echo ""
echo "3. 確保本地目錄存在..."
mkdir -p src/main/resources/static/images/products
echo "   ✓ 目錄已創建: src/main/resources/static/images/products"

echo ""
echo "4. 重新啟動容器..."
docker-compose up -d --build

echo ""
echo "5. 等待容器啟動..."
sleep 5

echo ""
echo "6. 檢查容器狀態..."
docker-compose ps

echo ""
echo "7. 驗證目錄掛載..."
CONTAINER_ID=$(docker ps | grep "online-shop-app" | awk '{print $1}')
if [ -n "$CONTAINER_ID" ]; then
    echo "   容器內的圖片:"
    docker exec $CONTAINER_ID ls -la /app/src/main/resources/static/images/products/ 2>/dev/null || echo "   目錄為空或不存在"
    
    echo ""
    echo "   本地的圖片:"
    ls -la src/main/resources/static/images/products/
else
    echo "   ✗ 容器未運行"
fi

echo ""
echo "=== 完成 ==="
echo ""
echo "現在上傳的圖片會自動同步到本地目錄:"
echo "  src/main/resources/static/images/products/"
echo ""
echo "您可以運行以下命令查看日誌:"
echo "  ./view-docker-logs.sh"