package Model;

import java.util.List;
import java.util.Map;

public class Order {
    private String time;
    private double totalAmount;
    private int customerID;
    private String status;
    private int shipperID;
    private List<Map<String, Object>> orderItems; // List to hold OrderItems as JSON objects

    public Order() {
        this.time = "";
        this.totalAmount = 0.0;
        this.customerID = 0;
        this.status = "";
        this.shipperID = 0;
        this.orderItems = null;
    }

    public Order(String time, double totalAmount, int customerID, String status, int shipperID, List<Map<String, Object>> orderItems) {
        this.time = time;
        this.totalAmount = totalAmount;
        this.customerID = customerID;
        this.status = status;
        this.shipperID = shipperID;
        this.orderItems = orderItems;
    }

    // Factory method for constructing an Order object from a JSON map
    public static Order fromJson(Map<String, Object> jsonObject) {
        String time = jsonObject.get("Time").toString();
        double totalAmount = ((Double) jsonObject.get("TotalAmount"));
        int customerID = ((Double) jsonObject.get("CustomerID")).intValue();
        String status = jsonObject.get("Status").toString();
        int shipperID = ((Double) jsonObject.get("ShipperID")).intValue();
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) jsonObject.get("OrderItems");

        return new Order(time, totalAmount, customerID, status, shipperID, orderItems);
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

    public List<Map<String, Object>> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Map<String, Object>> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "Order{" +
                "time='" + time + '\'' +
                ", totalAmount=" + totalAmount +
                ", customerID=" + customerID +
                ", status='" + status + '\'' +
                ", shipperID=" + shipperID +
                ", orderItems=" + orderItems +
                '}';
    }
}
