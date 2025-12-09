#!/bin/bash

# Docker運行腳本

set -e

# 顏色定義
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== 使用Docker啟動線上購物系統 ===${NC}"

# 檢查Docker
if ! command -v docker &> /dev/null; then
    echo "錯誤: Docker未安裝"
    exit 1
fi

# 檢查Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "錯誤: Docker Compose未安裝"
    exit 1
fi

echo "1. 構建Docker鏡像..."
docker-compose build

echo ""
echo "2. 啟動服務..."
docker-compose up -d

echo ""
echo -e "${GREEN}=== 服務啟動完成 ===${NC}"
echo ""
echo "應用程序: http://localhost:8080"
echo "MySQL數據庫: localhost:3306"
echo ""
echo "管理員帳號: admin / admin123"
echo "銷售帳號: sales / sales123"
echo ""
echo "查看日誌: docker-compose logs -f app"
echo "停止服務: docker-compose down"
echo "重啟服務: docker-compose restart"
echo ""
echo -e "${YELLOW}等待應用程序啟動...${NC}"
echo ""

# 等待應用程序啟動
sleep 10

# 檢查應用程序狀態
if curl -s http://localhost:8080 > /dev/null; then
    echo -e "${GREEN}應用程序啟動成功！${NC}"
else
    echo -e "${YELLOW}應用程序正在啟動中，請稍後訪問...${NC}"
fi

echo ""
echo "使用以下命令管理服務:"
echo "  docker-compose ps      # 查看服務狀態"
echo "  docker-compose logs    # 查看日誌"
echo "  docker-compose stop    # 停止服務"
echo "  docker-compose start   # 啟動服務"
echo "  docker-compose down    # 停止並移除服務"
