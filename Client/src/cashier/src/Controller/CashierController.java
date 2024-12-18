package cashier.src.Controller;

import cashier.src.DataAdapter.InventoryDataAdapter;
import cashier.src.DataAdapter.OrderDataAdapter;
import cashier.src.DataAdapter.ProductDataAdapter;
import cashier.src.Model.Order;
import cashier.src.Model.OrderItem;
import cashier.src.Model.Order_;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class CashierController {

    private final InventoryDataAdapter inventoryDataAdapter;
    private final OrderDataAdapter orderDataAdapter;
    private final ProductDataAdapter productDataAdapter;

    public CashierController() {
        this.inventoryDataAdapter = new InventoryDataAdapter();
        this.orderDataAdapter = new OrderDataAdapter();
        this.productDataAdapter = new ProductDataAdapter();
    }

    public static List<OrderItem> addItem(List<OrderItem> itemList, OrderItem orderItem) {
        itemList.add(orderItem);
        return itemList;
    }

    public OrderItem newOrderItem(int productID, int quantity) {
        return OrderDataAdapter.createOrderItem(productID, quantity);
    }

    public String createNewOrder(int customerID, int shipperID, String paymentStatus, List<OrderItem> orderItems) {
        // Current time
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Calculate total amount
        double totalAmount = 0;
        for (OrderItem item : orderItems) {
            totalAmount += item.getPrice() * item.getQuantity();
        }

        // Create a new Order object
        Order_ newOrder = new Order_();
        newOrder.setTime(time);
        newOrder.setTotalAmount(totalAmount);
        newOrder.setCustomerID(customerID);
        newOrder.setStatus(paymentStatus);
        newOrder.setShipperID(shipperID);
        newOrder.setOrderItems(orderItems);

        // Call the data adapter to save the order and retrieve the order ID
        int orderID = OrderDataAdapter.createNewOrder(newOrder);

        // Generate the invoice string
        StringBuilder invoice = new StringBuilder();
        invoice.append(String.format("Invoice for Order ID: %d\n", orderID));
        invoice.append(String.format("Customer ID: %d\n", customerID));
        invoice.append(String.format("Shipper ID: %d\n", shipperID));
        invoice.append(String.format("Order Time: %s\n", time));
        invoice.append(String.format("Payment Status: %s\n", paymentStatus));
        invoice.append(String.format("Total Amount: $%.2f\n", totalAmount));
        invoice.append("\nOrder Items:\n");

        for (OrderItem item : orderItems) {
            invoice.append("Product_id: " + item.getProductID());
            invoice.append(String.format("- %s (x%d) at $%.2f each\n",
                    ProductDataAdapter.getProductName(item.getProductID()),
                    item.getQuantity(),
                    item.getPrice() * item.getQuantity()));

        }

        return invoice.toString();
    }

    public void updateDebtPayment(int orderID) {
        OrderDataAdapter.updatePaymentMethod(orderID, "Paid");
    }

    public boolean updateShipper(int orderID, int shipperID) {
        try {
            OrderDataAdapter.updateShipperID(orderID, shipperID);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update shipper for Order ID " + orderID + ": " + e.getMessage());
            return false;
        }
    }

    public void showUpdateShippingDialog(JFrame frame) {
        JTextField orderIDField = new JTextField();
        JTextField shipperIDField = new JTextField();

        Object[] message = {
                "Order ID:", orderIDField,
                "Shipper ID:", shipperIDField
        };

        int option = JOptionPane.showConfirmDialog(frame, message, "Update Shipping", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int orderID = Integer.parseInt(orderIDField.getText());
                int shipperID = Integer.parseInt(shipperIDField.getText());
                OrderDataAdapter.updateShipperID(orderID, shipperID);
                JOptionPane.showMessageDialog(frame, "Shipping updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<Order> getAllOrders() {
        return OrderDataAdapter.getAllOrders();
    }

    public void addInventory(int productID, int quantity, String date, int supplierID) {
        InventoryDataAdapter.addInventory(productID, quantity, date, supplierID);
    }

    public double print_price_for_each_product(int productID) {
        Map<String, Object> productDetails = ProductDataAdapter.getProductByID(productID);
        if (productDetails != null && productDetails.containsKey("price")) {
            double price = (double) productDetails.get("price");
            System.out.printf("Product ID: %d, Price: %.2f\n", productID, price);
            return price;

        } else {
            System.out.printf("Product ID %d not found or price unavailable.\n", productID);
            return 0;
        }

    }

    public boolean updateOrderStatus(int orderID, String status) {
        try {
            OrderDataAdapter.updatePaymentMethod(orderID, status);
            System.out.printf("Order ID %d status updated to: %s\n", orderID, status);
            return true;
        } catch (Exception e) {
            System.err.printf("Failed to update status for Order ID %d: %s\n", orderID, e.getMessage());
            return false;
        }
    }

}
