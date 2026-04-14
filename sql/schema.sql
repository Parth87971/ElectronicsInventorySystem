-- ============================================================
-- ELECTRONICS INVENTORY MANAGEMENT SYSTEM
-- Database Schema Script
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS electronics_inventory;
USE electronics_inventory;

-- ============================================================
-- TABLE 1: BRAND
-- ============================================================
CREATE TABLE brand (
    brand_id    INT PRIMARY KEY AUTO_INCREMENT,
    brand_name  VARCHAR(100) NOT NULL,
    country     VARCHAR(60)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE 2: PRODUCT
-- ============================================================
CREATE TABLE product (
    product_id    INT PRIMARY KEY AUTO_INCREMENT,
    product_name  VARCHAR(150) NOT NULL,
    model_number  VARCHAR(80),
    category      VARCHAR(80),
    price         DECIMAL(10,2) NOT NULL,
    brand_id      INT NOT NULL,
    CONSTRAINT chk_product_price CHECK (price >= 0),
    CONSTRAINT fk_product_brand FOREIGN KEY (brand_id)
        REFERENCES brand(brand_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_product_brand (brand_id)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE 3: SUPPLIER
-- ============================================================
CREATE TABLE supplier (
    supplier_id    INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name  VARCHAR(150) NOT NULL,
    phone          VARCHAR(20),
    city           VARCHAR(80),
    email          VARCHAR(150)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE 4: CUSTOMER
-- ============================================================
CREATE TABLE customer (
    customer_id    INT PRIMARY KEY AUTO_INCREMENT,
    customer_name  VARCHAR(150) NOT NULL,
    phone          VARCHAR(20),
    email          VARCHAR(150)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE 5: PURCHASE
-- ============================================================
CREATE TABLE purchase (
    purchase_id    INT PRIMARY KEY AUTO_INCREMENT,
    purchase_date  DATE NOT NULL,
    quantity       INT NOT NULL,
    cost_price     DECIMAL(10,2) NOT NULL,
    product_id     INT NOT NULL,
    supplier_id    INT NOT NULL,
    CONSTRAINT chk_purchase_qty   CHECK (quantity > 0),
    CONSTRAINT chk_purchase_cost  CHECK (cost_price >= 0),
    CONSTRAINT fk_purchase_product  FOREIGN KEY (product_id)
        REFERENCES product(product_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_purchase_supplier FOREIGN KEY (supplier_id)
        REFERENCES supplier(supplier_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_purchase_product  (product_id),
    INDEX idx_purchase_supplier (supplier_id)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE 6: SALE
-- ============================================================
CREATE TABLE sale (
    sale_id        INT PRIMARY KEY AUTO_INCREMENT,
    sale_date      DATE NOT NULL,
    quantity       INT NOT NULL,
    selling_price  DECIMAL(10,2) NOT NULL,
    product_id     INT NOT NULL,
    customer_id    INT NOT NULL,
    CONSTRAINT chk_sale_qty    CHECK (quantity > 0),
    CONSTRAINT chk_sale_price  CHECK (selling_price >= 0),
    CONSTRAINT fk_sale_product  FOREIGN KEY (product_id)
        REFERENCES product(product_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id)
        REFERENCES customer(customer_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_sale_product  (product_id),
    INDEX idx_sale_customer (customer_id)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE 7: STOCK  (one-to-one with product)
-- ============================================================
CREATE TABLE stock (
    stock_id            INT PRIMARY KEY AUTO_INCREMENT,
    product_id          INT NOT NULL UNIQUE,
    available_quantity  INT NOT NULL,
    reorder_level       INT NOT NULL,
    CONSTRAINT chk_stock_qty     CHECK (available_quantity >= 0),
    CONSTRAINT chk_stock_reorder CHECK (reorder_level >= 0),
    CONSTRAINT fk_stock_product  FOREIGN KEY (product_id)
        REFERENCES product(product_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_stock_product (product_id)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE 8: WARRANTY  (one-to-one with sale)
-- ============================================================
CREATE TABLE warranty (
    warranty_id     INT PRIMARY KEY AUTO_INCREMENT,
    sale_id         INT NOT NULL UNIQUE,
    warranty_start  DATE NOT NULL,
    warranty_end    DATE NOT NULL,
    CONSTRAINT fk_warranty_sale FOREIGN KEY (sale_id)
        REFERENCES sale(sale_id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_warranty_sale (sale_id)
) ENGINE=InnoDB;

-- ============================================================
-- SAMPLE DATA (Optional — for testing)
-- ============================================================

INSERT INTO brand (brand_name, country) VALUES
('Samsung', 'South Korea'),
('Apple', 'USA'),
('OnePlus', 'China'),
('Sony', 'Japan'),
('Xiaomi', 'China');

INSERT INTO supplier (supplier_name, phone, city, email) VALUES
('TechWorld Distributors', '9876543210', 'Mumbai', 'tech@world.com'),
('Global Electronics', '9123456780', 'Delhi', 'global@elec.com'),
('Prime Supply Co.', '9988776655', 'Bangalore', 'prime@supply.com');

INSERT INTO customer (customer_name, phone, email) VALUES
('Rahul Sharma', '9876501234', 'rahul@email.com'),
('Priya Patel', '9123409876', 'priya@email.com'),
('Amit Kumar', '9988770011', 'amit@email.com');

INSERT INTO product (product_name, model_number, category, price, brand_id) VALUES
('Galaxy S24 Ultra', 'SM-S928B', 'Smartphone', 129999.00, 1),
('iPhone 15 Pro', 'A2848', 'Smartphone', 134900.00, 2),
('OnePlus 12', 'CPH2583', 'Smartphone', 64999.00, 3),
('WH-1000XM5', 'WH1000XM5', 'Headphones', 29990.00, 4),
('Redmi Note 13 Pro', '23090RA98I', 'Smartphone', 22999.00, 5);
