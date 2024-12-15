package views;

import controllers.CustomerController;
import javax.swing.*;
import java.awt.*;

public class CustomerView {
    private final CustomerController controller;
    private JFrame frame;
    private JTextField phoneField, nameField, addressField; // Added addressField
    private JButton checkButton, submitButton, nextButton, backButton;
    private JLabel messageLabel;

    public CustomerView(CustomerController controller) {
        this.controller = controller;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Customer View");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel with background color
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Message Label at the top
        setupMessageLabel();
        mainPanel.add(messageLabel, BorderLayout.NORTH);

        // Input Panel in the center
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Button Panel at the bottom
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(240, 248, 255));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        // Phone Number Row
        JLabel phoneLabel = new JLabel("Phone Number:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(phoneLabel, constraints);

        phoneField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        inputPanel.add(phoneField, constraints);

        // Name Row
        JLabel nameLabel = new JLabel("Name:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(nameLabel, constraints);

        nameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        nameField.setVisible(false);
        inputPanel.add(nameField, constraints);

        // Address Row (Optional)
        JLabel addressLabel = new JLabel("Address (Optional):");
        constraints.gridx = 0;
        constraints.gridy = 2;
        inputPanel.add(addressLabel, constraints);

        addressField = new JTextField(20); // Initialize addressField
        constraints.gridx = 1;
        constraints.gridy = 2;
        inputPanel.add(addressField, constraints); // Add addressField to inputPanel

        // Check Button Row
        checkButton = createStyledButton("Check Customer");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        inputPanel.add(checkButton, constraints);

        // Add action listener
        checkButton.addActionListener(e -> checkCustomer());

        return inputPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));

        nextButton = createStyledButton("Next");
        submitButton = createStyledButton("Submit");
        backButton = createStyledButton("Back");

        // Set initial button states
        nextButton.setEnabled(false);
        submitButton.setEnabled(false);

        // Add buttons to panel
        buttonPanel.add(nextButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        // Add action listeners
        submitButton.addActionListener(e -> submitNewCustomer());
        nextButton.addActionListener(e -> controller.handleNextButton());
        backButton.addActionListener(e -> controller.handleBackButton());

        return buttonPanel;
    }

    private void setupMessageLabel() {
        messageLabel = new JLabel("Enter phone number to check customer", SwingConstants.CENTER);
        messageLabel.setForeground(new Color(70, 130, 180));
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(173, 216, 230));
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 149, 237), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    private void checkCustomer() {
        String phoneNumber = phoneField.getText().trim();

        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter a phone number.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean customerExists = controller.checkCustomer(phoneNumber);

        if (customerExists) {
            messageLabel.setText("Customer found!");
            nameField.setVisible(false);
            addressField.setVisible(false); // Hide address field if customer exists
            submitButton.setEnabled(false);
            nextButton.setEnabled(true);
        } else {
            messageLabel.setText("Customer not found. Please enter customer name.");
            nameField.setVisible(true);
            addressField.setVisible(true); // Show address field if customer not found
            submitButton.setEnabled(true);
            nextButton.setEnabled(false);
        }
    }

    private void submitNewCustomer() {
        String phoneNumber = phoneField.getText().trim();
        String name = nameField.getText().trim();
        String address = addressField.getText().trim(); // Get address from addressField

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter a name.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (controller.addCustomer(name, phoneNumber, address)) {
            messageLabel.setText("Customer added successfully!");
            submitButton.setEnabled(false);
            nextButton.setEnabled(true);
        } else {
            messageLabel.setText("Error adding customer. Please try again.");
        }
    }

    public void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }
}