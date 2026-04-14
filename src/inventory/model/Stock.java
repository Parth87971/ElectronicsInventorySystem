package inventory.model;

public class Stock {
    private int stockId;
    private int productId;
    private int availableQuantity;
    private int reorderLevel;

    public Stock() {}

    public Stock(int stockId, int productId, int availableQuantity, int reorderLevel) {
        this.stockId           = stockId;
        this.productId         = productId;
        this.availableQuantity = availableQuantity;
        this.reorderLevel      = reorderLevel;
    }

    public int getStockId()           { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public int getProductId()         { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public int getReorderLevel()      { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
}
