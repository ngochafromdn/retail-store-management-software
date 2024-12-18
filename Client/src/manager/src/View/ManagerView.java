package manager.src.View;

import manager.src.Controller.ManagerController;
import manager.src.Model.Inventory;
import utils.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ManagerView extends JFrame {
    private final ManagerController controller;

    public ManagerView(ManagerController controller) {
        this.controller = controller;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        // Set up frame properties
        UIUtils.setFrameDefaults(this, "Manager Dashboard", 1200, 800);
        setLayout(new BorderLayout());

        // Add header panel
        add(UIUtils.createHeaderPanel("Manager Dashboard"), BorderLayout.NORTH);

        // Create main content panel with padding
        JPanel contentPanel = UIUtils.createPanelWithPadding(40, 40, 40, 40);
        contentPanel.setLayout(new BorderLayout());

        // Create button panel
        JPanel buttonPanel = createMainButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createMainButtonPanel() {
        JPanel mainPanel = UIUtils.createPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 200, 100, 200));

        // Create button panel with spacing
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 20, 30));

        // Create buttons
        JButton addInventoryButton = UIUtils.createButton("Add New Inventory");
        JButton viewInventoryButton = UIUtils.createButton("View All Inventory");
        JButton viewSaleReportsButton = UIUtils.createButton("View Sale Reports");

        // Add actions
        addInventoryButton.addActionListener(e -> showAddInventoryDialog());
        viewInventoryButton.addActionListener(e -> controller.viewAllInventory());
        viewSaleReportsButton.addActionListener(e -> controller.viewSaleReports());

        // Add buttons to panel
        buttonPanel.add(addInventoryButton);
        buttonPanel.add(viewInventoryButton);
        buttonPanel.add(viewSaleReportsButton);

        mainPanel.add(buttonPanel);
        return mainPanel;
    }

    private void showAddInventoryDialog() {
        JDialog dialog = new JDialog(this, "Add New Inventory", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = UIUtils.createPanelWithPadding(40, 60, 40, 60);
        mainPanel.setLayout(new GridBagLayout());

        // Modified GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create input fields with custom size
        JTextField productIdField = createWideTextField();
        JTextField quantityField = createWideTextField();
        JTextField dateField = createWideTextField();
        JTextField supplierIdField = createWideTextField();

        // Add form fields with labels
        addFormField(mainPanel, "Product ID:", productIdField, gbc, 0);
        addFormField(mainPanel, "Quantity Received:", quantityField, gbc, 1);
        addFormField(mainPanel, "Date (YYYY-MM-DD):", dateField, gbc, 2);
        addFormField(mainPanel, "Supplier ID:", supplierIdField, gbc, 3);

        // Button panel
        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 20));

        JButton addButton = UIUtils.createButton("Add Inventory");
        JButton cancelButton = UIUtils.createButton("Cancel");

        addButton.addActionListener(e -> {
            handleAddInventory(productIdField, quantityField, dateField, supplierIdField);
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            System.out.println("Cancel clicked");
            dialog.dispose();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add button panel with more space
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Helper method to create wider text fields
    private JTextField createWideTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(400, 35));  // Increased width
        field.setFont(UIUtils.INPUT_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int gridy) {
        // Add label
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        JLabel label = UIUtils.createLabel(labelText);
        label.setPreferredSize(new Dimension(150, 35));
        panel.add(label, gbc);

        // Add field
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    public void showInventoryTable(List<Inventory> inventoryList) {
        JDialog dialog = new JDialog(this, "Inventory List", true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);

        // Create table panel
        JPanel mainPanel = UIUtils.createPanelWithPadding(20, 20, 20, 20);
        mainPanel.setLayout(new BorderLayout());

        // Setup table
        String[] columnNames = {"Inventory ID", "Product ID", "Quantity Received", "Date", "Supplier ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add data to table
        for (Inventory inventory : inventoryList) {
            model.addRow(new Object[]{
                    inventory.getInventoryID(),
                    inventory.getProductID(),
                    inventory.getQuantityReceived(),
                    inventory.getDate(),
                    inventory.getSupplierID()
            });
        }

        // Create and style table
        JTable table = new JTable(model);
        table.setFont(UIUtils.INPUT_FONT);
        table.getTableHeader().setFont(UIUtils.LABEL_FONT);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(UIUtils.SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add close button
        JButton closeButton = UIUtils.createButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public void showOrderTable(List<Map<String, Object>> orders) {
        JDialog dialog = new JDialog(this, "Sale Reports", true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = UIUtils.createPanelWithPadding(20, 20, 20, 20);
        mainPanel.setLayout(new BorderLayout());

        // Setup table
        String[] columnNames = {"Order ID", "Time", "Total Amount", "Customer ID", "Status", "Shipper ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add data to table
        for (Map<String, Object> order : orders) {
            model.addRow(new Object[]{
                    order.get("OrderID"),
                    order.get("Time"),
                    String.format("$%.2f", order.get("TotalAmount")),
                    order.get("CustomerID"),
                    order.get("Status"),
                    order.get("ShipperID")
            });
        }

        // Create and style table
        JTable table = new JTable(model);
        table.setFont(UIUtils.INPUT_FONT);
        table.getTableHeader().setFont(UIUtils.LABEL_FONT);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(UIUtils.SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add close button
        JButton closeButton = UIUtils.createButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = UIUtils.createPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void handleAddInventory(JTextField productIdField, JTextField quantityField,
                                    JTextField dateField, JTextField supplierIdField) {
        try {
            String productIdText = productIdField.getText().trim();
            String quantityText = quantityField.getText().trim();
            String dateText = dateField.getText().trim();
            String supplierIdText = supplierIdField.getText().trim();

            if (productIdText.isEmpty() || quantityText.isEmpty() ||
                    dateText.isEmpty() || supplierIdText.isEmpty()) {
                UIUtils.showError(this, "All fields are required.");
                return;
            }

            int productId = Integer.parseInt(productIdText);
            int quantity = Integer.parseInt(quantityText);
            int supplierId = Integer.parseInt(supplierIdText);

            if (controller.addNewInventory(productId, quantity, dateText, supplierId)) {
                UIUtils.showSuccess(this, "Inventory added successfully!");
            } else {
                UIUtils.showError(this, "Failed to add inventory.");
            }
        } catch (NumberFormatException ex) {
            UIUtils.showError(this, "Please enter valid numbers for Product ID, Quantity, and Supplier ID.");
        } catch (Exception ex) {
            UIUtils.showError(this, "An error occurred: " + ex.getMessage());
        }
    }
}