package controllers;

import views.EmployeeView;
import javax.swing.SwingUtilities;
import dataaccess.DataAccess;

public class EmployeeController {
    private EmployeeView employeeView;

    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            employeeView = new EmployeeView(this);
            employeeView.createAndShowGUI();
        });
    }

    public void handleViewInventory() {
        employeeView.dispose();
        new InventoryAndProductController("employee").initialize();
    }

    public void handleProcessOrder() {
        employeeView.dispose();
        new CustomerController().initialize();
    }

    public void handleLogout() {
        employeeView.dispose();
        new LoginController().initialize();
    }
}