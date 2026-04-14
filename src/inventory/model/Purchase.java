package inventory.model;

import java.sql.Date;

public class Purchase {
    private int    purchaseId;
    private Date   purchaseDate;
    private int    quantity;
    private double costPrice;
    private int    productId;
    private int    supplierId;

    public Purchase() {}

    public Purchase(int purchaseId, Date purchaseDate, int quantity,
                    double costPrice, int productId, int supplierId) {
        this.purchaseId  = purchaseId;
        this.purchaseDate = purchaseDate;
        this.quantity    = quantity;
        this.costPrice   = costPrice;
        this.productId   = productId;
        this.supplierId  = supplierId;
    }

    public int    getPurchaseId()   { return purchaseId; }
    public void   setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }

    public Date   getPurchaseDate() { return purchaseDate; }
    public void   setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }

    public int    getQuantity()     { return quantity; }
    public void   setQuantity(int quantity) { this.quantity = quantity; }

    public double getCostPrice()    { return costPrice; }
    public void   setCostPrice(double costPrice) { this.costPrice = costPrice; }

    public int    getProductId()    { return productId; }
    public void   setProductId(int productId) { this.productId = productId; }

    public int    getSupplierId()   { return supplierId; }
    public void   setSupplierId(int supplierId) { this.supplierId = supplierId; }
}
