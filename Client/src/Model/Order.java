package Model;

public class Order {
    private int orderID;
    private String time;
    private double totalAmount;
    private int customerID;
    private String status;
    private int shipperID;

    public Order() {
        this.orderID = 0;
        this.time = "";
        this.totalAmount = 0.0;
        this.customerID = 0;
        this.status = "";
        this.shipperID = 0;
    }

    public Order(int orderID, String time, double totalAmount, int customerID, String status, int shipperID) {
        this.orderID = orderID;
        this.time = time;
        this.totalAmount = totalAmount;
        this.customerID = customerID;
        this.status = status;
        this.shipperID = shipperID;
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

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", time='" + time + '\'' +
                ", totalAmount=" + totalAmount +
                ", customerID=" + customerID +
                ", status='" + status + '\'' +
                ", shipperID=" + shipperID +
                '}';
    }
}
