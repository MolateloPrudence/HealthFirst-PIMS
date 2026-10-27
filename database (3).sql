-- ============================================
-- PIMS DATABASE SCRIPT
-- Pharmacy Inventory Management System
-- ============================================
CREATE DATABASE IF NOT EXISTS
pims_db;
USE pims_db;

-- ============================================
-- TABLE 1: users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    user_id     INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('Admin', 'Cashier') NOT NULL,
    full_name   VARCHAR(100) NOT NULL
);

-- ============================================
-- TABLE 2: suppliers
-- ============================================
CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id     INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT
);

-- ============================================
-- TABLE 3: medicines
-- ============================================
CREATE TABLE IF NOT EXISTS medicines (
    medicine_id       INT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(150) NOT NULL,
    company           VARCHAR(100),
    medicine_type     VARCHAR(50),
    price             DECIMAL(10,2) NOT NULL,
    quantity_in_stock INT NOT NULL DEFAULT 0,
    reorder_level     INT NOT NULL DEFAULT 10,
    expiry_date       DATE,
    supplier_id       INT,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- ============================================
-- TABLE 4: sales
-- ============================================
CREATE TABLE IF NOT EXISTS sales (
    sale_id       INT AUTO_INCREMENT PRIMARY KEY,
    sale_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10,2) NOT NULL,
    user_id       INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- ============================================
-- TABLE 5: sale_items
-- ============================================
CREATE TABLE IF NOT EXISTS sale_items (
    sale_item_id  INT AUTO_INCREMENT PRIMARY KEY,
    sale_id       INT NOT NULL,
    medicine_id   INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Users (admin & cashier)
INSERT INTO users (username, password, role, full_name) VALUES
('admin',    'admin123',   'Admin',   'System Administrator'),
('cashier',  'cash123',    'Cashier', 'John Cashier');

-- Suppliers
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('PharmaCorp Ltd',    'Prudence Molele',  '0112345678', 'sales@pharmacorp.co.za',   '12 Main Rd, Johannesburg'),
('MedSupply SA',      'Mokgadi Rangwato',   '0159876543', 'orders@medsupply.co.za',   '45 Market St, Polokwane'),
('HealthLink Distributors', 'Vuyo Mbam', '0315551234', 'info@healthlink.co.za',   '78 Marine Dr, Durban'),
('Wellness Pharma',   'Lebogang letswalo',    '0123456789', 'lebogang@wellnesspharma.co.za', '9 Church St, Pretoria');

-- Medicines (mix of stock levels & expiry dates)
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Paracetamol 500mg',    'PharmaCorp Ltd',        'Tablet',    12.50,  200, 50,  '2027-05-15', 1),
('Amoxicillin 250mg',    'MedSupply SA',          'Capsule',   45.00,  80,  30,  '2026-11-20', 2),
('Cough Syrup 100ml',    'HealthLink Distributors','Syrup',     35.75,  40,  20,  '2026-10-05', 3),
('Ibuprofen 400mg',      'PharmaCorp Ltd',        'Tablet',    18.90,  150, 40,  '2027-03-10', 1),
('Insulin Injection',    'MedSupply SA',          'Injection', 120.00, 25,  10,  '2026-12-31', 2),
('Vitamin C 1000mg',     'Wellness Pharma',       'Tablet',    22.00,  300, 60,  '2027-08-20', 4),
('Antiseptic Cream 50g', 'HealthLink Distributors','Cream',     28.50,  60,  25,  '2026-09-28', 3),
('Cetirizine 10mg',      'PharmaCorp Ltd',        'Tablet',    15.25,  8,   30,  '2027-01-15', 1),
('Metformin 500mg',      'MedSupply SA',          'Tablet',    20.00,  100, 35,  '2027-06-30', 2),
('Salbutamol Inhaler',   'Wellness Pharma',       'Injection', 89.99,  5,   15,  '2026-10-22', 4),
('Omeprazole 20mg',      'HealthLink Distributors','Capsule',   30.00,  75,  30,  '2027-04-18', 3),
('Hydrocortisone Cream','PharmaCorp Ltd',         'Cream',     25.00,  45,  20,  '2026-09-20', 1);

-- Sample sale (for testing reports)
INSERT INTO sales (total_amount, user_id) VALUES (215.00, 2);

INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES
(1, 1, 10, 12.50),
(1, 4, 5,  18.90);