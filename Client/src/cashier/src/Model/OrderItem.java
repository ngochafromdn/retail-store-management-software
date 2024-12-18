package cashier.src.Model;

public class OrderItem {
    private int productID;
    private int quantity;
    private double price;

    // Default constructor
    public OrderItem() {
        this.productID = 0;
        this.quantity = 0;
        this.price = 0.0;
    }

    // Parameterized constructor
    public OrderItem(int productID, int quantity, double price) {
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
    }

    // Getter and Setter for productID
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    // Getter and Setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productID=" + productID +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
