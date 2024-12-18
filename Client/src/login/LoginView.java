package login;

import utils.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LoginView extends JFrame {
    private final JTextField accountIDField;
    private final JPasswordField passwordField;
    private final LoginController loginController;

    public LoginView() {
        loginController = new LoginController();

        // Set up the JFrame using UIUtils
        UIUtils.setFrameDefaults(this, "Login", 1000, 800);
        setResizable(false);

        // Create main panel with padding
        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 40, 40, 40);
        mainPanel.setLayout(new GridBagLayout());

        // Create components using UIUtils
        JLabel titleLabel = UIUtils.createHeaderLabel("Welcome Back");
        JLabel subtitleLabel = UIUtils.createLabel("Please login to continue");
        accountIDField = UIUtils.createTextField();
        passwordField = UIUtils.createPasswordField();
        JButton loginButton = UIUtils.createButton("Login");

        // Layout setup
        GridBagConstraints gbc = UIUtils.createGBC(0, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 5, 30, 5);
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(UIUtils.createLabel("Type your account name:"), gbc);

        gbc.gridy = 3;
        mainPanel.add(accountIDField, gbc);

        gbc.gridy = 4;
        mainPanel.add(UIUtils.createLabel("Password:"), gbc);

        gbc.gridy = 5;
        mainPanel.add(passwordField, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(30, 5, 5, 5);
        mainPanel.add(loginButton, gbc);

        // Add action listener
        loginButton.addActionListener(e -> handleLogin());

        // Add panel to frame
        add(mainPanel);
        setVisible(true);
    }

    public static void runner() {
        SwingUtilities.invokeLater(LoginView::new);
    }

    private void handleLogin() {
        try {
            String accountName = accountIDField.getText().trim();
            String password = new String(passwordField.getPassword());

            Map<String, Object> accountInfo = loginController.validateLogin(accountName, password);

            if (accountInfo != null) {
                String accountType = (String) accountInfo.get("accountType");
                String accountID_ = (String) accountInfo.get("accountID");
                int accountID = Integer.parseInt(accountID_);
                redirectBasedOnAccountType(accountID, accountType);
            } else {
                showErrorDialog("Invalid credentials. Please try again.");
            }
        } catch (NumberFormatException ex) {
            showErrorDialog("Please enter a valid Account ID");
        }
    }

    private void redirectBasedOnAccountType(int accountID, String accountType) {
        UserRoleInterface userRole;

        switch (accountType.toLowerCase()) {
            case "customer":
                userRole = new CustomerRole();
                break;
            case "manager":
                userRole = new ManagerRole();
                break;
            case "cashier":
                userRole = new CashierRole();
                break;
            default:
                JOptionPane.showMessageDialog(null,
                        "Unknown account type: " + accountType,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
        }

        // Perform the action based on user role
        userRole.performAction(accountID);

        // Close the login window
        this.dispose();
    }

    // Custom error dialog
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
