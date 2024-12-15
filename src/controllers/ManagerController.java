package controllers;

import views.ManagerView;
import javax.swing.SwingUtilities;

public class ManagerController {
    private ManagerView managerView;

    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            managerView = new ManagerView(this);
            managerView.createAndShowGUI();
        });
    }

    public void handleViewInventory() {
        managerView.dispose();
        new InventoryAndProductController("manager").initialize();
    }

    public void handleProcessOrder() {
        managerView.dispose();
        new CustomerController().initialize();
    }

    public void handleLogout() {
        managerView.dispose();
        new LoginController().initialize();
    }
}