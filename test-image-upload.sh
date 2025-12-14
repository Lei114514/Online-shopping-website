#!/bin/bash

echo "=== 圖片上傳測試腳本 ==="
echo ""

# 檢查目錄
echo "1. 檢查圖片目錄:"
UPLOAD_DIR="src/main/resources/static/images/products"
if [ -d "$UPLOAD_DIR" ]; then
    echo "   ✓ 目錄存在: $UPLOAD_DIR"
    echo "   絕對路徑: $(cd $UPLOAD_DIR && pwd)"
    echo "   權限: $(ls -ld $UPLOAD_DIR | awk '{print $1}')"
    echo "   所有者: $(ls -ld $UPLOAD_DIR | awk '{print $3":"$4}')"
else
    echo "   ✗ 目錄不存在: $UPLOAD_DIR"
    echo "   正在創建目錄..."
    mkdir -p "$UPLOAD_DIR"
    echo "   ✓ 目錄已創建"
fi

echo ""
echo "2. 當前目錄中的圖片:"
ls -lh "$UPLOAD_DIR"/*.jpg "$UPLOAD_DIR"/*.png "$UPLOAD_DIR"/*.gif 2>/dev/null || echo "   沒有圖片文件"

echo ""
echo "3. 檢查應用配置:"
if [ -f "src/main/resources/application.properties" ]; then
    echo "   upload.path 配置:"
    grep "upload.path" src/main/resources/application.properties || echo "   未找到 upload.path 配置"
fi

echo ""
echo "4. 測試寫入權限:"
TEST_FILE="$UPLOAD_DIR/test-$(date +%s).txt"
if touch "$TEST_FILE" 2>/dev/null; then
    echo "   ✓ 目錄可寫"
    rm "$TEST_FILE"
else
    echo "   ✗ 目錄不可寫"
    echo "   嘗試修復權限..."
    chmod 755 "$UPLOAD_DIR"
    if touch "$TEST_FILE" 2>/dev/null; then
        echo "   ✓ 權限已修復"
        rm "$TEST_FILE"
    else
        echo "   ✗ 無法修復權限"
    fi
fi

echo ""
echo "5. 檢查應用是否正在運行:"
if pgrep -f "onlineshop" > /dev/null; then
    echo "   ✓ 應用正在運行"
    echo "   進程 ID: $(pgrep -f "onlineshop")"
else
    echo "   ✗ 應用未運行"
fi

echo ""
echo "6. 查看最近的應用日誌 (如果有):"
if [ -f "logs/application.log" ]; then
    echo "   最後 10 行日誌:"
    tail -10 logs/application.log
else
    echo "   未找到日誌文件"
fi

echo ""
echo "=== 測試完成 ==="
echo ""
echo "建議操作:"
echo "1. 確保應用正在運行"
echo "2. 嘗試上傳圖片"
echo "3. 查看控制台輸出的詳細日誌"
echo "4. 再次運行此腳本檢查圖片是否已保存"