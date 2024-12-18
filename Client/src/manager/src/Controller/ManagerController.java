package manager.src.Controller;

import manager.src.DataAdapter.InventoryAdapter;
import manager.src.DataAdapter.OrderAdapter;
import manager.src.Model.Inventory;
import manager.src.Model.Order;
import manager.src.View.ManagerView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerController {
    private final ManagerView view;
    OrderAdapter order_adapter = new OrderAdapter();

    public ManagerController() {
        this.view = new ManagerView(this);
    }

    public static void run() {
        SwingUtilities.invokeLater(() -> {
            ManagerController controller = new ManagerController();
            controller.showUI();
        });
    }

    public void showUI() {
        view.setVisible(true);
    }

    // Called by the view when "View All Inventory" button is clicked
    // Called by the view when "View All Inventory" button is clicked
    public void viewAllInventory() {
        // Fetch inventory data from the adapter
        List<Inventory> inventoryList = InventoryAdapter.getAllInventory();

        if (inventoryList == null || inventoryList.isEmpty()) {
            // Show a message dialog if no inventory is available
            JOptionPane.showMessageDialog(null, "No inventory available to display.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Pass the inventory list to the view for display
            view.showInventoryTable(inventoryList);
        }
    }


    // Called by the view when "View Sale Reports" button is clicked
    public void viewSaleReports() {
        // Get orders as a List<Order> from the OrderAdapter
        List<Order> orders = order_adapter.getAllOrders();

        // Check if the list is empty
        if (orders == null || orders.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No orders available to display.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convert List<Order> to a List<Map<String, Object>> for table display
        List<Map<String, Object>> orderTableData = new ArrayList<>();
        for (Order order : orders) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("OrderID", order.hashCode()); // Generate unique ID if necessary
            orderMap.put("Time", order.getTime());
            orderMap.put("TotalAmount", order.getTotalAmount());
            orderMap.put("CustomerID", order.getCustomerID());
            orderMap.put("Status", order.getStatus());
            orderMap.put("ShipperID", order.getShipperID());
            orderTableData.add(orderMap);
        }

        // Pass the converted data to the view for display
        view.showOrderTable(orderTableData);
    }


    // Called by the view when "Add New Inventory" button is clicked
    public boolean addNewInventory(int productID, int quantityReceived, String date, int supplierID) {
        try {
            InventoryAdapter.addInventory(productID, quantityReceived, date, supplierID);
            JOptionPane.showMessageDialog(null, "Inventory added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception e) {
            System.err.println("Error adding inventory: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to add inventory. Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
