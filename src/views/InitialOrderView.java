package views;

import controllers.InitialOrderController;
import javax.swing.*;
import java.awt.*;

public class InitialOrderView {
    private final InitialOrderController controller;
    private JFrame frame;
    private JTextField orderIdField;
    private JPanel orderIdPanel;
    private JButton payOldDebtButton;
    private JButton buyNewButton;
    private JButton submitOrderIdButton;

    public InitialOrderView(InitialOrderController controller) {
        this.controller = controller;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Select Action");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        setupComponents();
        setupListeners();
        frame.setVisible(true);
    }

    private void setupComponents() {
        // Title Panel
        JPanel titlePanel = createTitlePanel();
        frame.add(titlePanel, BorderLayout.NORTH);

        // Main Button Panel
        JPanel buttonPanel = createButtonPanel();
        frame.add(buttonPanel, BorderLayout.CENTER);

        // Order ID Panel (initially hidden)
        orderIdPanel = createOrderIdPanel();
        frame.add(orderIdPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Choose Your Action", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        return titlePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        payOldDebtButton = createStyledButton("Pay Old Debt");
        buyNewButton = createStyledButton("Buy New");

        buttonPanel.add(payOldDebtButton);
        buttonPanel.add(Box.createVerticalStrut(15));
        buttonPanel.add(buyNewButton);

        return buttonPanel;
    }

    private JPanel createOrderIdPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel orderIdLabel = new JLabel("Enter Order ID:");
        orderIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(orderIdLabel);

        orderIdField = new JTextField(10);
        panel.add(orderIdField);

        submitOrderIdButton = createStyledButton("Submit");
        panel.add(submitOrderIdButton);

        panel.setVisible(false);
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(173, 216, 230));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void setupListeners() {
        payOldDebtButton.addActionListener(e -> {
            orderIdPanel.setVisible(true);
            frame.pack();
            frame.setLocationRelativeTo(null);
        });

        buyNewButton.addActionListener(e -> controller.handleNewOrder());

        submitOrderIdButton.addActionListener(e -> handleDebtPayment());
    }

    private void handleDebtPayment() {
        String orderIdText = orderIdField.getText().trim();

        if (orderIdText.isEmpty()) {
            showError("Please enter an Order ID.");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdText);
            boolean success = controller.handleDebtPayment(orderId);

            if (success) {
                JOptionPane.showMessageDialog(frame,
                        "Debt has been paid successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                controller.handleReturnToEmployee();
            } else {
                showError("Error processing payment. Please check the Order ID and try again.");
            }
        } catch (NumberFormatException ex) {
            showError("Please enter a valid Order ID (numbers only).");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }
}