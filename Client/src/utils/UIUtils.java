package utils;

import login.LoginView;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UIUtils {
    // Color constants
    public static final Color PRIMARY_COLOR = new Color(60, 90, 150);    // Main blue
    public static final Color PRIMARY_DARK = new Color(45, 68, 113);     // Darker blue
    public static final Color SECONDARY_COLOR = new Color(240, 240, 240); // Light gray
    public static final Color TEXT_COLOR = new Color(51, 51, 51);        // Dark gray
    public static final Color ACCENT_COLOR = new Color(255, 255, 255);   // White
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 250);// Light blue-gray
    public static final Color ERROR_COLOR = new Color(220, 53, 69);      // Red
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);    // Green

    // Font constants
    private static final String PRIMARY_FONT = "Arial";
    public static final Font HEADER_FONT = new Font(PRIMARY_FONT, Font.BOLD, 28);
    public static final Font TITLE_FONT = new Font(PRIMARY_FONT, Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font(PRIMARY_FONT, Font.BOLD, 18);
    public static final Font BUTTON_FONT = new Font(PRIMARY_FONT, Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font(PRIMARY_FONT, Font.BOLD, 14);
    public static final Font INPUT_FONT = new Font(PRIMARY_FONT, Font.PLAIN, 14);

    // Button creation
    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(ACCENT_COLOR);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    // Label creation
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    // Header label creation
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    // Title label creation
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    // Text field creation
    public static JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(250, 35));
        field.setFont(INPUT_FONT);
        field.setBorder(createTextBorder());
        return field;
    }

    // Password field creation
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(250, 35));
        field.setFont(INPUT_FONT);
        field.setBorder(createTextBorder());
        return field;
    }

    // Combo box creation
    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(INPUT_FONT);
        comboBox.setPreferredSize(new Dimension(250, 35));
        return comboBox;
    }

    // Panel creation
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ACCENT_COLOR);
        return panel;
    }

    // Create standard border for text components
    private static CompoundBorder createTextBorder() {
        return new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(5, 10, 5, 10)
        );
    }

    // Message dialogs
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // Create panel with padding
    public static JPanel createPanelWithPadding(int top, int left, int bottom, int right) {
        JPanel panel = createPanel();
        panel.setBorder(new EmptyBorder(top, left, bottom, right));
        return panel;
    }

    // Create header panel
    public static JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Title label on the left
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(ACCENT_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Back button on the right
        JButton backButton = new JButton("⇠ Back");
        backButton.setFont(BUTTON_FONT);
        backButton.setForeground(ACCENT_COLOR);
        backButton.setBackground(PRIMARY_COLOR);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect for the back button
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(PRIMARY_DARK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(PRIMARY_COLOR);
            }
        });

        backButton.addActionListener(e -> {
            // Open the login.LoginView after closing the current window
            SwingUtilities.invokeLater(LoginView::new);
            // Close the current window (dispose of the frame)
            Window window = SwingUtilities.getWindowAncestor(backButton);
            if (window != null) {
                window.dispose(); // Close the window
            }
        });

        headerPanel.add(backButton, BorderLayout.EAST);
        return headerPanel;
    }

    // Create tabbed pane
    public static JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(BUTTON_FONT);
        tabbedPane.setBackground(ACCENT_COLOR);
        tabbedPane.setForeground(TEXT_COLOR);
        return tabbedPane;
    }

    // Set up basic frame properties
    public static void setFrameDefaults(JFrame frame, String title, int width, int height) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    // GridBagConstraints utility method
    public static GridBagConstraints createGBC(int gridx, int gridy) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }
}