package controllers;

import views.InitialOrderView;
import dataaccess.DataAccess;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;


public class InitialOrderController {
    private InitialOrderView view;
    private final String customerPhone;
    private final DataAccess dataAccess;

    public InitialOrderController(String customerPhone) {
        this.customerPhone = customerPhone;
        this.dataAccess = new DataAccess();


    }

    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            view = new InitialOrderView(this);
            view.createAndShowGUI();
        });
    }

    public void handleNewOrder() {
        try {
            // First, dispose of the current view
            if (view != null) {
                view.dispose();
            }

            // Create and initialize OrderController
            OrderController orderController = new OrderController(customerPhone);


            // Verify customer phone is not null
            if (customerPhone == null || customerPhone.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Error: Customer phone number is missing.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            orderController.initialize();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error creating new order: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean handleDebtPayment(int orderId) {
        try {
            return dataAccess.changeDebtToPaid(orderId);
        } catch (Exception e) {
            System.err.println("Error processing debt payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void handleReturnToEmployee() {
        if (view != null) {
            view.dispose();
        }
        new EmployeeController().initialize();
    }

    public String getCustomerPhone() {
        return customerPhone;
    }
}