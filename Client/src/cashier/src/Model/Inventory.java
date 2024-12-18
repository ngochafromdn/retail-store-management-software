package cashier.src.Model;

public class Inventory {
    private int inventoryID;
    private int productID;
    private int quantityReceived;
    private String date;
    private int supplierID;

    public Inventory() {
        this.inventoryID = 0;
        this.productID = 0;
        this.quantityReceived = 0;
        this.date = "";
        this.supplierID = 0;
    }

    public Inventory(int inventoryID, int productID, int quantityReceived, String date, int supplierID) {
        this.inventoryID = inventoryID;
        this.productID = productID;
        this.quantityReceived = quantityReceived;
        this.date = date;
        this.supplierID = supplierID;
    }

    public int getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(int quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryID=" + inventoryID +
                ", productID=" + productID +
                ", quantityReceived=" + quantityReceived +
                ", date='" + date + '\'' +
                ", supplierID=" + supplierID +
                '}';
    }
}
