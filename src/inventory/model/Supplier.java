package inventory.model;

public class Supplier {
    private int    supplierId;
    private String supplierName;
    private String phone;
    private String city;
    private String email;

    public Supplier() {}

    public Supplier(int supplierId, String supplierName, String phone,
                    String city, String email) {
        this.supplierId   = supplierId;
        this.supplierName = supplierName;
        this.phone        = phone;
        this.city         = city;
        this.email        = email;
    }

    public int    getSupplierId()   { return supplierId; }
    public void   setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void   setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getPhone()        { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    public String getCity()          { return city; }
    public void   setCity(String city) { this.city = city; }

    public String getEmail()         { return email; }
    public void   setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return supplierName; }
}
