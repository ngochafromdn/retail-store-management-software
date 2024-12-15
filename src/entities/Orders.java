package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order placed by a customer.
 */
public class Orders {
    private int orderId;                  // Unique identifier for the order
    private LocalDateTime time;           // Time when the order was placed
    private double totalAmount;            // Total amount of the order
    private int customerId;             // ID of the customer who placed the order
    private String status;                 // Status of the order (e.g., "Pending", "Completed")
    private List<OrderItem> orderItems;   // List of items in the order

    /**
     * Constructor with meaningful parameter names.
     *
     * @param orderId      Unique identifier for the order
     * @param time         Time when the order was placed
     * @param totalAmount  Total amount of the order
     * @param customerId   ID of the customer who placed the order
     * @param status       Status of the order
     * @param orderItems   List of items in the order
     */
    public Orders(int orderId, LocalDateTime time, double totalAmount, int customerId, String status, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.time = time;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
        this.status = status;
        this.orderItems = new ArrayList<>(orderItems); // Initialize with provided order items
    }

    // Default constructor
    public Orders() {
        this.orderId = 0;                  // Default value for order ID
        this.time = LocalDateTime.now();    // Default to current time
        this.totalAmount = 0.0;             // Default value for total amount
        this.customerId = 1;                // Default value for customer ID
        this.status = "Pending";             // Default status
        this.orderItems = new ArrayList<>(); // Initialize the order items list
    }

    // Getters
    public int getOrderId() {
        return this.orderId;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public String getStatus() {
        return this.status;
    }

    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(this.orderItems); // Return a copy to prevent external modification
    }

    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative.");
        }
        this.totalAmount = totalAmount;
    }

    public void setCustomerId(int customerId) {
        if (customerId == 0 ) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty.");
        }
        this.customerId = customerId;
    }

    public void setStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty.");
        }
        this.status = status;
    }

    // Method to add an order item
    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        this.totalAmount += item.getPrice() * item.getQuantity(); // Update total amount
    }

    // Method to remove an order item
    public void removeOrderItem(OrderItem item) {
        if (this.orderItems.remove(item)) {
            this.totalAmount -= item.getPrice() * item.getQuantity(); // Update total amount
        }
    }

    // Override toString method for better representation
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", time=" + time +
                ", totalAmount=" + totalAmount +
                ", customerId='" + customerId + '\'' +
                ", status='" + status + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}