package customer.src.Model;

import java.util.List;

public class Order_ {
    private String time;
    private double totalAmount;
    private int customerID;
    private String status;
    private int shipperID;
    private List<OrderItem> orderItems;

    // Proxy mechanism internal to the Order_ class
    private boolean isAdmin;

    // Constructor
    public Order_() {
        this.isAdmin = false; // Default to regular user, without admin privileges
    }

    public static void main(String[] args) {
        Order_ order = new Order_();

        // Non-admin user attempting to change order status
        System.out.println("Attempting to change status as a regular user:");
        order.setStatus("Shipped");

        // Granting admin privileges and modifying status
        order.setAdmin(true);
        System.out.println("\nAttempting to change status as an admin:");
        order.setStatus("Shipped");
    }

    // Setter for admin flag (can be used to set admin privileges)
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    // Getter for time
    public String getTime() {
        return time;
    }

    // Setter for time
    public void setTime(String time) {
        this.time = time;
    }

    // Getter for totalAmount
    public double getTotalAmount() {
        return totalAmount;
    }

    // Setter for totalAmount
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Getter for customerID
    public int getCustomerID() {
        return customerID;
    }

    // Setter for customerID
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    // Getter for status
    public String getStatus() {
        return status;
    }

    public boolean setStatus(String status) {
        if (isAdmin) {
            if (status.equals("Paid") || status.equals("Debt")) {
                this.status = status;
                System.out.println("Order payment status updated to: " + status);
                return true;  // Status update successful
            } else {
                System.out.println("Invalid status value. Please use 'Paid' or 'Debt'.");
                return false;  // Invalid status value
            }
        } else {
            System.out.println("Permission denied: You cannot modify this order payment status.");
            return false;  // Permission denied
        }
    }


    // Getter for shipperID
    public int getShipperID() {
        return shipperID;
    }

    // Setter for shipperID
    public void setShipperID(int shipperID) {
        this.shipperID = shipperID;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
