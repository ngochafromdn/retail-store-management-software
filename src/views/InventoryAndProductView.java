package views;

import controllers.InventoryAndProductController;
import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import entities.Account;


public class InventoryAndProductView {
    private final InventoryAndProductController controller;
    private JFrame mainFrame;

    public InventoryAndProductView(InventoryAndProductController controller) {
        this.controller = controller;
    }

    public void createAndShowGUI() {
        mainFrame = new JFrame("Inventory and Product Dashboard");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());
        setupTitlePanel(panel);
        setupButtonPanel(panel);
        setupExitButton(panel);

        mainFrame.add(panel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void setupTitlePanel(JPanel panel) {
        JLabel titleLabel = new JLabel("Inventory and Product Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
    }

    private void setupButtonPanel(JPanel panel) {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton addInventoryButton = createStyledButton("Add New Inventory");
        JButton viewInventoryButton = createStyledButton("View All Inventory");
        JButton viewProductDetailsButton = createStyledButton("View All Product Details");

        buttonPanel.add(addInventoryButton);
        buttonPanel.add(viewInventoryButton);
        buttonPanel.add(viewProductDetailsButton);

        addInventoryButton.addActionListener(e -> addNewInventory());
        viewInventoryButton.addActionListener(e -> viewAllInventory());
        viewProductDetailsButton.addActionListener(e -> viewAllProductDetails());

        panel.add(buttonPanel, BorderLayout.CENTER);
    }

    private void setupExitButton(JPanel panel) {
        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> controller.handleExitButton(controller.getType()));

        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.add(exitButton);
        panel.add(exitPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(173, 216, 230));
        button.setFocusPainted(false);
        return button;
    }

    private void addNewInventory() {
        JDialog dialog = new JDialog(mainFrame, "Add New Inventory", true);
        dialog.setSize(400, 300);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Product ID
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Product ID:"), gbc);
        JTextField productIdField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(productIdField, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Quantity Received:"), gbc);
        JTextField quantityField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(quantityField, gbc);

        // Supplier
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Select Supplier:"), gbc);
        JComboBox<String> supplierComboBox = new JComboBox<>(controller.getAllSuppliers());
        gbc.gridx = 1;
        mainPanel.add(supplierComboBox, gbc);

        // Add Button
        JButton addButton = createStyledButton("Add Inventory");
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        mainPanel.add(addButton, gbc);

        addButton.addActionListener(e ->
                handleAddInventoryAction(productIdField, quantityField, supplierComboBox, dialog));

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private void handleAddInventoryAction(JTextField productIdField, JTextField quantityField,
                                          JComboBox<String> supplierComboBox, JDialog dialog) {
        try {
            String productIdText = productIdField.getText().trim();
            String quantityText = quantityField.getText().trim();

            if (productIdText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.");
                return;
            }

            int productId = Integer.parseInt(productIdText);
            int quantity = Integer.parseInt(quantityText);
            String selectedSupplier = (String) supplierComboBox.getSelectedItem();

            if (selectedSupplier == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a supplier.");
                return;
            }

            int supplierId = controller.getSupplierIDByName(selectedSupplier);
            boolean isSuccess = controller.addInventory(productId, quantity, supplierId);

            if (isSuccess) {
                JOptionPane.showMessageDialog(dialog, "Inventory added successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Failed to add inventory. Please check product ID and quantity.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for Product ID and Quantity.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "An error occurred: " + ex.getMessage());
        }
    }

    // Method to show the inventory data in a dialog
    private void showDataDialog(String title, String[] inventoryData) {
        // Column names for the table
        String[] columnNames = {"Inventory ID", "Product ID", "Product Name", "Date", "Supplier ID", "Unit Price", "Quantity Received"};

        // Convert the inventory data into a 2D array for the table
        String[][] tableData = new String[inventoryData.length][7]; // 7 columns based on the data we want to display

        for (int i = 0; i < inventoryData.length; i++) {
            String[] details = inventoryData[i].split(", "); // Split the string by comma and space
            for (String detail : details) {
                // Extract the relevant information from the detail string
                if (detail.startsWith("InventoryID:")) {
                    tableData[i][0] = detail.split(": ")[1]; // Inventory ID
                } else if (detail.startsWith("ProductID:")) {
                    tableData[i][1] = detail.split(": ")[1]; // Product ID
                } else if (detail.startsWith("Product:")) {
                    tableData[i][2] = detail.split(": ")[1]; // Product Name
                } else if (detail.startsWith("Date:")) {
                    tableData[i][3] = detail.split(": ")[1]; // Date
                } else if (detail.startsWith("SupplierID:")) {
                    tableData[i][4] = detail.split(": ")[1]; // Supplier ID
                } else if (detail.startsWith("UnitPrice:")) {
                    tableData[i][5] = detail.split(": ")[1]; // Unit Price
                } else if (detail.startsWith("QuantityReceived:")) {
                    tableData[i][6] = detail.split(": ")[1]; // Quantity Received
                }
            }
        }

        // Create a table model with the data
        DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
        JTable table = new JTable(model);

        // Create a JScrollPane to make the table scrollable
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Create a dialog to display the table
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setSize(800, 400); // Set the size of the dialog
        dialog.setLocationRelativeTo(null); // Center the dialog on the screen
        dialog.setVisible(true); // Show the dialog
    }

    private void showProductDialog(String title, String[] productData) {
        // Column names for the table
        String[] columnNames = {"ID", "Name", "Quantity", "Unit Price"};

        // Convert the product data into a 2D array for the table
        String[][] tableData = new String[productData.length][4];

        for (int i = 0; i < productData.length; i++) {
            String[] details = productData[i].split(", "); // Split the string by comma and space
            for (String detail : details) {
                // Extract the relevant information from the detail string
                if (detail.startsWith("ID:")) {
                    tableData[i][0] = detail.split(": ")[1]; // Product ID
                } else if (detail.startsWith("Name:")) {
                    tableData[i][1] = detail.split(": ")[1]; // Name
                } else if (detail.startsWith("Quantity:")) {
                    tableData[i][2] = detail.split(": ")[1]; // Quantity
                } else if (detail.startsWith("Unit Price:")) {
                    tableData[i][3] = detail.split(": ")[1]; // Unit Price
                }
            }
        }

        // Create a table model with the data
        DefaultTableModel model = new DefaultTableModel(tableData, columnNames);
        JTable table = new JTable(model);

        // Create a JScrollPane to make the table scrollable
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        // Create a dialog to display the table
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void viewAllProductDetails() {
        List<String> productDetails = controller.getAllProductDetails();
        String[] detailsArray = productDetails.toArray(new String[0]);
        showProductDialog("All Product Details", detailsArray);
    }


    // Method to view all inventory
    public void viewAllInventory() {
        String[] inventoryData = controller.viewAllInventory(); // Get inventory data from the controller
        showDataDialog("All Inventory", inventoryData); // Show the data in a dialog
    }






    public void dispose() {
        if (mainFrame != null) {
            mainFrame.dispose();
        }
    }
}