//package controllers;
//
//import views.MainScreenView;
//import javax.swing.SwingUtilities;
//
//public class CashierController {
//    private MainScreenView cashierView;
//
//    public void initialize() {
//        SwingUtilities.invokeLater(() -> {
//            cashierView = new MainScreenView(this);
//            cashierView.createAndShowGUI();
//        });
//    }
//
//    public void handleViewInventory() {
//        cashierView.dispose();
//        new InventoryController().initialize();
//    }
//
//    public void handleProcessOrder() {
//        cashierView.dispose();
//        new CustomerController().initialize();
//    }
//
//    public void handleLogout() {
//        cashierView.dispose();
////        new LoginController().initialize();
//    }
//}
