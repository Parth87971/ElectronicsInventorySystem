# Electronics Inventory Management System

A complete desktop inventory management application built with **Java Swing**, **JDBC**, and **MySQL** for an electronics and mobile shop.

> **Academic DBMS Mini Project** — designed for structured presentation, viva, and demo.

---

## Features

| Module            | Capabilities                                                      |
|-------------------|-------------------------------------------------------------------|
| **Dashboard**     | Quick stats — total products, stock, sales, low-stock count       |
| **Brand**         | Add, Update, Delete, Search, View All                             |
| **Product**       | CRUD with brand dropdown, price validation                        |
| **Supplier**      | CRUD with phone/email validation                                  |
| **Customer**      | CRUD with phone/email validation                                  |
| **Purchase**      | Record purchase → auto-creates/updates stock (transactional)      |
| **Sale**          | Record sale → validates stock → auto-decrements → warranty option |
| **Stock**         | View all, search, low-stock filter, update reorder level          |
| **Warranty**      | Auto-created on sale, view/search by product or customer          |

### Business Logic Highlights

- **Stock auto-update**: Purchases increase stock; Sales decrease stock. If no stock row exists, it's created automatically.
- **Stock validation**: Sales are blocked if requested quantity exceeds available stock.
- **Low-stock alerts**: After a sale, a warning is displayed if stock drops to or below the reorder level.
- **Warranty generation**: When a sale is recorded with "Include Warranty" checked, a warranty row is automatically created with `warranty_end = sale_date + N months`.
- **Transaction safety**: Purchase and Sale operations use database transactions with commit/rollback.
- **Input validation**: Required fields, positive quantities, non-negative prices, email/phone format checks.

---

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Language    | Java 8+                             |
| GUI         | Java Swing                          |
| Database    | MySQL 8.0+                          |
| Connectivity| JDBC with `PreparedStatement` only  |
| Driver      | MySQL Connector/J 8.x               |

---

## Project Structure

```
ElectronicsInventorySystem/
├── sql/
│   └── schema.sql                  # Database creation + sample data
├── lib/
│   └── mysql-connector-j-8.x.jar  # (You must place the JAR here)
├── src/
│   └── inventory/
│       ├── Main.java               # Application entry point
│       ├── config/
│       │   └── DBConnection.java   # JDBC connection utility
│       ├── model/                  # POJO classes (8 files)
│       │   ├── Brand.java
│       │   ├── Product.java
│       │   ├── Supplier.java
│       │   ├── Customer.java
│       │   ├── Purchase.java
│       │   ├── Sale.java
│       │   ├── Stock.java
│       │   └── Warranty.java
│       ├── dao/                    # Data Access Objects (8 files)
│       │   ├── BrandDAO.java
│       │   ├── ProductDAO.java
│       │   ├── SupplierDAO.java
│       │   ├── CustomerDAO.java
│       │   ├── PurchaseDAO.java
│       │   ├── SaleDAO.java
│       │   ├── StockDAO.java
│       │   └── WarrantyDAO.java
│       ├── service/                # Transaction/business logic
│       │   ├── PurchaseService.java
│       │   └── SaleService.java
│       ├── util/                   # Utilities
│       │   ├── ValidationUtil.java
│       │   └── ComboItem.java
│       └── ui/                     # Swing GUI panels (10 files)
│           ├── MainFrame.java
│           ├── DashboardPanel.java
│           ├── BrandPanel.java
│           ├── ProductPanel.java
│           ├── SupplierPanel.java
│           ├── CustomerPanel.java
│           ├── PurchasePanel.java
│           ├── SalePanel.java
│           ├── StockPanel.java
│           └── WarrantyPanel.java
├── build_and_run.bat               # Windows build + run script
└── README.md
```

---

## Setup Instructions

### Prerequisites

1. **JDK 8** or later installed and `javac`/`java` in PATH.
2. **MySQL 8.0+** installed and running.
3. **MySQL Connector/J** JAR file (download from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/)).

### Step 1: Create the Database

```bash
mysql -u root -p < sql/schema.sql
```

This creates the `electronics_inventory` database, all 8 tables, and inserts sample data.

### Step 2: Configure Database Credentials

Open `src/inventory/config/DBConnection.java` and update:

```java
private static final String USER = "root";
private static final String PASS = "";   // ← Your MySQL password
```

### Step 3: Add MySQL Connector JAR

Create a `lib/` folder in the project root and place the MySQL Connector/J JAR there:

```
ElectronicsInventorySystem/
└── lib/
    └── mysql-connector-j-8.3.0.jar   (or your version)
```

### Step 4: Build and Run

**Option A — Batch Script (Windows)**:
```
build_and_run.bat
```

**Option B — Manual**:
```bash
# Compile
javac -cp "lib/*" -d out src/inventory/**/*.java src/inventory/Main.java

# Run
java -cp "out;lib/*" inventory.Main
```

---

## Database Schema (ER Summary)

```
BRAND (1) ──── (M) PRODUCT (1) ──── (1) STOCK
                       │
              ┌────────┴────────┐
              │                 │
         PURCHASE (M)      SALE (M) ──── (1) WARRANTY
              │                 │
         SUPPLIER (1)      CUSTOMER (1)
```

### Key Relationships

| Relationship          | Type     | FK Location         |
|-----------------------|----------|---------------------|
| Brand → Product       | 1:M      | product.brand_id    |
| Product → Purchase    | 1:M      | purchase.product_id |
| Supplier → Purchase   | 1:M      | purchase.supplier_id|
| Product → Sale        | 1:M      | sale.product_id     |
| Customer → Sale       | 1:M      | sale.customer_id    |
| Product → Stock       | 1:1      | stock.product_id (UNIQUE) |
| Sale → Warranty       | 1:1      | warranty.sale_id (UNIQUE) |

### Normalization

- All tables are in **3NF** (Third Normal Form).
- No transitive dependencies.
- No partial key dependencies.
- Referential integrity maintained via foreign keys.

---

## Design Decisions

1. **No product_supplier junction table** — supplier relationship is captured through `PURCHASE` (which supplier supplied which product, when).
2. **ON DELETE RESTRICT** on most FKs — prevents orphan records (cannot delete a brand if products exist).
3. **ON DELETE CASCADE** on warranty FK — deleting a sale automatically removes its warranty.
4. **CHECK constraints** — enforce positive quantities and non-negative prices at the database level.
5. **Service layer for transactions** — `PurchaseService` and `SaleService` handle multi-table operations within transactions.

---

## License

Academic project — free to use for educational purposes.
