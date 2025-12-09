#!/bin/bash

# 修復pom.xml文件

echo "=== 修復pom.xml文件 ==="
echo ""

# 備份原文件
if [ -f "pom.xml" ]; then
    cp pom.xml pom.xml.backup
    echo "1. 已創建備份: pom.xml.backup"
fi

echo ""
echo "2. 檢查文件編碼..."
file pom.xml

echo ""
echo "3. 顯示文件前100個字符..."
head -c 100 pom.xml | od -c

echo ""
echo "4. 修復文件..."
# 移除開頭和結尾的雙引號
sed -i 's/^"//' pom.xml
sed -i 's/"$//' pom.xml

# 移除可能的轉義字符
sed -i 's/\\"/"/g' pom.xml

# 移除開頭可能的BOM字符
sed -i '1s/^\xEF\xBB\xBF//' pom.xml

echo "   修復完成"
echo ""

echo "5. 修復後的文件前100個字符..."
head -c 100 pom.xml | od -c

echo ""
echo "=== 修復完成 ==="
echo "現在可以運行: ./docker-run.sh"
