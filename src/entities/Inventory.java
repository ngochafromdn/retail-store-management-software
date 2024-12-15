package entities;

import java.util.Date;

public class Inventory {
    private int inventoryID;
    private int productID;
    private int quantityReceived;
    private Date date;
    private int supplierID;

    // Default constructor
    public Inventory() {
    }

    // Parameterized constructor
    public Inventory(int inventoryID, int productID, int quantityReceived, Date date, int supplierID) {
        this.inventoryID = inventoryID;
        this.productID = productID;
        this.quantityReceived = quantityReceived;
        this.date = date;
        this.supplierID = supplierID;
    }

    // Getters and Setters
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    // Optional: toString method for easy printing
    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryID=" + inventoryID +
                ", productID=" + productID +
                ", quantityReceived=" + quantityReceived +
                ", date=" + date +
                ", supplierID=" + supplierID +
                '}';
    }
}
