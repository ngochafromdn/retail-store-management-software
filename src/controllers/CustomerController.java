package controllers;

import entities.Account;
import views.CustomerView;
import dataaccess.DataAccess;
import javax.swing.SwingUtilities;
import entities.Account;
import entities.Customer;

public class CustomerController {
    private boolean initialized = false;
    private CustomerView customerView;
    private DataAccess dataAccess;
    private String currentPhoneNumber;

    public CustomerController() {
        this.dataAccess = new DataAccess();

    }

    public void initialize() {
        initialized = true;
        SwingUtilities.invokeLater(() -> {
            customerView = new CustomerView(this);
            customerView.createAndShowGUI();
        });
    }

    public boolean checkCustomer(String phoneNumber) {
        this.currentPhoneNumber = phoneNumber;
        return customerExists(phoneNumber);
    }

    public boolean addCustomer(String customerName, String customerPhoneNumber, String address) {
        if (customerExists(customerPhoneNumber)) {
            return false;
        } else {
            dataAccess.addCustomer(customerName, customerPhoneNumber, address);
            return true;
        }
    }

    private boolean customerExists(String customerPhoneNumber) {
        return dataAccess.findCustomerByPhoneNumber(customerPhoneNumber) != null;
    }

    private void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }

    public void handleNextButton() {
        ensureInitialized();
        customerView.dispose();
        new InitialOrderController(currentPhoneNumber).initialize();
    }

    public void handleBackButton() {
        customerView.dispose();
    }
}