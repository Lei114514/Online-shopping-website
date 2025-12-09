-- 線上購物系統數據庫設計
-- 創建數據庫
CREATE DATABASE IF NOT EXISTS online_shop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE online_shop_db;

-- 用戶表（顧客和銷售人員）
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用戶名',
    email VARCHAR(100) UNIQUE NOT NULL COMMENT '電子郵件',
    password VARCHAR(255) NOT NULL COMMENT '密碼（加密存儲）',
    first_name VARCHAR(50) NOT NULL COMMENT '名字',
    last_name VARCHAR(50) NOT NULL COMMENT '姓氏',
    phone VARCHAR(20) COMMENT '電話號碼',
    address TEXT COMMENT '地址',
    role ENUM('CUSTOMER', 'SALES', 'ADMIN') DEFAULT 'CUSTOMER' COMMENT '用戶角色',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否啟用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '創建時間',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) COMMENT='用戶表';

-- 商品分類表
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分類名稱',
    description TEXT COMMENT '分類描述',
    parent_id BIGINT COMMENT '父分類ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_parent_id (parent_id)
) COMMENT='商品分類表';

-- 商品表
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL COMMENT '商品名稱',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10, 2) NOT NULL COMMENT '價格',
    stock_quantity INT NOT NULL DEFAULT 0 COMMENT '庫存數量',
    category_id BIGINT COMMENT '分類ID',
    image_url VARCHAR(500) COMMENT '商品圖片URL',
    sku VARCHAR(50) UNIQUE COMMENT '商品編號',
    status ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') DEFAULT 'ACTIVE' COMMENT '商品狀態',
    created_by BIGINT COMMENT '創建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_sku (sku)
) COMMENT='商品表';

-- 購物車表
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用戶ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '數量',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '添加時間',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_cart_item (user_id, product_id),
    INDEX idx_user_id (user_id)
) COMMENT='購物車項目表';

-- 訂單表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '訂單編號',
    user_id BIGINT NOT NULL COMMENT '用戶ID',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '總金額',
    status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '訂單狀態',
    shipping_address TEXT NOT NULL COMMENT '配送地址',
    billing_address TEXT COMMENT '賬單地址',
    payment_method VARCHAR(50) COMMENT '支付方式',
    payment_status ENUM('PENDING', 'PAID', 'FAILED') DEFAULT 'PENDING' COMMENT '支付狀態',
    notes TEXT COMMENT '備註',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) COMMENT='訂單表';

-- 訂單項目表
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '訂單ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名稱（快照）',
    product_price DECIMAL(10, 2) NOT NULL COMMENT '商品價格（快照）',
    quantity INT NOT NULL COMMENT '數量',
    subtotal DECIMAL(10, 2) NOT NULL COMMENT '小計',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) COMMENT='訂單項目表';

-- 客戶瀏覽日誌表
CREATE TABLE user_activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用戶ID（可為空，表示匿名用戶）',
    activity_type VARCHAR(50) NOT NULL COMMENT '活動類型',
    activity_details TEXT COMMENT '活動詳情',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用戶代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_activity_type (activity_type),
    INDEX idx_created_at (created_at)
) COMMENT='用戶活動日誌表';

-- 插入初始數據
-- 插入管理員用戶（密碼：admin123）
INSERT INTO users (username, email, password, first_name, last_name, role) 
VALUES ('admin', 'admin@onlineshop.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'Admin', 'User', 'ADMIN');

-- 插入銷售人員（密碼：sales123）
INSERT INTO users (username, email, password, first_name, last_name, role) 
VALUES ('sales', 'sales@onlineshop.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTV5UiC', 'Sales', 'Staff', 'SALES');

-- 插入示例分類
INSERT INTO categories (name, description) VALUES
('電子產品', '各種電子設備和配件'),
('服裝', '男女服裝和配飾'),
('書籍', '各類書籍和雜誌'),
('家居用品', '家庭生活用品'),
('食品飲料', '食品和飲料');

-- 插入示例商品
INSERT INTO products (name, description, price, stock_quantity, category_id, sku) VALUES
('智能手機', '最新款智能手機，6.5英寸屏幕，128GB存儲', 699.99, 100, 1, 'ELEC-001'),
('筆記本電腦', '高性能筆記本電腦，16GB RAM，512GB SSD', 1299.99, 50, 1, 'ELEC-002'),
('T恤', '純棉T恤，多種顏色可選', 19.99, 200, 2, 'CLOTH-001'),
('牛仔褲', '經典款牛仔褲，舒適耐穿', 49.99, 150, 2, 'CLOTH-002'),
('編程書籍', 'Java編程入門指南', 39.99, 80, 3, 'BOOK-001'),
('咖啡機', '自動咖啡機，可製作多種咖啡', 89.99, 30, 4, 'HOME-001'),
('有機咖啡豆', '100%有機咖啡豆，500g包裝', 24.99, 100, 5, 'FOOD-001');

-- 創建視圖用於銷售統計
CREATE VIEW sales_statistics AS
SELECT 
    DATE(o.created_at) as sale_date,
    COUNT(DISTINCT o.id) as total_orders,
    COUNT(oi.id) as total_items,
    SUM(o.total_amount) as total_revenue,
    AVG(o.total_amount) as avg_order_value
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
WHERE o.status IN ('DELIVERED', 'SHIPPED')
GROUP BY DATE(o.created_at);

-- 創建視圖用於庫存警報
CREATE VIEW low_stock_products AS
SELECT 
    p.id,
    p.name,
    p.stock_quantity,
    p.sku,
    c.name as category_name
FROM products p
LEFT JOIN categories c ON p.category_id = c.id
WHERE p.stock_quantity < 10 AND p.status = 'ACTIVE';
