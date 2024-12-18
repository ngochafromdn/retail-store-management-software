package Server;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLToMongoTransfer {
    private static final String SQLITE_URL = "jdbc:sqlite:retail-store-1.db";
    private static final String MONGO_URI = "mongodb+srv://nguyenhoangngocha:lJSUQlBOYMCLXZ1c@cluster1.e8nvi.mongodb.net/?retryWrites=true&w=majority&appName=Cluster1";
    private static final String MONGO_DB_NAME = "Project2";

    public static void main(String[] args) {
        // SQLite Connection
        SQLConnection sqlConnection = new SQLConnection();
        Connection sqliteConnection = null;
        try {
            sqliteConnection = sqlConnection.getConnection();

            // MongoDB Connection
            MongoDBConnection mongoDBConnection = new MongoDBConnection();
            MongoDatabase mongoDatabase = mongoDBConnection.getDatabase();

            // MongoDB Collections
            MongoCollection<Document> ordersCollection = mongoDatabase.getCollection("orders");
            MongoCollection<Document> orderItemsCollection = mongoDatabase.getCollection("order_items");

            // Retrieve data from SQLite Order table
            String orderQuery = "SELECT * FROM `Order`";  // Query for Order table
            Statement stmt = sqliteConnection.createStatement();
            ResultSet ordersResultSet = stmt.executeQuery(orderQuery);

            List<Document> ordersList = new ArrayList<>();
            List<Document> orderItemsList = new ArrayList<>();

            while (ordersResultSet.next()) {
                int orderId = ordersResultSet.getInt("id");
                int customerId = ordersResultSet.getInt("customer_id");
                String status = ordersResultSet.getString("status");
                double totalAmount = ordersResultSet.getDouble("total_amount");

                // Create a MongoDB Document for Order
                Document orderDocument = new Document("OrderID", orderId)
                        .append("CustomerID", customerId)
                        .append("Status", status)
                        .append("TotalAmount", totalAmount);

                ordersList.add(orderDocument);

                // Retrieve data from SQLite OrderItems table
                String orderItemsQuery = "SELECT * FROM OrderItems WHERE order_id = " + orderId;
                Statement stmt2 = sqliteConnection.createStatement();
                ResultSet orderItemsResultSet = stmt2.executeQuery(orderItemsQuery);

                while (orderItemsResultSet.next()) {
                    int quantity = orderItemsResultSet.getInt("quantity");
                    double price = orderItemsResultSet.getDouble("price");
                    int productId = orderItemsResultSet.getInt("product_id");

                    // Create a MongoDB Document for OrderItem
                    Document orderItemDocument = new Document("OrderID", orderId)
                            .append("Quantity", quantity)
                            .append("Price", price)
                            .append("ProductID", productId);

                    orderItemsList.add(orderItemDocument);
                }
                stmt2.close();
            }

            // Insert data into MongoDB
            if (!ordersList.isEmpty()) {
                ordersCollection.insertMany(ordersList);
            }
            if (!orderItemsList.isEmpty()) {
                orderItemsCollection.insertMany(orderItemsList);
            }

            System.out.println("Data transfer from SQLite to MongoDB completed successfully.");

            // Close connections
            sqlConnection.closeConnection();
            mongoDBConnection.close();
        } catch (SQLException e) {
            System.err.println("Error during SQL operations: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

