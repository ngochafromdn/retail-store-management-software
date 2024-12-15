package controllers;

import views.LoginView;
import dataaccess.DataAccess;
import entities.Account;
import javax.swing.SwingUtilities;

public class LoginController {
    private DataAccess dataAccess;
    private LoginView loginView;

    public LoginController() {
        this.dataAccess = new DataAccess();
    }

    public void initialize() {
        SwingUtilities.invokeLater(() -> {
            loginView = new LoginView(this);
            loginView.createAndShowGUI();
        });
    }

    public boolean login(String accountName, String password) {
        Account account = this.dataAccess.getAccountByAccountName(accountName);

        if (account != null && account.getPassword().equals(password)) {
            onLoginSuccess(account);
            return true;
        }
        return false;
    }

    private void onLoginSuccess(Account account) {
        // Check the AccountType and navigate to the appropriate controller
        String accountType = account.getAccountType();
        String accountName = account.getAccountName();
        int accountId = account.getAccountID();
        System.out.println(accountType);

        switch (accountType) {
            case "employee":
                new EmployeeController().initialize();
                break;
            case "manager":
                new ManagerController().initialize();
                break;
            case "customer":
                String phone_number = this.dataAccess.getCustomerPhoneByAccount(accountId);
                System.out.println(phone_number);
                new CustomerScreenController(phone_number).initialize();
                break;
            // Add more cases as needed for different account types
            default:
                System.out.println("Unknown account type: " + accountType);
                break;
        }

        loginView.dispose(); // Close the login view after successful login
    }
}