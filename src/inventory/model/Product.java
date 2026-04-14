package inventory.model;

public class Product {
    private int    productId;
    private String productName;
    private String modelNumber;
    private String category;
    private double price;
    private int    brandId;

    public Product() {}

    public Product(int productId, String productName, String modelNumber,
                   String category, double price, int brandId) {
        this.productId   = productId;
        this.productName = productName;
        this.modelNumber = modelNumber;
        this.category    = category;
        this.price       = price;
        this.brandId     = brandId;
    }

    public int    getProductId()   { return productId; }
    public void   setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void   setProductName(String productName) { this.productName = productName; }

    public String getModelNumber() { return modelNumber; }
    public void   setModelNumber(String modelNumber) { this.modelNumber = modelNumber; }

    public String getCategory()    { return category; }
    public void   setCategory(String category) { this.category = category; }

    public double getPrice()       { return price; }
    public void   setPrice(double price) { this.price = price; }

    public int    getBrandId()     { return brandId; }
    public void   setBrandId(int brandId) { this.brandId = brandId; }

    @Override
    public String toString() { return productName; }
}
