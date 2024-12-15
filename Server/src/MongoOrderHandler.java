package Server;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.google.gson.Gson;
import org.bson.Document;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.reflect.TypeToken;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.mongodb.client.model.Filters;

public class MongoOrderHandler implements HttpHandler {

    private MongoDBConnection mongoDBConnection;

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
        String jsonResponse = getAllOrders();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    private List<Document> parseOrderItems(String orderItemsData) {
        List<Document> orderItems = new ArrayList<>();
        int orderItemID = 1; // Start with 1 and increment for each order item

        if (orderItemsData != null && !orderItemsData.isEmpty()) {
            // Assume JSON array format: [ { "ProductID": ..., "Quantity": ..., "Price": ... }, { ... } ]
            List<Map<String, Object>> items = new Gson().fromJson(orderItemsData, new TypeToken<List<Map<String, Object>>>() {}.getType());

            for (Map<String, Object> itemData : items) {
                Document orderItem = new Document();
                orderItem.append("OrderItemID", orderItemID++); // Auto-increment OrderItemID
                orderItem.append("ProductID", ((Double) itemData.get("ProductID")).intValue());
                orderItem.append("Quantity", ((Double) itemData.get("Quantity")).intValue());
                orderItem.append("Price", ((Double) itemData.get("Price")).doubleValue());
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

            // Lấy giá trị từ JSON
            String time = getValueFromJson(jsonData, "time");
            double totalAmount = Double.parseDouble(getValueFromJson(jsonData, "totalAmount"));
            int customerID = Integer.parseInt(getValueFromJson(jsonData, "customerID"));
            String status = getValueFromJson(jsonData, "status");
            int shipperID = Integer.parseInt(getValueFromJson(jsonData, "shipperID"));

            // Auto-generate the orderID
            int orderID = generateOrderID(); // New method for generating orderID

            // Lấy dữ liệu OrderItems và phân tích
            String orderItemsData = getValueFromJson(jsonData, "orderItems");
            List<Document> orderItems = parseOrderItems(orderItemsData);

            // Kiểm tra Time và OrderItems
            if (time != null && !orderItems.isEmpty()) {
                // Lưu đơn hàng nếu tất cả các trường hợp hợp lệ
                if (saveOrder(orderID, time, totalAmount, customerID, status, shipperID, orderItems)) {
                    responseMessage = String.format("{\"message\": \"Order added successfully!\", \"OrderID\": %d}", orderID);
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

        // Phân tích chuỗi JSON thủ công để chuyển thành Map
        Map<String, Object> dataMap = parseJsonToMap(jsonData);

        String orderID = (String) dataMap.get("OrderID");
        String time = (String) dataMap.get("Time");
        Double totalAmount = (Double) dataMap.get("TotalAmount");
        String status = (String) dataMap.get("Status");
        Integer shipperID = (Integer) dataMap.get("ShipperID");

        // Phản hồi nếu thiếu thông tin cần thiết
        String responseMessage;
        int statusCode;
        if (orderID == null || orderID.isEmpty()) {
            responseMessage = "{\"message\": \"OrderID is required\"}";
            statusCode = 400;
            sendResponse(exchange, responseMessage, statusCode);
            return;
        }

        if (totalAmount == null || shipperID == null) {
            responseMessage = "{\"message\": \"Invalid number format or missing required fields\"}";
            statusCode = 400;
            sendResponse(exchange, responseMessage, statusCode);
            return;
        }

        // Cập nhật thông tin đơn hàng
        if (updateOrder(orderID, time, totalAmount, status, shipperID)) {
            responseMessage = "{\"message\": \"Order updated successfully!\"}";
            statusCode = 200;
        } else {
            responseMessage = "{\"message\": \"Failed to update order.\"}";
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
                .append("OrderItems", orderItems); // Include parsed order items with auto-generated OrderItemID

        orderCollection.insertOne(newOrder);
        return true;
    }



}
