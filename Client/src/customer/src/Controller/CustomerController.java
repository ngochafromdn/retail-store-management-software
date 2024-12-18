package customer.src.Controller;

import customer.src.DataAdapter.CustomerAdapter;
import customer.src.DataAdapter.OrderDataAdapter;
import customer.src.DataAdapter.ProductDataAdapter;
import customer.src.Model.Order;
import customer.src.Model.OrderItem;
import customer.src.Model.Order_;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerController {

    public final int accountID;
    private final CustomerAdapter customerAdapter;
    private final OrderDataAdapter orderDataAdapter;
    private final ProductDataAdapter productDataAdapter;
    public int customerID;
    public String customerName;
    public String customerNumber;
    public String customerAddress;
    public String bankInformation;

    public CustomerController(int accountID) {
        this.customerAdapter = new CustomerAdapter();
        this.orderDataAdapter = new OrderDataAdapter();
        this.productDataAdapter = new ProductDataAdapter();

        this.accountID = accountID;

        // Fetch the customer information when the controller is initialized
        initializeCustomerInformation();
    }

    public static List<OrderItem> addItem(List<OrderItem> itemList, OrderItem orderItem) {
        itemList.add(orderItem);

        return itemList;
    }

    public static String getProductName(int productID) {
        return ProductDataAdapter.getProductName(productID);
    }

    public static double getProductPrice(int productID) {
        return ProductDataAdapter.getProductPrice(productID);
    }

    public static void main(String[] args) {
        System.out.println("===== Testing createNewOrder Method =====");

        // Test Data Setup
        int customerID = 1;
        int shipperID = 2;
        String paymentStatus = "Paid";

        // Create sample order items
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(1, 2, 50.0)); // Product ID: 101, Quantity: 2, Price: $50.0
        orderItems.add(new OrderItem(1, 1, 30.0)); // Product ID: 102, Quantity: 1, Price: $30.0

        // Simulate the product data adapter response
        System.out.println("\n--- Step 1: Simulating Product Data Before Order ---");
        System.out.println("Product 101 Quantity: 10");
        System.out.println("Product 102 Quantity: 5");

        // Mock: Update product quantities
        System.out.println("\n--- Step 2: Updating Product Quantities ---");
        ProductDataAdapter.updateProductQuantity(1, 8); // 10 - 2 = 8
        ProductDataAdapter.updateProductQuantity(2, 4); // 5 - 1 = 4
        System.out.println("Product 101 Updated Quantity: 8");
        System.out.println("Product 102 Updated Quantity: 4");

        // Call the createNewOrder method
        System.out.println("\n--- Step 3: Creating New Order ---");
        CustomerController test = new CustomerController(3);
        String invoice = test.createNewOrder(customerID, shipperID, paymentStatus, orderItems);

        // Display the generated invoice
        System.out.println("\n--- Step 4: Generated Invoice ---");
        System.out.println(invoice);

        System.out.println("\n===== Test Completed =====");
    }

    public void initializeCustomerInformation() {
        List<Map<String, Object>> customerData = customerAdapter.getCustomerInformationByAccountID(accountID);
        if (customerData != null && !customerData.isEmpty()) {
            Map<String, Object> customerInfo = customerData.get(0);
            this.customerID = ((Double) customerInfo.get("CustomerID")).intValue();
            this.customerName = (String) customerInfo.get("Name");
            this.customerNumber = (String) customerInfo.get("Number");
            this.customerAddress = (String) customerInfo.get("Address");
            this.bankInformation = (String) customerInfo.get("BankInformation");
        } else {
            throw new RuntimeException("No customer information found for account ID: " + accountID);
        }
    }

    public boolean updateCustomerName(String newCustomerName) {
        this.customerName = newCustomerName;
        return CustomerAdapter.updateCustomerInformation(
                customerID, customerName, customerNumber, customerAddress, accountID, bankInformation
        );
    }

    public boolean updateBankInformation(String newbankInformation) {
        this.customerName = newbankInformation;
        return CustomerAdapter.updateCustomerInformation(
                customerID, customerName, customerNumber, customerAddress, accountID, newbankInformation
        );
    }

    public boolean updateCustomerNumber(String newNumber) {
        this.customerNumber = newNumber;
        return CustomerAdapter.updateCustomerInformation(
                customerID, customerName, customerNumber, customerAddress, accountID, bankInformation
        );
    }

    public boolean updateCustomerAddress(String newAddress) {
        this.customerAddress = newAddress;
        return CustomerAdapter.updateCustomerInformation(
                customerID, customerName, customerNumber, newAddress, accountID, bankInformation
        );
    }

    public OrderItem newOrderItem(int productID, int quantity) {
        return OrderDataAdapter.createOrderItem(productID, quantity);
    }


    public List<Order> getOrdersByCustomerID(int customerID) {
        return orderDataAdapter.getOrdersByCustomerID(customerID);
    }

    public String createNewOrder(int customerID, int shipperID, String paymentStatus, List<OrderItem> orderItems) {
        // Current time
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Calculate total amount and update product
        double totalAmount = 0;
        for (OrderItem item : orderItems) {
            totalAmount += item.getPrice() * item.getQuantity();
            int productID = item.getProductID();
            Double old_quantity = (Double) ProductDataAdapter.getProductByID(productID).get("quantity");
            int new_quantity = old_quantity.intValue() - item.getQuantity();
            ProductDataAdapter.updateProductQuantity(productID, new_quantity);


        }

        // Create a new Order object
        Order_ newOrder = new Order_();
        newOrder.setTime(time);
        newOrder.setTotalAmount(totalAmount);
        newOrder.setCustomerID(customerID);
        newOrder.setStatus(paymentStatus);
        newOrder.setShipperID(shipperID);
        newOrder.setOrderItems(orderItems);

        // Call the data adapter to save the order and retrieve the order ID
        int orderID = OrderDataAdapter.createNewOrder(newOrder);
        System.out.println(newOrder);

        // Generate the invoice string
        StringBuilder invoice = new StringBuilder();
        invoice.append(String.format("Invoice for Order ID: %d\n", orderID));
        invoice.append(String.format("Customer ID: %d\n", customerID));
        invoice.append(String.format("Shipper ID: %d\n", shipperID));
        invoice.append(String.format("Order Time: %s\n", time));
        invoice.append(String.format("Payment Status: %s\n", paymentStatus));
        invoice.append(String.format("Total Amount: $%.2f\n", totalAmount));
        invoice.append("\n Order Items:\n");

        for (OrderItem item : orderItems) {
            invoice.append(String.format("\n - %s (x%d) at $%.2f each \n" + ProductDataAdapter.getProductName(item.getProductID()), item.getProductID(), item.getQuantity(), item.getPrice()));
        }

        return invoice.toString();
    }

    public void updateDebtPayment(int orderID) {
        OrderDataAdapter.updatePaymentMethod(orderID, "Paid");
    }

    public List<Order> getAllOrders() {
        return OrderDataAdapter.getAllOrders();
    }

    public boolean updateOrderStatus(int orderID, String status) {
        try {
            OrderDataAdapter.updatePaymentMethod(orderID, status);
            System.out.printf("Order ID %d status updated to: %s\n", orderID, status);
            return true;
        } catch (Exception e) {
            System.err.printf("Failed to update status for Order ID %d: %s\n", orderID, e.getMessage());
            return false;
        }
    }

}
