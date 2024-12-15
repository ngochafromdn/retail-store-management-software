package views;

import controllers.OrderController;
import javax.swing.*;
        import java.awt.*;

public class OrderView {
    private final OrderController controller;
    private JFrame frame;
    private JTextField productIdField;
    private JTextField quantityField;
    private JTextArea orderSummaryArea;
    private JButton addButton, debtButton, paidButton, exitButton;

    public OrderView(OrderController controller) {
        this.controller = controller;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Order System");
        frame.setSize(500, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

        setupComponents();
        frame.setVisible(true);
        setupListeners();
    }

    private void setupComponents() {
        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = createStyledLabel("Order Management", 20, Font.BOLD);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = createInputPanel();
        titlePanel.add(inputPanel, BorderLayout.SOUTH);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Order Summary Area
        orderSummaryArea = createOrderSummaryArea();
        frame.add(new JScrollPane(orderSummaryArea), BorderLayout.CENTER);

        // Button Panel
        frame.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        inputPanel.add(createStyledLabel("Product ID:", 14, Font.PLAIN));
        productIdField = new JTextField();
        inputPanel.add(productIdField);

        inputPanel.add(createStyledLabel("Quantity:", 14, Font.PLAIN));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        return inputPanel;
    }

    private JTextArea createOrderSummaryArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setEditable(false);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(Color.GRAY, 1)
        ));
        return area;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        addButton = createStyledButton("Add Item");
        debtButton = createStyledButton("Debt");
        paidButton = createStyledButton("Paid");
        exitButton = createStyledButton("Exit");
        exitButton.setEnabled(false);

        buttonPanel.add(addButton);
        buttonPanel.add(debtButton);
        buttonPanel.add(paidButton);
        buttonPanel.add(exitButton);

        return buttonPanel;
    }

    private void setupListeners() {
        addButton.addActionListener(e -> addOrderItem());
        debtButton.addActionListener(e -> completeOrder("Debt"));
        paidButton.addActionListener(e -> completeOrder("Paid"));
        exitButton.addActionListener(e -> controller.handleExitButton());
    }

    private JLabel createStyledLabel(String text, int size, int style) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", style, size));
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(173, 216, 230));
        button.setFocusPainted(false);
        return button;
    }

    private void addOrderItem() {
        try {
            int productId = Integer.parseInt(productIdField.getText().trim());
            int quantity = Integer.parseInt(quantityField.getText().trim());

            String result = controller.addOrderItem(productId, quantity);
            orderSummaryArea.append(result + "\n");

            // Clear input fields
            productIdField.setText("");
            quantityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter valid numbers for Product ID and Quantity.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void completeOrder(String status) {
        try {
            String[] orderDetails = controller.completeOrder(status);

            orderSummaryArea.append("\n=== Order Complete ===\n");
            orderSummaryArea.append(orderDetails[0] + "\n");
            orderSummaryArea.append("Total Amount: $" + orderDetails[1] + "\n");
            orderSummaryArea.append("Order Time: " + orderDetails[2] + "\n");

            // Disable order-related buttons
            addButton.setEnabled(false);
            debtButton.setEnabled(false);
            paidButton.setEnabled(false);
            exitButton.setEnabled(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Error completing the order: " + e.getMessage(),
                    "Order Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void displayNewOrderMessage(int orderId) {
        orderSummaryArea.append("New order created with Order ID: " + orderId + "\n\n");
    }

    public void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }
}