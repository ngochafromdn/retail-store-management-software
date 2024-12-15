package controllers;

import views.InventoryAndProductView;
import dataaccess.DataAccess;
import javax.swing.SwingUtilities;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InventoryAndProductController {
    private InventoryAndProductView view;
    private DataAccess dataAccess;
    private String type;

    public InventoryAndProductController(String type) {
        this.dataAccess = new DataAccess();
        this.type = type; // type
    }

    public String getType() {
        return type; //  lấy giá trị của type user
    }

    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            view = new InventoryAndProductView(this);
            view.createAndShowGUI();
        });
    }

    public boolean addInventory(int productId, int quantityReceived, int supplierId) {
        if (quantityReceived <= 0) {
            return false;
        }

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            if (!this.dataAccess.productExists(productId)) {
                return false;
            } else {
                Date inventoryDate = Date.valueOf(currentDate);
                return this.dataAccess.addInventory(productId, supplierId, quantityReceived, inventoryDate);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            return false;
        }
    }

    public String[] getAllSuppliers() {
        List supplierList = dataAccess.getAllSuppliers();
        return (String[]) supplierList.toArray(new String[0]);
    }

    public int getSupplierIDByName(String supplierName) {
        return dataAccess.getSupplierIDByName(supplierName);
    }

    public String[] viewAllInventory() {
        return dataAccess.getAllInventory();
    }

    public List<String> getAllProductDetails() {
        return dataAccess.getAllProducts();
    }

    public void handleExitButton(String type) {
        view.dispose();
        if (type == "employee") {
            new EmployeeController().initialize();
        } else {
            new ManagerController().initialize();
        }
    }
}