package cashier.src.View;

import cashier.src.Controller.CashierController;
import cashier.src.Model.OrderItem;
import utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CashierView extends JFrame {
    private final CashierController controller;

    public CashierView(CashierController controller) {
        this.controller = controller;
    }

    public static void run() {
        SwingUtilities.invokeLater(() -> {
            CashierController controller = new CashierController();
            CashierView view = new CashierView(controller);
            view.createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        // Set up frame properties using UIUtils
        UIUtils.setFrameDefaults(this, "Cashier Dashboard", 1200, 800);
        setLayout(new BorderLayout());

        // Add header panel
        add(UIUtils.createHeaderPanel("Cashier Dashboard"), BorderLayout.NORTH);

        // Create tabbed pane with padding
        JPanel contentPanel = UIUtils.createPanelWithPadding(20, 20, 20, 20);
        contentPanel.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = UIUtils.createTabbedPane();
        tabbedPane.addTab("Add Inventory", createAddInventoryPanel());
        tabbedPane.addTab("Create Order", createAddOrderPanel());
        tabbedPane.addTab("Update Payment", createUpdatePaymentPanel());
        tabbedPane.addTab("Update Shipping", createUpdateShippingPanel());

        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createAddInventoryPanel() {
        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 40, 40, 40);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Form panel
        JPanel formPanel = UIUtils.createPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = UIUtils.createGBC(0, 0);

        // Create form fields
        JTextField productIDField = UIUtils.createTextField();
        JTextField quantityField = UIUtils.createTextField();
        JTextField supplierIDField = UIUtils.createTextField();

        // Add components
        addFormField(formPanel, "Product ID:", productIDField, gbc, 0);
        addFormField(formPanel, "Quantity:", quantityField, gbc, 1);
        addFormField(formPanel, "Supplier ID:", supplierIDField, gbc, 2);

        // Button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton addButton = UIUtils.createButton("Add Inventory");
        addButton.addActionListener(e -> handleAddInventory(productIDField, quantityField, supplierIDField));
        buttonPanel.add(addButton);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        return mainPanel;
    }

    private JPanel createAddOrderPanel() {
        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 40, 40, 40);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Form panel
        JPanel formPanel = UIUtils.createPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = UIUtils.createGBC(0, 0);

        // Create form fields
        JTextField customerIDField = UIUtils.createTextField();
        JTextField shipperIDField = UIUtils.createTextField();
        JComboBox<String> paymentStatusDropdown = UIUtils.createComboBox(new String[]{"Paid", "Debt"});

        // Add components
        addFormField(formPanel, "Customer ID:", customerIDField, gbc, 0);
        addFormField(formPanel, "Shipper ID:", shipperIDField, gbc, 1);
        addFormField(formPanel, "Payment Status:", paymentStatusDropdown, gbc, 2);

        // Button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton createOrderButton = UIUtils.createButton("Create Order");
        createOrderButton.addActionListener(e -> handleCreateOrder(customerIDField, shipperIDField, paymentStatusDropdown));
        buttonPanel.add(createOrderButton);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        return mainPanel;
    }

    private JPanel createUpdatePaymentPanel() {
        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 40, 40, 40);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Form panel
        JPanel formPanel = UIUtils.createPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = UIUtils.createGBC(0, 0);

        // Create form field
        JTextField orderIDField = UIUtils.createTextField();
        addFormField(formPanel, "Order ID:", orderIDField, gbc, 0);

        // Button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton updateButton = UIUtils.createButton("Update Payment");
        updateButton.addActionListener(e -> handleUpdatePayment(orderIDField));
        buttonPanel.add(updateButton);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        return mainPanel;
    }

    private JPanel createUpdateShippingPanel() {
        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 40, 40, 40);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Form panel
        JPanel formPanel = UIUtils.createPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = UIUtils.createGBC(0, 0);

        // Create form fields
        JTextField orderIDField = UIUtils.createTextField();
        JTextField shipperIDField = UIUtils.createTextField();

        // Add components
        addFormField(formPanel, "Order ID:", orderIDField, gbc, 0);
        addFormField(formPanel, "New Shipper ID:", shipperIDField, gbc, 1);

        // Button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton updateButton = UIUtils.createButton("Update Shipping");
        updateButton.addActionListener(e -> handleUpdateShipping(orderIDField, shipperIDField));
        buttonPanel.add(updateButton);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        return mainPanel;
    }

    // Helper method to add form fields consistently
    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int gridy) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        panel.add(UIUtils.createLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    // Handler methods for actions
    private void handleAddInventory(JTextField productIDField, JTextField quantityField, JTextField supplierIDField) {
        try {
            String date = java.time.LocalDate.now().toString();
            int productID = Integer.parseInt(productIDField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            int supplierID = Integer.parseInt(supplierIDField.getText());

            controller.addInventory(productID, quantity, date, supplierID);
            UIUtils.showSuccess(this, "Inventory added successfully!");
            clearFields(productIDField, quantityField, supplierIDField);
        } catch (Exception ex) {
            UIUtils.showError(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void handleCreateOrder(JTextField customerIDField, JTextField shipperIDField, JComboBox<String> paymentStatusDropdown) {
        try {
            int customerID = Integer.parseInt(customerIDField.getText());
            int shipperID = Integer.parseInt(shipperIDField.getText());
            String paymentStatus = (String) paymentStatusDropdown.getSelectedItem();
            List<OrderItem> orderItems = new ArrayList<>();

            while (true) {
                JPanel inputPanel = UIUtils.createPanel();
                inputPanel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = UIUtils.createGBC(0, 0);

                JTextField productIDField = UIUtils.createTextField();
                JTextField quantityField = UIUtils.createTextField();

                addFormField(inputPanel, "Product ID:", productIDField, gbc, 0);
                addFormField(inputPanel, "Quantity:", quantityField, gbc, 1);

                // Custom button labels: "Next" and "Complete"
                Object[] options = {"Next", "Complete"};
                int option = JOptionPane.showOptionDialog(
                        this,                    // Parent component
                        inputPanel,              // Content
                        "Add Product",           // Title
                        JOptionPane.DEFAULT_OPTION, // Option type
                        JOptionPane.PLAIN_MESSAGE,  // Message type
                        null,                    // Icon
                        options,                 // Custom buttons
                        options[0]               // Default button (Next)
                );

                if (option == 1) { // "Complete" button clicked
                    break;
                } else if (option == 0) { // "Next" button clicked
                    int productID = Integer.parseInt(productIDField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    double price = controller.print_price_for_each_product(productID);

                    orderItems.add(new OrderItem(productID, quantity, price));
                    UIUtils.showSuccess(this, "Product ID " + productID + " added to order");
                } else {
                    break; // Dialog closed or unexpected behavior
                }
            }

            if (!orderItems.isEmpty()) {
                String invoice = controller.createNewOrder(customerID, shipperID, paymentStatus, orderItems);
                showInvoice(invoice);
                clearFields(customerIDField, shipperIDField);
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Error creating order: " + ex.getMessage());
        }
    }


    private void handleUpdatePayment(JTextField orderIDField) {
        try {
            int orderID = Integer.parseInt(orderIDField.getText());
            controller.updateDebtPayment(orderID);
            UIUtils.showSuccess(this, "Payment updated successfully!");
            clearFields(orderIDField);
        } catch (Exception ex) {
            UIUtils.showError(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void handleUpdateShipping(JTextField orderIDField, JTextField shipperIDField) {
        try {
            int orderID = Integer.parseInt(orderIDField.getText());
            int shipperID = Integer.parseInt(shipperIDField.getText());

            if (controller.updateShipper(orderID, shipperID)) {
                UIUtils.showSuccess(this, "Shipping information updated successfully!");
                clearFields(orderIDField, shipperIDField);
            } else {
                UIUtils.showError(this, "Failed to update shipping information.");
            }
        } catch (Exception ex) {
            UIUtils.showError(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void showInvoice(String invoice) {
        JTextArea invoiceArea = new JTextArea(invoice);
        invoiceArea.setFont(UIUtils.INPUT_FONT);
        invoiceArea.setEditable(false);
        invoiceArea.setBackground(UIUtils.ACCENT_COLOR);
        invoiceArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(invoiceArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Invoice",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}