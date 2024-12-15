package dataaccess;

import entities.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.sql.*;
import java.text.SimpleDateFormat;

public class DataAccess {
    private static final String URL = "jdbc:sqlite:retail-store-1.db";
    private Connection connection; // Class-level connection variable

    public DataAccess() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(String customerName, String customerNumber, String address) {
        // SQL query to insert a new account
        String accountQuery = "INSERT INTO Account(AccountName, Password, AccountType) VALUES(?, ?, ?)";
        // SQL query to insert a new customer
        String customerQuery = "INSERT INTO Customer(Name, Number, Address, AccountID) VALUES(?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL)) {
            // Start a transaction
            connection.setAutoCommit(false);

            // Create a prepared statement for the account insertion
            try (PreparedStatement accountStatement = connection.prepareStatement(accountQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                // Set parameters for the account
                accountStatement.setString(1, customerName); // Assuming AccountName is the same as customerName
                accountStatement.setString(2, "123456"); // Default password
                accountStatement.setString(3, "Customer"); // Assuming the account type is "Customer"

                // Execute the account insertion
                accountStatement.executeUpdate();

                // Retrieve the generated AccountID
                try (var generatedKeys = accountStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int accountId = generatedKeys.getInt(1); // Get the generated AccountID

                        // Now insert the customer with the generated AccountID
                        try (PreparedStatement customerStatement = connection.prepareStatement(customerQuery)) {
                            customerStatement.setString(1, customerName);
                            customerStatement.setString(2, customerNumber);
                            customerStatement.setString(3, address);
                            customerStatement.setInt(4, accountId); // Set the AccountID

                            // Execute the customer insertion
                            customerStatement.executeUpdate();
                        }
                    }
                }
            }

            // Commit the transaction
            connection.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            // Rollback in case of an error
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }}

    public boolean customerExists(String customerName) {
        String query = "SELECT COUNT(*) FROM Customer WHERE Name = ?";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, customerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Customer findCustomerByPhoneNumber(String phoneNumber) {
        String query = "SELECT * FROM Customer WHERE Number = ?";
        Customer customer = null;

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, phoneNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                customer = new Customer(resultSet.getString("Name"), resultSet.getString("Number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }

    public int createNewOrder(String phoneNumber) {
        Customer customer = this.findCustomerByPhoneNumber(phoneNumber);
        if (customer == null) {
            return -1;
        }

        int customerId = customer.getCustomerId();
        String query = "INSERT INTO [Order](Time, TotalAmount, CustomerID, Status) VALUES(?, ?, ?, 'INCOMPLETE')";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = currentTime.format(formatter);

            preparedStatement.setString(1, formattedTime);
            preparedStatement.setDouble(2, 0.0);
            preparedStatement.setInt(3, customerId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean changeDebtToPaid(int orderId) {
        String query = "UPDATE [Order] SET Status = 'PAID' WHERE OrderID = ? AND Status = 'INCOMPLETE'";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, orderId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean productExists(int productId) {
        String query = "SELECT 1 FROM Product WHERE ProductID = ?";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException e) {
            return false;
        }
    }

    public List<String> getAllProducts() {
        List<String> productList = new ArrayList<>();
        String query = "SELECT ProductID, Name, Quantity, UnitPrice FROM Product";

        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int productId = resultSet.getInt("ProductID");
                String productInfo = "ID: " + productId + ", Name: " + resultSet.getString("Name") +
                        ", Quantity: " + resultSet.getInt("Quantity") + ", Unit Price: "
                        + resultSet.getDouble("UnitPrice");
                productList.add(productInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    public void addProduct(String productName, double unitPrice, int quantity) {
        String query = "INSERT INTO Product(Name, UnitPrice, Quantity) VALUES(?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, productName);
            preparedStatement.setDouble(2, unitPrice);
            preparedStatement.setInt(3, quantity);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getUnitPrice(int productId) {
        String query = "SELECT UnitPrice FROM Product WHERE ProductID = ?";
        double unitPrice = 0.0;

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    unitPrice = rs.getDouble("UnitPrice");
                } else {
                    System.out.println("Product with ID " + productId + " not found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return unitPrice;
    }

    public Product getProductById(int productId) {
        String query = "SELECT * FROM Product WHERE ProductID = ?";
        Product product = null;

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("Name");
                    double unitPrice = rs.getDouble("UnitPrice");
                    int quantity = rs.getInt("Quantity");
                    product = new Product(productId, name, unitPrice, quantity);
                } else {
                    System.out.println("Product with ID " + productId + " not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    public void addOrderItem(int productId, int quantity, int orderId) {
        String insertOrderItemQuery = "INSERT INTO OrderItem(OrderItemID, OrderID, ProductID, Quantity, Price) VALUES(?, ?, ?, ?, ?)";
        String selectProductQuery = "SELECT UnitPrice, Quantity FROM Product WHERE ProductID = ?";
        String updateOrderQuery = "UPDATE [Order] SET TotalAmount = TotalAmount + ? WHERE OrderID = ?";
        String updateProductQuery = "UPDATE Product SET Quantity = Quantity - ? WHERE ProductID = ?";

        try (Connection conn = DriverManager.getConnection(URL)) {
            // Check product availability
            try (PreparedStatement stmt = conn.prepareStatement(selectProductQuery)) {
                stmt.setInt(1, productId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        double unitPrice = rs.getDouble("UnitPrice");
                        int stockQuantity = rs.getInt("Quantity");

                        if (quantity <= stockQuantity) {
                            double totalPrice = unitPrice * quantity;

                            // Add to OrderItem
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertOrderItemQuery)) {
                                insertStmt.setInt(1, this.getNextOrderItemID(conn)); // Assuming this method exists
                                insertStmt.setInt(2, orderId);
                                insertStmt.setInt(3, productId);
                                insertStmt.setInt(4, quantity);
                                insertStmt.setDouble(5, totalPrice);
                                insertStmt.executeUpdate();
                            }

                            // Update order total amount
                            try (PreparedStatement updateOrderStmt = conn.prepareStatement(updateOrderQuery)) {
                                updateOrderStmt.setDouble(1, totalPrice);
                                updateOrderStmt.setInt(2, orderId);
                                updateOrderStmt.executeUpdate();
                            }

                            // Update product quantity
                            try (PreparedStatement updateProductStmt = conn.prepareStatement(updateProductQuery)) {
                                updateProductStmt.setInt(1, quantity);
                                updateProductStmt.setInt(2, productId);
                                updateProductStmt.executeUpdate();
                            }

                        } else {
                            System.out.println("Not enough product quantity in stock.");
                        }
                    } else {
                        System.out.println("Product not found with ID: " + productId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getNextOrderItemID(Connection conn) throws SQLException {
        String query = "SELECT MAX(OrderItemID) + 1 AS nextID FROM OrderItem";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("nextID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Return 1 in case of any exception or when no result is found
        }

        // Return 1 if no results or an error occurs
        return 1;
    }

    public void updateOrderStatus(int orderId, String status) {
        String query = "UPDATE [Order] SET Status = ? WHERE OrderID = ?";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Order status updated successfully.");
            } else {
                System.out.println("No order found with OrderID: " + orderId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Order getOrderById(int orderId) {
        String query = "SELECT * FROM [Order] WHERE OrderID = ?";
        Order order = null;

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int orderID = rs.getInt("OrderID");
                    double totalAmount = rs.getDouble("TotalAmount");
                    Timestamp timestamp = rs.getTimestamp("Time");
                    LocalDateTime time = timestamp.toLocalDateTime();
                    order = new Order(orderID, time, totalAmount);
                } else {
                    System.out.println("Order not found for ID: " + orderId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    // Get Supplier ID by Name
    public int getSupplierIDByName(String supplierName) {
        String query = "SELECT SupplierID FROM Supplier WHERE Name = ?";
        int supplierID = -1;

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, supplierName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    supplierID = rs.getInt("SupplierID");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching supplier ID: " + e.getMessage());
        }

        return supplierID;
    }

    // Add a new supplier
    public boolean addSupplier(String supplierName) {
        String query = "INSERT INTO Supplier (Name) VALUES (?)";
        boolean isAdded = false;

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, supplierName);
            int rowsAffected = stmt.executeUpdate();
            isAdded = rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAdded;
    }

    public boolean addInventory(int productId, int supplierId, int quantityReceived, Date date) {
        String insertQuery = "INSERT INTO Inventory (ProductID, SupplierID, QuantityReceived, Date) VALUES (?, ?, ?, ?)";
        String updateProductQuantityQuery = "UPDATE Product SET Quantity = Quantity + ? WHERE ProductID = ?";
        boolean isAdded = false;
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false); // Begin transaction

            // Insert into Inventory table
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, productId);
                insertStmt.setInt(2, supplierId);
                insertStmt.setInt(3, quantityReceived);
                insertStmt.setDate(4, new java.sql.Date(date.getTime()));
                int rowsAffected = insertStmt.executeUpdate();
                isAdded = rowsAffected > 0;
            }

            // Update Product quantity if inventory was successfully added
            if (isAdded) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateProductQuantityQuery)) {
                    updateStmt.setInt(1, quantityReceived);
                    updateStmt.setInt(2, productId);
                    updateStmt.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction if both statements succeed

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction in case of error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Close connection
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }

        return isAdded;
    }

    public List<OrderItem> getAllOrderItemByID(int OrderID) {
        List<OrderItem> orderItems = new ArrayList<>();
        String query = "SELECT * FROM OrderItem WHERE OrderID = ?";

        // Assuming you have a method getConnection() to get a connection to the database
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameter in the prepared statement
            stmt.setInt(1, OrderID);

            try (ResultSet rs = stmt.executeQuery()) {
                // Process the result set
                while (rs.next()) {
                    // Assuming OrderItem has a constructor that accepts these values
                    OrderItem item = new OrderItem(
                            rs.getInt("OrderItemID"), // or whatever the column name is
                            rs.getInt("ProductID"), // or whatever the column name is
                            rs.getInt("Quantity"),
                            rs.getDouble("Price")
                    );
                    orderItems.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderItems;
    }

    public Account getAccountByAccountName(String accountName) {
        System.out.println("Find account by account name");

        String query = "SELECT * FROM Account WHERE AccountName = ?";
        Account account = null;

        // Use try-with-resources to handle resource closing automatically
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, accountName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Map the result set to an Account object
                    account = new Account(rs.getInt("AccountID"), rs.getString("AccountName"),
                            rs.getString("Password"), rs.getString("AccountType"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return account;
    }

    // Get a list of all suppliers
    public List<String> getAllSuppliers() {
        List<String> suppliers = new ArrayList<>();
        String query = "SELECT Name FROM Supplier";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String supplierName = rs.getString("Name");
                suppliers.add(supplierName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    public String getCustomerPhoneByAccount(int accountId) {
        String phoneNumber = null;
        String query = "SELECT Number FROM Customer WHERE AccountID = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, accountId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    phoneNumber = resultSet.getString("Number");
                }
            }
        } catch (SQLException e) {
            // Log the error instead of just printing the stack trace
            System.err.println("Error while fetching customer phone number: " + e.getMessage());
            e.printStackTrace();
        }

        return phoneNumber;
    }




    public String[] getAllInventory() {
        List<String> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM Inventory JOIN Product ON Inventory.ProductID = Product.ProductID";

        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int inventoryId = rs.getInt("InventoryID");
                int productId = rs.getInt("ProductID");
                int quantityReceived = rs.getInt("QuantityReceived");
                long timestamp = rs.getLong("Date"); // Giả sử ngày tháng lưu dưới dạng timestamp (số mili giây)
                String productName = rs.getString("Name");
                double unitPrice = rs.getDouble("UnitPrice");
                int quantity = rs.getInt("Quantity");
                int supplierId = rs.getInt("SupplierID");

                // Chuyển đổi timestamp thành ngày tháng
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Định dạng ngày tháng bạn muốn
                Date date = new Date(timestamp); // Chuyển timestamp thành đối tượng Date
                String formattedDate = dateFormat.format(date); // Chuyển đổi thành chuỗi ngày tháng

                // Tạo chuỗi thông tin kho với ngày tháng đã định dạng
                String inventoryDetails = String.format(
                        "InventoryID: %d, ProductID: %d, Product: %s, Date: %s, SupplierID: %d, UnitPrice: %.2f, QuantityReceived: %d",
                        inventoryId, productId, productName, formattedDate, supplierId, unitPrice,
                        quantityReceived);

                inventoryList.add(inventoryDetails);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return inventoryList.toArray(new String[0]);
    }
}
