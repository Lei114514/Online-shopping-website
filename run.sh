#!/bin/bash

# 線上購物系統運行腳本

set -e

# 顏色定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 函數：打印帶顏色的消息
print_message() {
    echo -e "${2}${1}${NC}"
}

# 檢查是否在正確的目錄
if [ ! -f "pom.xml" ]; then
    print_message "錯誤: 請在項目根目錄運行此腳本" "$RED"
    exit 1
fi

# 檢查Java
if ! command -v java &> /dev/null; then
    print_message "錯誤: Java未安裝" "$RED"
    exit 1
fi

# 檢查MySQL是否運行（可選）
if command -v mysqladmin &> /dev/null; then
    if mysqladmin ping -h localhost -u root --silent 2>/dev/null; then
        print_message "MySQL正在運行" "$GREEN"
    else
        print_message "警告: MySQL未運行，應用程序可能無法連接數據庫" "$YELLOW"
    fi
fi

print_message "=== 啟動線上購物系統 ===" "$GREEN"

# 檢查是否已構建
if [ ! -f "target/online-shop-1.0.0.jar" ]; then
    print_message "未找到JAR文件，開始構建..." "$YELLOW"
    ./build.sh
fi

# 設置環境變量
export SPRING_PROFILES_ACTIVE=dev

# 創建必要的目錄
mkdir -p logs
mkdir -p uploads/products

# 啟動應用程序
print_message "啟動應用程序..." "$GREEN"
print_message "訪問地址: http://localhost:8080" "$GREEN"
print_message "管理員帳號: admin / admin123" "$GREEN"
print_message "銷售帳號: sales / sales123" "$GREEN"
print_message "按 Ctrl+C 停止應用程序" "$YELLOW"

echo ""

# 運行應用程序
java -jar target/online-shop-1.0.0.jar

# 如果應用程序退出
print_message "應用程序已停止" "$YELLOW"
