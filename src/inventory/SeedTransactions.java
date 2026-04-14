package inventory;

import inventory.model.Purchase;
import inventory.model.Sale;
import inventory.service.PurchaseService;
import inventory.service.SaleService;

import java.sql.Date;
import java.time.LocalDate;

public class SeedTransactions {
    public static void main(String[] args) {
        System.out.println("Seeding transaction data...");
        try {
            PurchaseService purchaseService = new PurchaseService();
            SaleService saleService = new SaleService();
            
            // Generate some past dates
            Date date1 = Date.valueOf(LocalDate.now().minusDays(10));
            Date date2 = Date.valueOf(LocalDate.now().minusDays(8));
            Date date3 = Date.valueOf(LocalDate.now().minusDays(5));
            Date date4 = Date.valueOf(LocalDate.now().minusDays(2));
            Date dateNow = Date.valueOf(LocalDate.now());

            // 1. Add Purchases (automatically creates/updates Stock)
            // Product 1: Galaxy S24 Ultra, Supplier 1
            Purchase p1 = new Purchase();
            p1.setPurchaseDate(date1);
            p1.setProductId(1);
            p1.setSupplierId(1);
            p1.setQuantity(50);
            p1.setCostPrice(115000.0);
            purchaseService.addPurchase(p1);

            // Product 2: iPhone 15 Pro, Supplier 2
            Purchase p2 = new Purchase();
            p2.setPurchaseDate(date2);
            p2.setProductId(2);
            p2.setSupplierId(2);
            p2.setQuantity(30);
            p2.setCostPrice(120000.0);
            purchaseService.addPurchase(p2);

            // Product 4: WH-1000XM5, Supplier 3
            Purchase p3 = new Purchase();
            p3.setPurchaseDate(date3);
            p3.setProductId(4);
            p3.setSupplierId(3);
            p3.setQuantity(100);
            p3.setCostPrice(22000.0);
            purchaseService.addPurchase(p3);

            System.out.println("Purchases added and stock updated!");

            // 2. Add Sales (automatically decrements Stock and creates Warranty if true)
            // Sell 2 Galaxy S24 Ultra to Customer 1 with 12 months warranty
            Sale s1 = new Sale();
            s1.setSaleDate(date3);
            s1.setProductId(1);
            s1.setCustomerId(1);
            s1.setQuantity(2);
            s1.setSellingPrice(129999.0);
            saleService.addSale(s1, true, 12);

            // Sell 1 iPhone 15 Pro to Customer 2 with 24 months warranty
            Sale s2 = new Sale();
            s2.setSaleDate(date4);
            s2.setProductId(2);
            s2.setCustomerId(2);
            s2.setQuantity(1);
            s2.setSellingPrice(134900.0);
            saleService.addSale(s2, true, 24);

            // Sell 5 Headphones to Customer 3 without warranty
            Sale s3 = new Sale();
            s3.setSaleDate(dateNow);
            s3.setProductId(4);
            s3.setCustomerId(3);
            s3.setQuantity(5);
            s3.setSellingPrice(29990.0);
            saleService.addSale(s3, false, 0);

            // Sell 1 more Galaxy S24 Ultra to Customer 2
            Sale s4 = new Sale();
            s4.setSaleDate(dateNow);
            s4.setProductId(1);
            s4.setCustomerId(2);
            s4.setQuantity(1);
            s4.setSellingPrice(129999.0);
            saleService.addSale(s4, true, 12);

            System.out.println("Sales recorded, stock decrcemented, and warranties generated!");
            System.out.println("Data seeding complete.");

        } catch (Exception e) {
            System.err.println("Error seeding data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
