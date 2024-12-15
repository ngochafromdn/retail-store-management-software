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

public class OrderHandler implements HttpHandler {

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
        String jsonResponse = getOrders();
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

        String jsonData = requestBody.toString();
        String time = extractJsonValue(jsonData, "Time");
        String totalAmount = extractJsonValue(jsonData, "TotalAmount");
        String customerID = extractJsonValue(jsonData, "CustomerID");
        String status = extractJsonValue(jsonData, "Status");
        String shipperID = extractJsonValue(jsonData, "ShipperID");

        if (time != null && totalAmount != null && customerID != null && status != null && shipperID != null) {
            if (saveOrder(time, Double.parseDouble(totalAmount), Integer.parseInt(customerID), status, Integer.parseInt(shipperID))) {
                responseMessage = "{\"message\": \"Order added successfully!\"}";
                statusCode = 201;
            } else {
                responseMessage = "{\"message\": \"Failed to add order.\"}";
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
        // Handle updating order information
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        // Handle deleting an order
    }

    private String getOrders() {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection();
        try (Connection conn = sqlConnection.getConnection()) {
            String query = "SELECT * FROM Order";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                jsonBuilder.append("{")
                        .append("\"OrderID\":").append(rs.getInt("OrderID")).append(",")
                        .append("\"Time\":\"").append(rs.getString("Time")).append("\",")
                        .append("\"TotalAmount\":").append(rs.getDouble("TotalAmount")).append(",")
                        .append("\"CustomerID\":").append(rs.getInt("CustomerID")).append(",")
                        .append("\"Status\":\"").append(rs.getString("Status")).append("\",")
                        .append("\"ShipperID\":").append(rs.getInt("ShipperID"))
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

    private boolean saveOrder(String time, double totalAmount, int customerID, String status, int shipperID) {
        try (Connection conn = new SQLConnection().getConnection()) {
            String query = "INSERT INTO Order (Time, TotalAmount, CustomerID, Status, ShipperID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, time);
                pstmt.setDouble(2, totalAmount);
                pstmt.setInt(3, customerID);
                pstmt.setString(4, status);
                pstmt.setInt(5, shipperID);

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
