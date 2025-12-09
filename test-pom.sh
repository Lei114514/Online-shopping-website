#!/bin/bash

# 測試pom.xml文件

echo "=== 測試pom.xml文件 ==="
echo ""

# 1. 檢查文件是否存在
if [ ! -f "pom.xml" ]; then
    echo "錯誤: pom.xml文件不存在"
    exit 1
fi

echo "1. 檢查文件大小..."
FILE_SIZE=$(wc -c < pom.xml)
echo "   文件大小: $FILE_SIZE 字節"
echo ""

# 2. 檢查文件開頭
echo "2. 檢查文件開頭..."
HEAD_CONTENT=$(head -c 50 pom.xml)
echo "   文件開頭(前50字節): $HEAD_CONTENT"
echo ""

# 3. 檢查文件結尾
echo "3. 檢查文件結尾..."
TAIL_CONTENT=$(tail -c 50 pom.xml)
echo "   文件結尾(後50字節): $TAIL_CONTENT"
echo ""

# 4. 檢查是否包含雙引號
echo "4. 檢查是否包含雙引號..."
if grep -q '^".*"$' pom.xml; then
    echo "   警告: 文件被雙引號包圍"
    echo "   修復方法: sed -i 's/^"//; s/"$//' pom.xml"
else
    echo "   正常: 文件沒有被雙引號包圍"
fi
echo ""

# 5. 使用Maven驗證
if command -v mvn &> /dev/null; then
    echo "5. 使用Maven驗證pom.xml..."
    mvn validate -q
    if [ $? -eq 0 ]; then
        echo "   ✓ pom.xml語法正確"
    else
        echo "   ✗ pom.xml語法錯誤"
        echo "   錯誤信息:"
        mvn validate
    fi
else
    echo "5. Maven未安裝，跳過驗證"
fi
echo ""

echo "=== 測試完成 ==="
