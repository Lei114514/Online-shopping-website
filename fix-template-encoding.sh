#!/bin/bash

# 修復所有模板文件的編碼問題
echo "=== 修復模板文件編碼 ==="

# 定義要處理的模板文件
TEMPLATE_FILES=(
    "src/main/resources/templates/products.html"
    "src/main/resources/templates/merchant/product-list.html"
    "src/main/resources/templates/merchant/product-form.html"
    "src/main/resources/templates/checkout.html"
    "src/main/resources/templates/order-confirmation.html"
)

# 備份目錄
BACKUP_DIR="template_backups_$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

echo "備份原始文件到: $BACKUP_DIR"

# 處理每個文件
for file in "${TEMPLATE_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "處理: $file"
        
        # 備份原文件
        cp "$file" "$BACKUP_DIR/$(basename $file)"
        
        # 轉換為UTF-8編碼（如果需要）
        # 檢查文件編碼
        current_encoding=$(file -b --mime-encoding "$file")
        echo "  當前編碼: $current_encoding"
        
        if [ "$current_encoding" != "utf-8" ] && [ "$current_encoding" != "us-ascii" ]; then
            echo "  轉換為 UTF-8..."
            iconv -f "$current_encoding" -t UTF-8 "$file" -o "$file.tmp"
            mv "$file.tmp" "$file"
        fi
        
        # 確保文件以UTF-8保存且無BOM
        echo "  確保UTF-8無BOM..."
        # 移除BOM如果存在
        sed -i '1s/^\xEF\xBB\xBF//' "$file"2>/dev/null || sed -i '' '1s/^\xEF\xBB\xBF//' "$file" 2>/dev/null
        
        echo "  完成"
    else
        echo "文件不存在: $file"
    fi
done

echo ""
echo "=== 修復完成 ==="
echo "備份文件位置: $BACKUP_DIR"
echo ""
echo "請重新構建並部署應用:"
echo "  ./docker-run.sh"