package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controllers.CustomerScreenController;
import controllers.CustomerController;
import controllers.InitialOrderController;

public class CustomerAccountView {

    private final CustomerScreenController controller; // Declare controller as a class field

    // Constructor to set up the GUI
    public CustomerAccountView(String phone_number) {
        // Initialize the controller
        this.controller = new CustomerScreenController(phone_number);

        // Create the main frame
        JFrame frame = new JFrame("Customer Account");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); // Center the frame on the screen

        // Create a panel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 rows, 1 column, with gaps

        // Create buttons
        JButton selfOrderButton = new JButton("Self-Order");
        JButton viewOrderHistoryButton = new JButton("View Order History");
        JButton updateProfileButton = new JButton("Update Profile");
        JButton logoutButton = new JButton("Logout");

        // Add action listeners to buttons
        selfOrderButton.addActionListener(e -> controller.handleOrder(phone_number));

//        viewOrderHistoryButton.addActionListener(e -> controller.handleViewHistory(accountName));
//
//        updateProfileButton.addActionListener(e -> controller.handleUpdateProfile(accountName));

        logoutButton.addActionListener(e -> {
            controller.handleLogOut();
            JOptionPane.showMessageDialog(frame, "Logout functionality not implemented yet.");
        });

        // Add buttons to the panel
        panel.add(selfOrderButton);
        panel.add(viewOrderHistoryButton);
        panel.add(updateProfileButton);
        panel.add(logoutButton);

        // Add the panel to the frame
        frame.add(panel);

        // Set the frame to be visible
        frame.setVisible(true);
    }
}
