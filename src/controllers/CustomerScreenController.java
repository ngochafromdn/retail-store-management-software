package controllers;

import dataaccess.DataAccess;
import views.CustomerAccountView;
import controllers.CustomerController;

import javax.swing.*;

public class CustomerScreenController {

    private final DataAccess dataAccess;
    private final String phone_number;

    public CustomerScreenController(String Phone_number) {
        this.dataAccess = new DataAccess();
        this.phone_number =  Phone_number;
    }

    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            CustomerAccountView customeraccountView = new CustomerAccountView(phone_number);
        });
    }

    public void handleOrder(String account_name)
    {
    new InitialOrderController(phone_number).initialize();

    }

    public void handleProcessOrder(String accountName) {
        new CustomerController().initialize();
    }

    public void handleViewHistory(String accountName) {


    }

    public void handleUpdateProfile(String accountName) {
        System.out.println("Updating profile for " + accountName);
        // Logic to update the profile
    }

    public void handleLogOut() {
        System.out.println("Customer logged out.");
        // Logic to navigate back to the login screen
    }
}
