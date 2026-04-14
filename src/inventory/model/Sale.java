package inventory.model;

import java.sql.Date;

public class Sale {
    private int    saleId;
    private Date   saleDate;
    private int    quantity;
    private double sellingPrice;
    private int    productId;
    private int    customerId;

    public Sale() {}

    public Sale(int saleId, Date saleDate, int quantity,
                double sellingPrice, int productId, int customerId) {
        this.saleId       = saleId;
        this.saleDate     = saleDate;
        this.quantity     = quantity;
        this.sellingPrice = sellingPrice;
        this.productId    = productId;
        this.customerId   = customerId;
    }

    public int    getSaleId()       { return saleId; }
    public void   setSaleId(int saleId) { this.saleId = saleId; }

    public Date   getSaleDate()     { return saleDate; }
    public void   setSaleDate(Date saleDate) { this.saleDate = saleDate; }

    public int    getQuantity()     { return quantity; }
    public void   setQuantity(int quantity) { this.quantity = quantity; }

    public double getSellingPrice() { return sellingPrice; }
    public void   setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }

    public int    getProductId()    { return productId; }
    public void   setProductId(int productId) { this.productId = productId; }

    public int    getCustomerId()   { return customerId; }
    public void   setCustomerId(int customerId) { this.customerId = customerId; }
}
