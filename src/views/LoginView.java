package views;

import javax.swing.*;
import java.awt.*;
import controllers.LoginController;

public class LoginView {
    private LoginController controller;
    private JFrame frame;
    private JTextField accountField;
    private JPasswordField passwordField;

    public LoginView(LoginController controller) {
        this.controller = controller;
    }

    public void createAndShowGUI() {
        this.frame = new JFrame("Login");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(500, 400);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        this.frame.add(mainPanel);
        this.placeComponents(mainPanel);

        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        // Account Name label
        JLabel accountLabel = new JLabel("Account Name:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(accountLabel, constraints);

        // Account Name field
        accountField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(accountField, constraints);

        // Password label
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, constraints);

        // Password field
        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, constraints);

        // Login button
        JButton loginButton = new JButton("Login");
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = accountField.getText();
        char[] password = passwordField.getPassword();
        boolean loginSuccessful = controller.login(username, new String(password));

        if (loginSuccessful) {
            JOptionPane.showMessageDialog(frame, "Login successful! Welcome to the system.");
        } else {
            JOptionPane.showMessageDialog(frame, "Login failed. Please check your credentials and try again.");
        }
    }

    public void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }
}