package entities;

// OrderItem class to represent individual order items
public class OrderItem {
    private int orderItemId;
    private int productId;
    private int quantity;
    private double price;

    public OrderItem(int orderItemId, int productId, int quantity, double price) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getOrderItemId() {
        return orderItemId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}