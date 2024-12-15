//package dataaccess;
//import java.time.ZoneId;
//
//
//import com.mongodb.MongoClientSettings;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import org.bson.Document;
//import entities.Orders;
//import entities.OrderItem;
//import java.util.Date;
//
//
//import java.util.ArrayList;
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class MongoDBAccess {
//
//    private MongoClient mongoClient;
//    private MongoDatabase database;
//    private MongoCollection<Document> ordersCollection;
//
//    // Connection string and database name
//    private static final String CONNECTION_STRING = "mongodb+srv://nguyenhoangngocha:lJSUQlBOYMCLXZ1c@cluster1.e8nvi.mongodb.net/?retryWrites=true&w=majority&appName=Cluster1";
//    private static final String DATABASE_NAME = "Project2";
//
//    // Constructor to initialize MongoDB connection
//    public MongoDBAccess() {
//        this.mongoClient = MongoClients.create(CONNECTION_STRING);
//        this.database = mongoClient.getDatabase(DATABASE_NAME);
//        this.ordersCollection = database.getCollection("Orders"); // Ensure the collection name matches your MongoDB setup
//    }
//
//    // Method to query all orders
//    public List<Orders> getAllOrders() {
//        List<Orders> orders = new ArrayList<>();
//
//        // Retrieve all documents from the orders collection
//        for (Document doc : ordersCollection.find()) {
//            int orderId = doc.getInteger("OrderID"); // Correct method
//            String customerPhone = doc.getString("CustomerPhone"); // Use String for customerPhone
//            double totalAmount = doc.getDouble("TotalAmount");
//            String status = doc.getString("Status");
//            Date date = doc.getDate("Time"); // Assuming this retrieves a java.util.Date
//            LocalDateTime time = null;
//
//            if (date != null) {
//                time = date.toInstant()
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDateTime();
//            }
//
//            // Retrieve order items
//            List<OrderItem> orderItems = new ArrayList<>();
//            List<Document> items = (List<Document>) doc.get("OrderItems");
//            for (Document itemDoc : items) {
//                int orderItemId = itemDoc.getInteger("OrderItemID");
//                int productId = itemDoc.getInteger("ProductID");
//                int quantity = itemDoc.getInteger("Quantity");
//                double price = itemDoc.getDouble("Price");
//
//                OrderItem orderItem = new OrderItem(orderItemId, productId, quantity, price);
//                orderItems.add(orderItem);
//            }
//
//            // Create an Order object and add it to the list
//            Orders order = new Orders(orderId, time, totalAmount, customerPhone, status, orderItems);
//            orders.add(order);
//        }
//
//        return orders;
//    }
//
//
//    // Method to query all orders by CustomerPhone (phone_number)
//    public List<Orders> getAllOrdersByPhoneNumber(String phone_number) {
//        List<Orders> orders = new ArrayList<>();
//
//        // Retrieve all documents from the orders collection where CustomerPhone matches the given phone_number
//        for (Document doc : ordersCollection.find(Filters.eq("CustomerPhone", phone_number))) {
//            int orderId = doc.getInteger("OrderID"); // Correct method
//            String customerPhone = doc.getString("CustomerPhone"); // Use String for customerPhone
//            double totalAmount = doc.getDouble("TotalAmount");
//            String status = doc.getString("Status");
//            Date date = doc.getDate("Time"); // Assuming this retrieves a java.util.Date
//            LocalDateTime time = null;
//
//            if (date != null) {
//                time = date.toInstant()
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDateTime();
//            }
//
//            // Retrieve order items
//            List<OrderItem> orderItems = new ArrayList<>();
//            List<Document> items = (List<Document>) doc.get("OrderItems");
//            for (Document itemDoc : items) {
//                int orderItemId = itemDoc.getInteger("OrderItemID");
//                int productId = itemDoc.getInteger("ProductID");
//                int quantity = itemDoc.getInteger("Quantity");
//                double price = itemDoc.getDouble("Price");
//
//                OrderItem orderItem = new OrderItem(orderItemId, productId, quantity, price);
//                orderItems.add(orderItem);
//            }
//
//            // Create an Order object and add it to the list
//            Orders order = new Orders(orderId, time, totalAmount, customerPhone, status, orderItems);
//            orders.add(order);
//        }
//
//        return orders;
//    }
//
//
//    // Method to close the MongoDB connection
//    public void close() {
//        if (mongoClient != null) {
//            mongoClient.close();
//        }
//    }
//
//    // Main method for testing
//    public static void main(String[] args) {
//        MongoDBAccess mongoDBAccess = new MongoDBAccess();
//
//        // Example: Insert a sample order (optional)
////        List<OrderItem> orderItems = new ArrayList<>();
////        orderItems.add(new OrderItem("item1", "product1", 2, 50.0));
////        orderItems.add(new OrderItem("item2", "product2", 1, 25.0));
////        mongoDBAccess.insertOrder("1", "C001", 125.0, "Completed", orderItems);
//
//    }
//}