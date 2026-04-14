package inventory;

import inventory.config.DBConnection;
import inventory.dao.*;
import inventory.model.*;
import inventory.service.*;
import inventory.util.ValidationUtil;

import java.sql.*;
import java.util.List;

/**
 * Headless test runner to verify all DAO, Service, and Validation logic.
 */
public class TestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" ELECTRONICS INVENTORY - AUTOMATED TEST SUITE");
        System.out.println("=================================================\n");

        testConnection();
        testValidationUtil();
        testBrandDAO();
        testProductDAO();
        testSupplierDAO();
        testCustomerDAO();
        testPurchaseService();
        testSaleService();
        testStockDAO();
        testWarrantyDAO();
        testPurchaseDAO();
        testSaleDAO();
        cleanup();

        System.out.println("\n=================================================");
        System.out.println(" RESULTS: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("=================================================");
        System.exit(failed > 0 ? 1 : 0);
    }

    // ---- DB Connection Test ----
    static void testConnection() {
        section("Database Connection");
        try {
            Connection conn = DBConnection.getConnection();
            check("Connection established", conn != null && !conn.isClosed());
            conn.close();
        } catch (Exception e) {
            fail("Connection", e);
        }
    }

    // ---- Validation Tests ----
    static void testValidationUtil() {
        section("ValidationUtil");
        check("isBlank(null)", ValidationUtil.isBlank(null));
        check("isBlank('')", ValidationUtil.isBlank(""));
        check("isBlank('  ')", ValidationUtil.isBlank("   "));
        check("!isBlank('abc')", !ValidationUtil.isBlank("abc"));

        check("validEmail('a@b.com')", ValidationUtil.isValidEmail("a@b.com"));
        check("validEmail('')  (optional)", ValidationUtil.isValidEmail(""));
        check("!validEmail('abc')", !ValidationUtil.isValidEmail("abc"));

        check("validPhone('9876543210')", ValidationUtil.isValidPhone("9876543210"));
        check("validPhone('')  (optional)", ValidationUtil.isValidPhone(""));
        check("!validPhone('abc')", !ValidationUtil.isValidPhone("abc"));

        check("positiveInt('5')", ValidationUtil.isPositiveInt("5"));
        check("!positiveInt('0')", !ValidationUtil.isPositiveInt("0"));
        check("!positiveInt('-1')", !ValidationUtil.isPositiveInt("-1"));
        check("!positiveInt('abc')", !ValidationUtil.isPositiveInt("abc"));

        check("nonNegDouble('0')", ValidationUtil.isNonNegativeDouble("0"));
        check("nonNegDouble('9.99')", ValidationUtil.isNonNegativeDouble("9.99"));
        check("!nonNegDouble('-1')", !ValidationUtil.isNonNegativeDouble("-1"));

        check("validDate('2024-01-15')", ValidationUtil.isValidDate("2024-01-15"));
        check("!validDate('abc')", !ValidationUtil.isValidDate("abc"));
        check("!validDate('')", !ValidationUtil.isValidDate(""));
    }

    // ---- Brand DAO ----
    static void testBrandDAO() {
        section("BrandDAO");
        BrandDAO dao = new BrandDAO();
        try {
            // Get all (sample data)
            List<Brand> all = dao.getAll();
            check("getAll returns data", all.size() >= 5);

            // Insert
            Brand b = new Brand();
            b.setBrandName("TestBrand");
            b.setCountry("TestCountry");
            dao.insert(b);

            // Search
            List<Brand> search = dao.search("TestBrand");
            check("search finds inserted", search.size() >= 1);
            int newId = search.get(0).getBrandId();

            // getById
            Brand fetched = dao.getById(newId);
            check("getById works", fetched != null && "TestBrand".equals(fetched.getBrandName()));

            // Update
            fetched.setBrandName("TestBrandUpdated");
            dao.update(fetched);
            Brand updated = dao.getById(newId);
            check("update works", "TestBrandUpdated".equals(updated.getBrandName()));

            // Delete
            dao.delete(newId);
            Brand deleted = dao.getById(newId);
            check("delete works", deleted == null);

        } catch (Exception e) {
            fail("BrandDAO", e);
        }
    }

    // ---- Product DAO ----
    static void testProductDAO() {
        section("ProductDAO");
        ProductDAO dao = new ProductDAO();
        try {
            List<Object[]> all = dao.getAllWithBrand();
            check("getAllWithBrand returns data", all.size() >= 5);

            List<Product> products = dao.getAll();
            check("getAll returns products", products.size() >= 5);

            int count = dao.getCount();
            check("getCount matches", count == products.size());

            // Search
            List<Object[]> search = dao.searchWithBrand("Galaxy");
            check("searchWithBrand works", search.size() >= 1);

            // Insert
            Product p = new Product();
            p.setProductName("TestProduct");
            p.setModelNumber("TP-001");
            p.setCategory("Test");
            p.setPrice(999.99);
            p.setBrandId(1); // Samsung from sample data
            dao.insert(p);

            List<Object[]> searchNew = dao.searchWithBrand("TestProduct");
            check("insert + search works", searchNew.size() >= 1);
            int pid = (int) searchNew.get(0)[0];

            // Update
            Product fetched = dao.getById(pid);
            check("getById works", fetched != null);
            fetched.setPrice(1099.99);
            dao.update(fetched);
            Product updated = dao.getById(pid);
            check("update price works", updated.getPrice() == 1099.99);

            // Delete
            dao.delete(pid);
            check("delete works", dao.getById(pid) == null);

        } catch (Exception e) {
            fail("ProductDAO", e);
        }
    }

    // ---- Supplier DAO ----
    static void testSupplierDAO() {
        section("SupplierDAO");
        SupplierDAO dao = new SupplierDAO();
        try {
            List<Supplier> all = dao.getAll();
            check("getAll returns data", all.size() >= 3);

            Supplier s = new Supplier();
            s.setSupplierName("TestSupplier");
            s.setPhone("1234567890");
            s.setCity("TestCity");
            s.setEmail("test@supplier.com");
            dao.insert(s);

            List<Supplier> search = dao.search("TestSupplier");
            check("insert + search works", search.size() >= 1);
            int sid = search.get(0).getSupplierId();

            dao.delete(sid);
            check("delete works", dao.getById(sid) == null);

        } catch (Exception e) {
            fail("SupplierDAO", e);
        }
    }

    // ---- Customer DAO ----
    static void testCustomerDAO() {
        section("CustomerDAO");
        CustomerDAO dao = new CustomerDAO();
        try {
            List<Customer> all = dao.getAll();
            check("getAll returns data", all.size() >= 3);

            Customer c = new Customer();
            c.setCustomerName("TestCustomer");
            c.setPhone("9876543210");
            c.setEmail("test@customer.com");
            dao.insert(c);

            List<Customer> search = dao.search("TestCustomer");
            check("insert + search works", search.size() >= 1);
            int cid = search.get(0).getCustomerId();

            dao.delete(cid);
            check("delete works", dao.getById(cid) == null);

        } catch (Exception e) {
            fail("CustomerDAO", e);
        }
    }

    // ---- Purchase Service (transactional) ----
    static void testPurchaseService() {
        section("PurchaseService (Transaction)");
        PurchaseService service = new PurchaseService();
        StockDAO stockDAO = new StockDAO();
        try {
            // Product 1 = Galaxy S24 Ultra (from sample data)
            // Get initial stock (may not exist yet)
            Stock initStock = stockDAO.getByProductId(1);
            int initQty = (initStock != null) ? initStock.getAvailableQuantity() : 0;

            // Add purchase
            Purchase p = new Purchase();
            p.setPurchaseDate(Date.valueOf("2024-06-01"));
            p.setQuantity(50);
            p.setCostPrice(110000.00);
            p.setProductId(1);
            p.setSupplierId(1);
            service.addPurchase(p);

            // Verify stock increased
            Stock afterPurchase = stockDAO.getByProductId(1);
            check("stock created/updated after purchase",
                    afterPurchase != null && afterPurchase.getAvailableQuantity() == initQty + 50);

            System.out.println("    [INFO] Stock for product 1: " + afterPurchase.getAvailableQuantity());

        } catch (Exception e) {
            fail("PurchaseService.addPurchase", e);
        }
    }

    // ---- Sale Service (transactional + warranty) ----
    static void testSaleService() {
        section("SaleService (Transaction + Stock Check + Warranty)");
        SaleService service = new SaleService();
        StockDAO stockDAO = new StockDAO();
        WarrantyDAO warrantyDAO = new WarrantyDAO();
        try {
            Stock beforeSale = stockDAO.getByProductId(1);
            int beforeQty = beforeSale.getAvailableQuantity();

            // Sale with warranty
            Sale s = new Sale();
            s.setSaleDate(Date.valueOf("2024-06-15"));
            s.setQuantity(2);
            s.setSellingPrice(129999.00);
            s.setProductId(1);
            s.setCustomerId(1);
            String warning = service.addSale(s, true, 12);

            // Verify stock decreased
            Stock afterSale = stockDAO.getByProductId(1);
            check("stock decreased after sale",
                    afterSale.getAvailableQuantity() == beforeQty - 2);

            // Verify warranty created
            List<Object[]> warranties = warrantyDAO.getAllWithDetails();
            check("warranty created", warranties.size() >= 1);

            System.out.println("    [INFO] Stock after sale: " + afterSale.getAvailableQuantity());
            if (warning != null) System.out.println("    [INFO] Warning: " + warning);

            // Test insufficient stock
            Sale bigSale = new Sale();
            bigSale.setSaleDate(Date.valueOf("2024-06-20"));
            bigSale.setQuantity(99999); // way too much
            bigSale.setSellingPrice(129999.00);
            bigSale.setProductId(1);
            bigSale.setCustomerId(1);
            boolean blocked = false;
            try {
                service.addSale(bigSale, false, 0);
            } catch (SQLException e) {
                if (e.getMessage().contains("Insufficient stock")) blocked = true;
            }
            check("insufficient stock sale blocked", blocked);

        } catch (Exception e) {
            fail("SaleService", e);
        }
    }

    // ---- Stock DAO ----
    static void testStockDAO() {
        section("StockDAO");
        StockDAO dao = new StockDAO();
        try {
            List<Object[]> all = dao.getAllWithProduct();
            check("getAllWithProduct returns data", all.size() >= 1);

            int totalQty = dao.getTotalStockQuantity();
            check("getTotalStockQuantity > 0", totalQty > 0);

            // Search
            List<Object[]> search = dao.searchByProduct("Galaxy");
            check("searchByProduct works", search.size() >= 1);

            // Update reorder level
            int stockId = (int) all.get(0)[0];
            dao.updateReorderLevel(stockId, 20);
            Stock s = dao.getByProductId((int) all.get(0)[1]);
            check("updateReorderLevel works", s.getReorderLevel() == 20);

            // Low stock (set reorder very high to test)
            dao.updateReorderLevel(stockId, 99999);
            List<Object[]> lowStock = dao.getLowStock();
            check("getLowStock detects items", lowStock.size() >= 1);
            int lowCount = dao.getLowStockCount();
            check("getLowStockCount matches", lowCount >= 1);

            // Reset reorder level
            dao.updateReorderLevel(stockId, 10);

        } catch (Exception e) {
            fail("StockDAO", e);
        }
    }

    // ---- Warranty DAO ----
    static void testWarrantyDAO() {
        section("WarrantyDAO");
        WarrantyDAO dao = new WarrantyDAO();
        try {
            List<Object[]> all = dao.getAllWithDetails();
            check("getAllWithDetails returns data", all.size() >= 1);

            // Check warranty dates
            Object[] first = all.get(0);
            Date start = (Date) first[5];
            Date end = (Date) first[6];
            check("warranty_end > warranty_start", end.after(start));

            // Search
            List<Object[]> search = dao.searchWithDetails("Galaxy");
            check("searchWithDetails works", search.size() >= 1);

        } catch (Exception e) {
            fail("WarrantyDAO", e);
        }
    }

    // ---- Purchase DAO (read) ----
    static void testPurchaseDAO() {
        section("PurchaseDAO (Read)");
        PurchaseDAO dao = new PurchaseDAO();
        try {
            List<Object[]> all = dao.getAllWithDetails();
            check("getAllWithDetails returns data", all.size() >= 1);

            int total = dao.getTotalPurchases();
            check("getTotalPurchases matches", total == all.size());

            List<Object[]> search = dao.searchWithDetails("Galaxy");
            check("searchWithDetails works", search.size() >= 1);

        } catch (Exception e) {
            fail("PurchaseDAO", e);
        }
    }

    // ---- Sale DAO (read) ----
    static void testSaleDAO() {
        section("SaleDAO (Read)");
        SaleDAO dao = new SaleDAO();
        try {
            List<Object[]> all = dao.getAllWithDetails();
            check("getAllWithDetails returns data", all.size() >= 1);

            int total = dao.getTotalSales();
            check("getTotalSales matches", total == all.size());

            double revenue = dao.getTotalRevenue();
            check("getTotalRevenue > 0", revenue > 0);

            List<Object[]> search = dao.searchWithDetails("Rahul");
            check("searchWithDetails by customer works", search.size() >= 1);

        } catch (Exception e) {
            fail("SaleDAO", e);
        }
    }

    // ---- Cleanup test data ----
    static void cleanup() {
        section("Cleanup");
        try {
            Connection conn = DBConnection.getConnection();
            // Delete test warranties, sales, purchases, stock
            PreparedStatement ps;
            ps = conn.prepareStatement("DELETE FROM warranty WHERE sale_id IN (SELECT sale_id FROM sale WHERE sale_date='2024-06-15')");
            ps.executeUpdate();
            ps = conn.prepareStatement("DELETE FROM sale WHERE sale_date='2024-06-15'");
            ps.executeUpdate();
            ps = conn.prepareStatement("DELETE FROM purchase WHERE purchase_date='2024-06-01'");
            ps.executeUpdate();
            ps = conn.prepareStatement("DELETE FROM stock WHERE product_id=1");
            ps.executeUpdate();
            conn.close();
            System.out.println("  [OK] Test data cleaned up.");
        } catch (Exception e) {
            System.out.println("  [WARN] Cleanup: " + e.getMessage());
        }
    }

    // ---- Helpers ----

    static void section(String name) {
        System.out.println("\n--- " + name + " ---");
    }

    static void check(String desc, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + desc);
            passed++;
        } else {
            System.out.println("  [FAIL] " + desc);
            failed++;
        }
    }

    static void fail(String context, Exception e) {
        System.out.println("  [FAIL] " + context + " -> " + e.getMessage());
        e.printStackTrace(System.out);
        failed++;
    }
}
