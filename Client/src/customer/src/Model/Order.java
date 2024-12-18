package customer.src.Model;

import java.util.List;

public class Order {
    private String id; // For "_id.$oid"
    private int orderID;
    private String time;
    private double totalAmount;
    private int customerID;
    private String status;
    private int shipperID;
    private List<OrderItem> orderItems;

    // Getters and setters (unchanged from your original code)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getShipperID() {
        return shipperID;
    }

    public void setShipperID(int shipperID) {
        this.shipperID = shipperID;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    // Inner static Builder class
    public static class Builder {
        private String id;
        private int orderID;
        private String time;
        private double totalAmount;
        private int customerID;
        private String status;
        private int shipperID;
        private List<OrderItem> orderItems;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setOrderID(int orderID) {
            this.orderID = orderID;
            return this;
        }

        public Builder setTime(String time) {
            this.time = time;
            return this;
        }

        public Builder setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder setCustomerID(int customerID) {
            this.customerID = customerID;
            return this;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder setShipperID(int shipperID) {
            this.shipperID = shipperID;
            return this;
        }

        public Builder setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        
        public Order build() {
            Order order = new Order();
            order.setId(this.id);
            order.setOrderID(this.orderID);
            order.setTime(this.time);
            order.setTotalAmount(this.totalAmount);
            order.setCustomerID(this.customerID);
            order.setStatus(this.status);
            order.setShipperID(this.shipperID);
            order.setOrderItems(this.orderItems);
            return order;
        }
    }
}
