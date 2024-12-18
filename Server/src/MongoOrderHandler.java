package Server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoOrderHandler implements HttpHandler {

    private final MongoDBConnection mongoDBConnection;

    // Constructor
    public MongoOrderHandler() {
        mongoDBConnection = new MongoDBConnection();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String jsonResponse;
        int statusCode = 200; // Default to OK

        if (path.equals("/orders/shipperID/0")) {
            jsonResponse = findOrdersWithShipperIDZero();
        } else if (path.equals("/orders/status/debt")) {
            jsonResponse = findOrdersWithStatusDebt();
        } else if (path.matches("/orders/\\d+")) { // Pattern to match /orders/{orderID}
            String orderIDStr = path.substring(path.lastIndexOf("/") + 1);
            try {
                int orderID = Integer.parseInt(orderIDStr);
                jsonResponse = findOrderByID(orderID);
            } catch (NumberFormatException e) {
                jsonResponse = "{\"message\": \"Invalid order ID format.\"}";
                statusCode = 400; // Bad Request
            }
        } else {
            jsonResponse = getAllOrders(); // Default: return all orders
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }


    private List<Document> parseOrderItems(List<Map<String, Object>> orderItemsData) {
        List<Document> orderItems = new ArrayList<>();

        if (orderItemsData != null && !orderItemsData.isEmpty()) {
            for (Map<String, Object> itemData : orderItemsData) {
                Document orderItem = new Document();
                orderItem.append("productID", ((Double) itemData.get("productID")).intValue());
                orderItem.append("quantity", ((Double) itemData.get("quantity")).intValue());
                orderItem.append("price", ((Double) itemData.get("price")).doubleValue());
                orderItems.add(orderItem);
            }
        }
        return orderItems;
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String responseMessage;
        int statusCode;

        try {
            String jsonData = requestBody.toString();

            // Parse the entire JSON into a Map
            Map<String, Object> jsonObject = new Gson().fromJson(jsonData, new TypeToken<Map<String, Object>>() {
            }.getType());

            // Extract values from the Map
            String time = (String) jsonObject.get("time");
            double totalAmount = ((Double) jsonObject.get("totalAmount"));
            int customerID = ((Double) jsonObject.get("customerID")).intValue();
            String status = (String) jsonObject.get("status");
            int shipperID = ((Double) jsonObject.get("shipperID")).intValue();

            // Auto-generate the orderID
            int orderID = generateOrderID();

            // Extract and parse "orderItems"
            List<Map<String, Object>> orderItemsList = (List<Map<String, Object>>) jsonObject.get("orderItems");
            List<Document> orderItems = parseOrderItems(orderItemsList);

            // Validate and save order
            if (time != null && !orderItems.isEmpty()) {
                if (saveOrder(orderID, time, totalAmount, customerID, status, shipperID, orderItems)) {
                    responseMessage = "{\"message\": \"Order added successfully!\", \"orderID\": " + orderID + "}";
                    statusCode = 201;
                } else {
                    responseMessage = "{\"message\": \"Failed to add order.\"}";
                    statusCode = 500;
                }
            } else {
                responseMessage = "{\"message\": \"Missing required fields\"}";
                statusCode = 400;
            }
        } catch (Exception e) {
            responseMessage = "{\"message\": \"Internal server error: " + e.getMessage() + "\"}";
            statusCode = 500;
        }

        sendResponse(exchange, responseMessage, statusCode);
    }


    // Method to generate a unique OrderID
    private int generateOrderID() {
        // Generate a random orderID in the range 1 to 100000
        return (int) (Math.random() * 100000) + 1;
    }


    private void handlePut(HttpExchange exchange) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String jsonData = requestBody.toString();
        String path = exchange.getRequestURI().getPath();
        String responseMessage;
        int statusCode;

        try {
            int orderID = Integer.parseInt(getValueFromJson(jsonData, "OrderID"));

            if (path.contains("/updateShipperID")) {
                // Update ShipperID
                int shipperID = Integer.parseInt(getValueFromJson(jsonData, "ShipperID"));
                if (updateShipperID(orderID, shipperID)) {
                    responseMessage = "{\"message\": \"ShipperID updated successfully!\"}";
                    statusCode = 200;
                } else {
                    responseMessage = "{\"message\": \"Failed to update ShipperID.\"}";
                    statusCode = 500;
                }
            } else if (path.contains("/updateStatus")) {
                // Update Status to "paid"
                if (updateOrderStatusToPaid(orderID)) {
                    responseMessage = "{\"message\": \"Order status updated to 'paid' successfully!\"}";
                    statusCode = 200;
                } else {
                    responseMessage = "{\"message\": \"Failed to update order status.\"}";
                    statusCode = 500;
                }
            } else {
                responseMessage = "{\"message\": \"Invalid endpoint.\"}";
                statusCode = 400;
            }
        } catch (Exception e) {
            responseMessage = "{\"message\": \"Internal server error: " + e.getMessage() + "\"}";
            statusCode = 500;
        }

        sendResponse(exchange, responseMessage, statusCode);
    }


    private boolean updateOrder(String orderID, String time, double totalAmount, String status, int shipperID) {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");

        // Tìm đơn hàng cần cập nhật
        Document existingOrder = orderCollection.find(Filters.eq("OrderID", Integer.parseInt(orderID))).first();

        if (existingOrder != null) {
            // Cập nhật các trường trong đơn hàng
            Document updatedOrder = new Document();
            if (time != null && !time.isEmpty()) updatedOrder.append("Time", time);
            if (totalAmount != -1) updatedOrder.append("TotalAmount", totalAmount);
            if (status != null && !status.isEmpty()) updatedOrder.append("Status", status);
            if (shipperID != -1) updatedOrder.append("ShipperID", shipperID);

            // Cập nhật trong MongoDB
            orderCollection.updateOne(Filters.eq("OrderID", Integer.parseInt(orderID)), new Document("$set", updatedOrder));
            return true;
        } else {
            return false;
        }
    }

    private Map<String, Object> parseJsonToMap(String jsonData) {
        Map<String, Object> map = new HashMap<>();

        String[] pairs = jsonData.replaceAll("[{}\"]", "").split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                if (value.matches("\\d+(\\.\\d+)?")) {
                    if (value.contains(".")) {
                        map.put(key, Double.parseDouble(value));
                    } else {
                        map.put(key, Integer.parseInt(value));
                    }
                } else {
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    private boolean deleteOrder(String orderID) {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");

        // Tìm và xóa đơn hàng theo OrderID
        Document existingOrder = orderCollection.find(Filters.eq("OrderID", Integer.parseInt(orderID))).first();

        if (existingOrder != null) {
            // Xóa đơn hàng nếu tìm thấy
            orderCollection.deleteOne(Filters.eq("OrderID", Integer.parseInt(orderID)));
            return true;
        } else {
            return false;
        }
    }


    private void handleDelete(HttpExchange exchange) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String jsonData = requestBody.toString();
        String orderID = getValueFromJson(jsonData, "OrderID");

        String responseMessage;
        int statusCode;

        if (orderID != null && !orderID.isEmpty()) {
            if (deleteOrder(orderID)) {
                responseMessage = "{\"message\": \"Order deleted successfully!\"}";
                statusCode = 200;
            } else {
                responseMessage = "{\"message\": \"Failed to delete order.\"}";
                statusCode = 500;
            }
        } else {
            responseMessage = "{\"message\": \"Invalid JSON data.\"}";
            statusCode = 400;
        }

        sendResponse(exchange, responseMessage, statusCode);
    }

    private String findOrderByID(int orderID) {
        // Example: In-memory order data (replace with actual data source)
        Map<Integer, Map<String, Object>> orders = new HashMap<>();
        Map<String, Object> order = new HashMap<>();
        order.put("orderID", 12345);
        order.put("customerID", 67890);
        order.put("shipperID", 11223);
        order.put("status", "paid");
        order.put("time", "2024-12-16 17:40:43");
        order.put("totalAmount", 150.75);
        orders.put(12345, order);

        // Retrieve the order by orderID
        Map<String, Object> foundOrder = orders.get(orderID);
        if (foundOrder != null) {
            // Convert the order map to a JSON string
            Gson gson = new Gson();
            return gson.toJson(foundOrder);
        } else {
            return "{\"message\": \"Order not found.\"}";
        }
    }

    private String getAllOrders() {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");
        List<Document> orders = orderCollection.find().into(new ArrayList<>());

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (Document order : orders) {
            jsonBuilder.append(order.toJson()).append(",");
        }

        if (jsonBuilder.length() > 1) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove trailing comma
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    private String getValueFromJson(String jsonData, String key) {
        String keyPattern = "\"" + key + "\":";
        int startIndex = jsonData.indexOf(keyPattern);

        if (startIndex == -1) {
            return null;
        }

        startIndex += keyPattern.length();
        int endIndex = jsonData.indexOf(",", startIndex);

        if (endIndex == -1) {
            endIndex = jsonData.indexOf("}", startIndex);
        }

        return jsonData.substring(startIndex, endIndex).replaceAll("[\"{}]", "").trim();
    }

    private void sendResponse(HttpExchange exchange, String responseMessage, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseMessage.getBytes());
        }
    }

    // Find orders where ShipperID = 0
    private String findOrdersWithShipperIDZero() {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");

        // Query to find orders where ShipperID = 0
        List<Document> orders = orderCollection.find(Filters.eq("ShipperID", 0)).into(new ArrayList<>());

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (Document order : orders) {
            jsonBuilder.append(order.toJson()).append(",");
        }

        if (jsonBuilder.length() > 1) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove trailing comma
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    // Find orders where Status = "debt"
    private String findOrdersWithStatusDebt() {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");

        // Query to find orders where Status = "debt"
        List<Document> orders = orderCollection.find(Filters.eq("Status", "debt")).into(new ArrayList<>());

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (Document order : orders) {
            jsonBuilder.append(order.toJson()).append(",");
        }

        if (jsonBuilder.length() > 1) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove trailing comma
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }


    private boolean saveOrder(int orderID, String time, double totalAmount, int customerID, String status, int shipperID, List<Document> orderItems) {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");

        Document newOrder = new Document()
                .append("OrderID", orderID)
                .append("Time", time)
                .append("TotalAmount", totalAmount)
                .append("CustomerID", customerID)
                .append("Status", status)
                .append("ShipperID", shipperID)
                .append("OrderItems", orderItems);

        orderCollection.insertOne(newOrder);
        return true;
    }

    private boolean updateShipperID(int orderID, int shipperID) {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");

        // Find the order with the given orderID and update its ShipperID
        Document result = orderCollection.findOneAndUpdate(
                Filters.eq("OrderID", orderID),
                new Document("$set", new Document("ShipperID", shipperID))
        );

        return result != null; // If a document was found and updated, return true
    }

    private boolean updateOrderStatusToPaid(int orderID) {
        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> orderCollection = database.getCollection("Order");

        // Find the order with the given orderID and update its Status to "paid"
        Document result = orderCollection.findOneAndUpdate(
                Filters.eq("OrderID", orderID),
                new Document("$set", new Document("Status", "paid"))
        );

        return result != null; // If a document was found and updated, return true
    }


}
