#!/bin/bash

echo "=== Docker 容器日誌查看工具 ==="
echo ""

# 檢查 Docker 是否運行
if ! docker ps &> /dev/null; then
    echo "❌ Docker 未運行或無權限訪問"
    echo "請確保 Docker 已啟動"
    exit 1
fi

# 查找運行中的容器
echo "1. 查找運行中的容器:"
CONTAINERS=$(docker ps --format "{{.ID}}\t{{.Names}}\t{{.Status}}")

if [ -z "$CONTAINERS" ]; then
    echo "   ❌ 沒有運行中的容器"
    echo ""
    echo "   請先啟動應用:"
    echo "   ./docker-run.sh"
    exit 1
fi

echo "$CONTAINERS" | while IFS=$'\t' read -r id name status; do
    echo "   ✓ 容器 ID: $id"
    echo "     名稱: $name"
    echo "     狀態: $status"
done

echo ""
echo "2. 選擇查看日誌的方式:"
echo "   [1] 查看最後 50 行日誌"
echo "   [2] 查看最後 100 行日誌"
echo "   [3] 實時跟蹤日誌 (Ctrl+C 退出)"
echo "   [4] 搜索包含 '圖片' 或 'image' 的日誌"
echo "   [5] 搜索包含 '錯誤' 或 'error' 的日誌"
echo "   [6] 查看完整日誌"
echo ""

# 獲取第一個容器 ID
CONTAINER_ID=$(docker ps -q | head -1)

read -p "請選擇 (1-6): " choice

case $choice in
    1)
        echo ""
        echo "=== 最後 50 行日誌 ==="
        docker logs --tail 50 $CONTAINER_ID
        ;;
    2)
        echo ""
        echo "=== 最後 100 行日誌 ==="
        docker logs --tail 100 $CONTAINER_ID
        ;;
    3)
        echo ""
        echo "=== 實時跟蹤日誌 (按 Ctrl+C 退出) ==="
        docker logs -f $CONTAINER_ID
        ;;
    4)
        echo ""
        echo "=== 搜索圖片相關日誌 ==="
        docker logs $CONTAINER_ID 2>&1 | grep -i -E "(圖片|image|saveProductImage|createProduct|imageFile)"
        ;;
    5)
        echo ""
        echo "=== 搜索錯誤日誌 ==="
        docker logs $CONTAINER_ID 2>&1 | grep -i -E "(錯誤|error|exception|failed|失敗)"
        ;;
    6)
        echo ""
        echo "=== 完整日誌 ==="
        docker logs $CONTAINER_ID
        ;;
    *)
        echo "無效的選擇"
        exit 1
        ;;
esac

echo ""
echo "=== 完成 ==="