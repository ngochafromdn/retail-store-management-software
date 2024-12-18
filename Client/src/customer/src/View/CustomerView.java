package customer.src.View;

import customer.src.Controller.CustomerController;
import customer.src.Model.OrderItem;
import utils.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CustomerView extends JFrame {
    private final CustomerController customerController;
    private final int customerID;
    private JTextArea informationArea;

    public CustomerView(int accountID) {
        this.customerController = new CustomerController(accountID);
        customerController.initializeCustomerInformation();
        this.customerID = customerController.customerID;

        createAndShowGUI();
    }

    public static void run(int accountId) {
        SwingUtilities.invokeLater(() -> new CustomerView(accountId));
    }

    private void createAndShowGUI() {
        // Set up frame properties
        UIUtils.setFrameDefaults(this, "Customer Dashboard", 1200, 800);
        setLayout(new BorderLayout());

        // Add header
        add(UIUtils.createHeaderPanel("Customer Dashboard"), BorderLayout.NORTH);

        // Create main content panel with padding
        JPanel contentPanel = UIUtils.createPanelWithPadding(20, 20, 20, 20);
        contentPanel.setLayout(new BorderLayout());

        // Create and setup tabbed pane
        JTabbedPane tabbedPane = UIUtils.createTabbedPane();
        tabbedPane.addTab("Your Information", createInfoPanel());
        tabbedPane.addTab("Edit Information", createEditPanel());
        tabbedPane.addTab("New Order", createNewOrderPanel());
        tabbedPane.addTab("Order History", createOrderHistoryPanel());


        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createInfoPanel() {
        JPanel panel = UIUtils.createPanelWithPadding(20, 20, 20, 20);
        panel.setLayout(new BorderLayout());

        // Create info area with styling
        informationArea = new JTextArea();
        informationArea.setEditable(false);
        informationArea.setFont(UIUtils.INPUT_FONT);
        informationArea.setBackground(UIUtils.ACCENT_COLOR);
        informationArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        refreshCustomerInformation();

        JScrollPane scrollPane = new JScrollPane(informationArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY_COLOR),
                "Customer Information"
        ));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEditPanel() {
        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 40, 40, 40);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 100, 0, 100));

        // Create buttons
        JButton editNameButton = UIUtils.createButton("Edit Name");
        JButton editNumberButton = UIUtils.createButton("Edit Number");
        JButton editAddressButton = UIUtils.createButton("Edit Address");
        JButton editBankButton = UIUtils.createButton("Edit Bank Info");

        // Add action listeners
        editNameButton.addActionListener(e -> showEditDialog("Edit Name", "Enter new name:", this::updateName));
        editNumberButton.addActionListener(e -> showEditDialog("Edit Number", "Enter new number:", this::updateNumber));
        editAddressButton.addActionListener(e -> showEditDialog("Edit Address", "Enter new address:", this::updateAddress));
        editBankButton.addActionListener(e -> showEditDialog("Edit Bank Info", "Enter new bank info:", this::updateBankInfo));

        // Add buttons to panel
        buttonPanel.add(editNameButton);
        buttonPanel.add(editNumberButton);
        buttonPanel.add(editAddressButton);
        buttonPanel.add(editBankButton);

        mainPanel.add(buttonPanel);
        return mainPanel;
    }

    private JPanel createNewOrderPanel() {
        JPanel mainPanel = UIUtils.createPanelWithPadding(20, 20, 20, 20);
        mainPanel.setLayout(new BorderLayout());

        // Create table
        String[] columnNames = {"Product ID", "Product Name", "Quantity", "Price", "Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable orderTable = new JTable(tableModel);
        orderTable.setFont(UIUtils.INPUT_FONT);
        orderTable.getTableHeader().setFont(UIUtils.LABEL_FONT);

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY_COLOR),
                "Order Items"
        ));

        // Create button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        List<OrderItem> orderItems = new ArrayList<>();

        // Create buttons
        JButton addItemButton = UIUtils.createButton("Add Item");
        JButton removeItemButton = UIUtils.createButton("Remove Selected");
        JButton finalizeOrderButton = UIUtils.createButton("Finalize Order");

        // Add button actions
        addItemButton.addActionListener(e -> handleAddItem(tableModel, orderItems));
        removeItemButton.addActionListener(e -> handleRemoveItem(orderTable, tableModel, orderItems));
        finalizeOrderButton.addActionListener(e -> handleFinalizeOrder(tableModel, orderItems));

        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        buttonPanel.add(finalizeOrderButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void showEditDialog(String title, String message, Consumer<String> updateFunction) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(800, 400);  // Increased dialog size

        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 60, 40, 60);  // Increased padding
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);  // Increased vertical spacing
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create larger label and field
        JLabel messageLabel = UIUtils.createLabel(message);
        messageLabel.setFont(UIUtils.SUBTITLE_FONT);  // Bigger font for label

        // Create custom sized text field
        JTextField inputField = createWideTextField();

        // Create buttons with standard size
        JButton confirmButton = UIUtils.createButton("Confirm");
        JButton cancelButton = UIUtils.createButton("Cancel");

        // Add components with proper spacing
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(messageLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(30, 10, 30, 10);  // More spacing around the input field
        mainPanel.add(inputField, gbc);

        // Button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));  // Increased button spacing
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        // Add actions
        confirmButton.addActionListener(e -> {
            String input = inputField.getText().trim();
            if (!input.isEmpty()) {
                updateFunction.accept(input);
                dialog.dispose();
                UIUtils.showSuccess(this, title + " updated successfully!");
            } else {
                UIUtils.showError(dialog, "Input cannot be empty.");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Method to create wide text fields
    private JTextField createWideTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(500, 40));
        field.setFont(UIUtils.INPUT_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return field;
    }

    private void handleAddItem(DefaultTableModel tableModel, List<OrderItem> orderItems) {
        JPanel inputPanel = UIUtils.createPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = UIUtils.createGBC(0, 0);

        JTextField productIDField = UIUtils.createTextField();
        JTextField quantityField = UIUtils.createTextField();

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(UIUtils.createLabel("Product ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productIDField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(UIUtils.createLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);

        int option = JOptionPane.showConfirmDialog(this, inputPanel,
                "Add Order Item", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                int productID = Integer.parseInt(productIDField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (quantity <= 0) {
                    UIUtils.showError(this, "Quantity must be greater than 0");
                    return;
                }

                String productName = CustomerController.getProductName(productID);
                double price = CustomerController.getProductPrice(productID);

                if (productName == null) {
                    UIUtils.showError(this, "Product ID not found.");
                    return;
                }

                double total = price * quantity;
                tableModel.addRow(new Object[]{
                        productID, productName, quantity,
                        String.format("$%.2f", price),
                        String.format("$%.2f", total)
                });
                orderItems.add(new OrderItem(productID, quantity, price));

            } catch (NumberFormatException ex) {
                UIUtils.showError(this, "Please enter valid numeric values.");
            } catch (Exception ex) {
                UIUtils.showError(this, "Error adding item: " + ex.getMessage());
            }
        }
    }

    private void handleRemoveItem(JTable orderTable, DefaultTableModel tableModel, List<OrderItem> orderItems) {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove the selected item?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(selectedRow);
                orderItems.remove(selectedRow);
            }
        } else {
            UIUtils.showError(this, "Please select an item to remove.");
        }
    }

    private void handleFinalizeOrder(DefaultTableModel tableModel, List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            UIUtils.showError(this, "Add at least one item to finalize the order.");
            return;
        }

        try {
            String paymentStatus = "Paid";
            int shipperID = 0;

            String invoice = customerController.createNewOrder(
                    customerController.customerID,
                    shipperID,
                    paymentStatus,
                    orderItems
            );

            showInvoice(invoice);
            tableModel.setRowCount(0);
            orderItems.clear();

        } catch (Exception ex) {
            UIUtils.showError(this, "Error finalizing the order: " + ex.getMessage());
        }
    }

    private void showInvoice(String invoice) {
        JTextArea invoiceArea = new JTextArea(invoice);
        invoiceArea.setFont(UIUtils.INPUT_FONT);
        invoiceArea.setEditable(false);
        invoiceArea.setBackground(UIUtils.ACCENT_COLOR);
        invoiceArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(invoiceArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Invoice",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Update methods
    private void updateName(String newName) {
        if (customerController.updateCustomerName(newName)) {
            refreshCustomerInformation();
        } else {
            UIUtils.showError(this, "Failed to update name.");
        }
    }

    private void updateNumber(String newNumber) {
        if (customerController.updateCustomerNumber(newNumber)) {
            refreshCustomerInformation();
        } else {
            UIUtils.showError(this, "Failed to update number.");
        }
    }

    private void updateAddress(String newAddress) {
        if (customerController.updateCustomerAddress(newAddress)) {
            refreshCustomerInformation();
        } else {
            UIUtils.showError(this, "Failed to update address.");
        }
    }

    private void updateBankInfo(String newBankInfo) {
        if (customerController.updateBankInformation(newBankInfo)) {
            refreshCustomerInformation();
        } else {
            UIUtils.showError(this, "Failed to update bank information.");
        }
    }

    private void refreshCustomerInformation() {
        String customerInfo = String.format("""
                        Customer ID: %d
                        Name: %s
                        Number: %s
                        Address: %s
                        Bank Information: %s
                        """,
                customerController.customerID,
                customerController.customerName,
                customerController.customerNumber,
                customerController.customerAddress,
                customerController.bankInformation
        );
        informationArea.setText(customerInfo);
    }

    private JPanel createOrderHistoryPanel() {
        JPanel panel = UIUtils.createPanelWithPadding(20, 20, 20, 20);
        panel.setLayout(new BorderLayout());

        // Create table
        String[] columnNames = {"Order ID", "Time", "Total Amount", "Status", "Shipper ID"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable orderTable = new JTable(tableModel);
        orderTable.setFont(UIUtils.INPUT_FONT);
        orderTable.getTableHeader().setFont(UIUtils.LABEL_FONT);

        // Fetch orders for the current customer
        List<customer.src.Model.Order> customerOrders = customerController.getOrdersByCustomerID(customerID);

        // Populate table
        for (customer.src.Model.Order order : customerOrders) {
            tableModel.addRow(new Object[]{
                    order.getOrderID(),
                    order.getTime(),
                    String.format("$%.2f", order.getTotalAmount()),
                    order.getStatus(),
                    order.getShipperID()
            });
        }

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIUtils.PRIMARY_COLOR),
                "Order History"
        ));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

}
