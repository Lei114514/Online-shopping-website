#!/bin/bash

# Docker運行腳本

set -e

# 顏色定義
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 函數：打印錯誤並退出
error_exit() {
    echo -e "${RED}錯誤: $1${NC}" >&2
    exit 1
}

# 函數：打印信息
print_info() {
    echo -e "${GREEN}$1${NC}"
}

# 函數：打印警告
print_warning() {
    echo -e "${YELLOW}$1${NC}"
}

echo -e "${GREEN}=== 使用Docker啟動線上購物系統 ===${NC}"

# 檢查當前目錄
CURRENT_DIR=$(pwd)
print_info "當前目錄: $CURRENT_DIR"

# 檢查必要文件
print_info "檢查必要文件..."
if [ ! -f "docker-compose.yml" ]; then
    error_exit "找不到 docker-compose.yml 文件"
fi

if [ ! -f "Dockerfile" ]; then
    error_exit "找不到 Dockerfile 文件"
fi

if [ ! -f "pom.xml" ]; then
    error_exit "找不到 pom.xml 文件"
fi

if [ ! -d "src" ]; then
    error_exit "找不到 src 目錄"
fi

if [ ! -d "docs" ]; then
    print_warning "警告: 找不到 docs 目錄，將創建..."
    mkdir -p docs
fi

# 檢查Docker
if ! command -v docker &> /dev/null; then
    error_exit "Docker未安裝"
fi

# 檢查Docker Compose（新版本使用 docker compose）
if ! command -v docker compose &> /dev/null; then
    print_warning "警告: docker compose 命令未找到，嘗試使用 docker-compose..."
    if ! command -v docker-compose &> /dev/null; then
        error_exit "Docker Compose未安裝"
    else
        DOCKER_COMPOSE_CMD="docker-compose"
    fi
else
    DOCKER_COMPOSE_CMD="docker compose"
fi

print_info "使用命令: $DOCKER_COMPOSE_CMD"

print_info "1. 構建Docker鏡像..."
$DOCKER_COMPOSE_CMD build

echo ""
print_info "2. 啟動服務..."
$DOCKER_COMPOSE_CMD up -d

echo ""
print_info "=== 服務啟動完成 ==="
echo ""
echo "應用程序: http://localhost:8080"
echo "MySQL數據庫: localhost:3306"
echo ""
echo "管理員帳號: admin / admin123"
echo "銷售帳號: sales / sales123"
echo ""
echo "查看日誌: $DOCKER_COMPOSE_CMD logs -f app"
echo "停止服務: $DOCKER_COMPOSE_CMD down"
echo "重啟服務: $DOCKER_COMPOSE_CMD restart"
echo ""
print_warning "等待應用程序啟動..."
echo ""

# 等待應用程序啟動
MAX_WAIT=60
WAIT_TIME=0
while [ $WAIT_TIME -lt $MAX_WAIT ]; do
    if curl -s http://localhost:8080 > /dev/null; then
        print_info "應用程序啟動成功！"
        break
    fi
    
    print_warning "等待應用程序啟動... ($((WAIT_TIME+5))秒)"
    sleep 5
    WAIT_TIME=$((WAIT_TIME + 5))
    
    # 檢查容器狀態
    if [ $WAIT_TIME -ge 30 ]; then
        print_warning "檢查容器狀態..."
        $DOCKER_COMPOSE_CMD ps
        $DOCKER_COMPOSE_CMD logs app --tail=20
    fi
done

if [ $WAIT_TIME -ge $MAX_WAIT ]; then
    print_warning "應用程序啟動超時，請檢查日誌"
    $DOCKER_COMPOSE_CMD logs app
fi

echo ""
echo "使用以下命令管理服務:"
echo "  $DOCKER_COMPOSE_CMD ps      # 查看服務狀態"
echo "  $DOCKER_COMPOSE_CMD logs    # 查看日誌"
echo "  $DOCKER_COMPOSE_CMD stop    # 停止服務"
echo "  $DOCKER_COMPOSE_CMD start   # 啟動服務"
echo "  $DOCKER_COMPOSE_CMD down    # 停止並移除服務"

echo ""
print_info "線上購物系統已啟動完成！"
print_info "請訪問 http://localhost:8080 開始使用"
