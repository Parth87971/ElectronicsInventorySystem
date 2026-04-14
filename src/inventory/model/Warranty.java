package inventory.model;

import java.sql.Date;

public class Warranty {
    private int  warrantyId;
    private int  saleId;
    private Date warrantyStart;
    private Date warrantyEnd;

    public Warranty() {}

    public Warranty(int warrantyId, int saleId, Date warrantyStart, Date warrantyEnd) {
        this.warrantyId    = warrantyId;
        this.saleId        = saleId;
        this.warrantyStart = warrantyStart;
        this.warrantyEnd   = warrantyEnd;
    }

    public int  getWarrantyId()    { return warrantyId; }
    public void setWarrantyId(int warrantyId) { this.warrantyId = warrantyId; }

    public int  getSaleId()        { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }

    public Date getWarrantyStart() { return warrantyStart; }
    public void setWarrantyStart(Date warrantyStart) { this.warrantyStart = warrantyStart; }

    public Date getWarrantyEnd()   { return warrantyEnd; }
    public void setWarrantyEnd(Date warrantyEnd) { this.warrantyEnd = warrantyEnd; }
}
