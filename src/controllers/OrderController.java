package controllers;

import views.OrderView;
import dataaccess.DataAccess;
//import dataaccess.MongoDBAccess;
import java.util.List;

import entities.*;

import javax.swing.*;

public class OrderController {
    private OrderView orderView;
    private final String customerPhone;
    private final DataAccess dataAccess;
//    private final MongoDBAccess orderAccess;
    private int currentOrderId;

    public OrderController(String customerPhone) {
        this.customerPhone = customerPhone;
        this.dataAccess = new DataAccess();
//        this.orderAccess = new MongoDBAccess();
        System.out.println("OrderController created with phone: " + customerPhone);
    }

    public void initialize() {
        try {
            System.out.println("Initializing OrderController...");
            currentOrderId = createNewOrder();

            if (currentOrderId != -1) {
                System.out.println("Order created successfully with ID: " + currentOrderId);

                try {
                    orderView = new OrderView(this);
                    orderView.createAndShowGUI();
                    orderView.displayNewOrderMessage(currentOrderId);
                    System.out.println("OrderView created and displayed");
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Error creating order view: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                System.err.println("Failed to create new order");
                JOptionPane.showMessageDialog(null,
                        "Failed to create new order. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error initializing order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int createNewOrder() {
        try {
            Customer customer = dataAccess.findCustomerByPhoneNumber(customerPhone);
            if (customer == null) {
                System.err.println("Customer not found for phone: " + customerPhone);
                return -1;
            }
            int orderId = dataAccess.createNewOrder(customerPhone);
            System.out.println("Created new order with ID: " + orderId);
            return orderId;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error creating new order: " + e.getMessage());
            return -1;
        }
    }

    public String addOrderItem(int productId, int quantity) {
        Product product = dataAccess.getProductById(productId);

        if (product == null) {
            return "Product not found";
        }

        double totalPrice = product.getUnitPrice() * quantity;
        dataAccess.addOrderItem(productId, quantity, currentOrderId);

        return String.format("Product: %s | Quantity: %d | Price per unit: %.2f | Total price for this item: %.2f",
                product.getName(), quantity, product.getUnitPrice(), totalPrice);
    }

    public String[] completeOrder(String status) {
        Order order = dataAccess.getOrderById(currentOrderId);
        if (order == null) {
            return new String[]{"Order not found"};
        }

        return new String[]{
                String.format("Order completed with status: %s", status),
                String.format("%.2f", order.getTotalAmount()),
                order.getTime().toString()
        };
    }

    public void handleExitButton() {
        orderView.dispose();

    }

    public boolean isValidOrder() {
        return currentOrderId != -1;
    }

    public int getCurrentOrderId() {
        return currentOrderId;
    }

    public String addOrderItem(int productId, int quantity, int orderId) {
        Product product = this.dataAccess.getProductById(productId);

        if (product == null) {
            return "Product not found";
        } else {
            double totalPrice = product.getUnitPrice() * (double) quantity;
            this.dataAccess.addOrderItem(productId, quantity, orderId);

            // Build a message string
            String message = "Product: " + product.getName()
                    + " | Quantity: " + quantity
                    + " | Price per unit: " + product.getUnitPrice()
                    + " | Total price for this item: " + totalPrice;

            // Print the message
            System.out.println(message);

            // Return the message
            return message;
        }
    }

    public String[] completeOrder(int orderId, String status) {
        Order order = this.dataAccess.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found");
            return new String[]{"Order not found"};
        } else {
            String[] orderDetails = new String[5];
            orderDetails[0] = "Order ID: " + order.getOrderId();
            orderDetails[1] = "Date: " + String.valueOf(order.getTime());
            orderDetails[2] = "Total Amount: " + order.getTotalAmount();
            orderDetails[3] = "Customer Phone: " + this.customerPhone;
            orderDetails[4] = "Order Status: " + status;

            List<OrderItem> orderItems = this.dataAccess.getAllOrderItemByID(orderId);
            orderAccess.insertOrder(orderId, this.customerPhone, order.getTotalAmount(), status, orderItems);

            return orderDetails;
        }
    }

    // Function to mark the debt as paid
    public boolean payTheDebt(int orderId) {
        boolean success = dataAccess.changeDebtToPaid(orderId);
        return success;
    }
}
