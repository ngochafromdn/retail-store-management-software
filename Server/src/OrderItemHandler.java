package Server;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderItemHandler implements HttpHandler {

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
        String jsonResponse = getOrderItems();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
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

        // Simple JSON parsing: extract values using basic string operations
        String jsonData = requestBody.toString();
        String orderID = extractJsonValue(jsonData, "OrderID");
        String productID = extractJsonValue(jsonData, "ProductID");
        String quantity = extractJsonValue(jsonData, "Quantity");
        String price = extractJsonValue(jsonData, "Price");

        if (orderID != null && productID != null && quantity != null && price != null) {
            if (saveOrderItem(Integer.parseInt(orderID), Integer.parseInt(productID), Integer.parseInt(quantity), Double.parseDouble(price))) {
                responseMessage = "{\"message\": \"OrderItem added successfully!\"}";
                statusCode = 201;
            } else {
                responseMessage = "{\"message\": \"Failed to add OrderItem.\"}";
                statusCode = 500;
            }
        } else {
            responseMessage = "{\"message\": \"Invalid JSON data.\"}";
            statusCode = 400;
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseMessage.getBytes());
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        // Handle updating order item information
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        // Handle deleting an order item
    }

    private String getOrderItems() {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection();
        try (Connection conn = sqlConnection.getConnection()) {
            String query = "SELECT * FROM OrderItem";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                jsonBuilder.append("{")
                        .append("\"OrderItemID\":").append(rs.getInt("OrderItemID")).append(",")
                        .append("\"OrderID\":").append(rs.getInt("OrderID")).append(",")
                        .append("\"ProductID\":").append(rs.getInt("ProductID")).append(",")
                        .append("\"Quantity\":").append(rs.getInt("Quantity")).append(",")
                        .append("\"Price\":").append(rs.getDouble("Price"))
                        .append("},");
            }

            if (jsonBuilder.length() > 1) {
                jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove trailing comma
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    private boolean saveOrderItem(int orderID, int productID, int quantity, double price) {
        try (Connection conn = new SQLConnection().getConnection()) {
            String query = "INSERT INTO OrderItem (OrderID, ProductID, Quantity, Price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, orderID);
                pstmt.setInt(2, productID);
                pstmt.setInt(3, quantity);
                pstmt.setDouble(4, price);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String extractJsonValue(String jsonData, String key) {
        int startIndex = jsonData.indexOf("\"" + key + "\":");
        if (startIndex == -1) return null;

        startIndex += key.length() + 3; // Move past the key and `":`
        int endIndex = jsonData.indexOf("\"", startIndex);

        return (endIndex != -1) ? jsonData.substring(startIndex, endIndex) : null;
    }
}
