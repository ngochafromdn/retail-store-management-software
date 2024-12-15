package views;

import controllers.EmployeeController;
import javax.swing.*;
import java.awt.*;

public class EmployeeView {
    private final EmployeeController controller;
    private JFrame frame;

    public EmployeeView(EmployeeController controller) {
        this.controller = controller;
    }

    public void createAndShowGUI() {
        // Frame setup
        frame = new JFrame("Employee Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Main panel with centered layout and background color
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 250));

        // Welcome label styling
        JLabel welcomeLabel = new JLabel("Welcome, Employee!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(70, 130, 180));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Center panel for buttons with padding and styling
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
        centerPanel.setOpaque(false);

        // Create buttons
        JButton viewInventoryButton = createStyledButton("Process Inventory and Product");
        JButton processOrderButton = createStyledButton("Process Order");
        JButton logoutButton = createStyledButton("Logout");

        // Add buttons to center panel
        centerPanel.add(viewInventoryButton);
        centerPanel.add(processOrderButton);
        centerPanel.add(logoutButton);

        // Add center panel to main panel
        panel.add(centerPanel, BorderLayout.CENTER);
        frame.add(panel);

        // Action listeners for buttons
        viewInventoryButton.addActionListener(e -> controller.handleViewInventory());

        processOrderButton.addActionListener(e -> controller.handleProcessOrder());

        logoutButton.addActionListener(e -> {
            int confirmLogout = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to log out?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirmLogout == JOptionPane.YES_OPTION) {
                controller.handleLogout();
            }
        });

        // Set the frame visibility
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(new Color(173, 216, 230));
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return button;
    }

    public void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }
}