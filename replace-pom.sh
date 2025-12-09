#!/bin/bash

# 替換pom.xml文件

echo "=== 替換pom.xml文件 ==="
echo ""

# 1. 檢查pom.xml.new是否存在
if [ ! -f "pom.xml.new" ]; then
    echo "錯誤: pom.xml.new文件不存在"
    exit 1
fi

echo "1. 檢查pom.xml.new文件..."
file pom.xml.new

echo ""

# 2. 備份原文件
if [ -f "pom.xml" ]; then
    cp pom.xml pom.xml.backup.$(date +%Y%m%d%H%M%S)
    echo "2. 已創建備份"
fi

echo ""

# 3. 複製新文件
cp pom.xml.new pom.xml

echo "3. 已替換pom.xml文件"
echo ""

# 4. 驗證新文件
echo "4. 驗證新文件..."
if [ -f "pom.xml" ]; then
    echo "   ✓ pom.xml文件存在"
    FILE_SIZE=$(wc -c < pom.xml)
    echo "   文件大小: $FILE_SIZE 字節"
    
    # 檢查文件開頭
    FIRST_CHAR=$(head -c 1 pom.xml)
    if [ "$FIRST_CHAR" = "<" ]; then
        echo "   ✓ 文件以'<'開頭（正確）"
    else
        echo "   ✗ 文件不以'<'開頭（可能還有問題）"
        echo "   第一個字符: '$FIRST_CHAR'"
    fi
else
    echo "   ✗ pom.xml文件不存在"
fi

echo ""

echo "=== 替換完成 ==="
echo "現在可以運行: ./docker-run.sh"

# 清理臨時文件
rm -f pom.xml.new test-pom.sh fix-pom.sh
