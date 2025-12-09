#!/bin/bash

# 線上購物系統構建腳本

set -e

echo "=== 開始構建線上購物系統 ==="

# 檢查Java版本
JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
echo "Java版本: $JAVA_VERSION"

# 檢查Maven
if ! command -v mvn &> /dev/null; then
    echo "錯誤: Maven未安裝"
    exit 1
fi

# 清理並構建
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "=== 構建成功 ==="
    echo "JAR文件位置: target/online-shop-1.0.0.jar"
    
    # 顯示文件大小
    JAR_SIZE=$(du -h target/online-shop-1.0.0.jar | cut -f1)
    echo "JAR文件大小: $JAR_SIZE"
else
    echo "=== 構建失敗 ==="
    exit 1
fi

# 創建運行目錄
mkdir -p run
cp target/online-shop-1.0.0.jar run/
cp -r src/main/resources/application.properties run/

# 創建上傳目錄
mkdir -p run/uploads/products

echo "=== 構建完成 ==="
echo "運行命令: cd run && java -jar online-shop-1.0.0.jar"
