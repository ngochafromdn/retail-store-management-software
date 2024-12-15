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

public class ProductHandler implements HttpHandler {

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
            default:
                exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String jsonResponse = getProductsAsJSON();

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
        if (saveProductToDatabase(requestBody.toString())) {
            responseMessage = "{\"message\": \"Product added successfully!\"}";
            statusCode = 201;
        } else {
            responseMessage = "{\"message\": \"Failed to add product.\"}";
            statusCode = 500;
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseMessage.getBytes());
        }
    }

    private String getProductsAsJSON() {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection();
        try (Connection conn = sqlConnection.getConnection()) {
            String query = "SELECT * FROM Product";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                jsonBuilder.append("{")
                        .append("\"product_id\":").append(rs.getInt("product_id")).append(",")
                        .append("\"name\":\"").append(rs.getString("Name")).append("\",")
                        .append("\"price\":").append(rs.getDouble("UnitPrice")).append(",")
                        .append("\"quantity\":").append(rs.getInt("Quantity"))
                        .append("},");
            }

            if (jsonBuilder.length() > 1) {
                jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

    private boolean saveProductToDatabase(String jsonData) {
        SQLConnection sqlConnection = new SQLConnection();

        // Ensure the JSON data is not null or empty
        if (jsonData == null || jsonData.isEmpty()) {
            System.err.println("Received empty JSON data.");
            return false;
        }

        // Extract product fields from JSON (name, price, quantity)
        String name = extractFieldFromJson(jsonData, "name");
        double price = extractDoubleFromJson(jsonData, "price");
        int quantity = extractIntFromJson(jsonData, "quantity");

        if (name == null || price == -1 || quantity == -1) {
            System.err.println("Error parsing JSON fields.");
            return false;
        }

        try (Connection conn = sqlConnection.getConnection()) {
            String query = "INSERT INTO Product (Name, UnitPrice, Quantity) VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Set the parameters for the query
                pstmt.setString(1, name);
                pstmt.setDouble(2, price);
                pstmt.setInt(3, quantity);

                // Execute the update
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0; // Return true if product was added successfully
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Helper method to extract a string field from the JSON
    private String extractFieldFromJson(String jsonData, String field) {
        try {
            int start = jsonData.indexOf("\"" + field + "\":\"") + field.length() + 3; // Find the start of the field
            int end = jsonData.indexOf("\"", start); // Find the end of the field
            if (start != -1 && end != -1) {
                return jsonData.substring(start, end); // Return the field value
            }
        } catch (Exception e) {
            System.err.println("Error extracting field '" + field + "' from JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to extract a double field from the JSON
    private double extractDoubleFromJson(String jsonData, String field) {
        try {
            int start = jsonData.indexOf("\"" + field + "\":") + field.length() + 3; // Find the start of the field
            int end = jsonData.indexOf(",", start); // Find the comma after the value
            if (end == -1) end = jsonData.indexOf("}", start); // Handle last field case

            if (start != -1 && end != -1) {
                return Double.parseDouble(jsonData.substring(start, end).trim()); // Return the field value as double
            }
        } catch (Exception e) {
            System.err.println("Error extracting field '" + field + "' from JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 if the value is not found or is invalid
    }

    // Helper method to extract an integer field from the JSON
    private int extractIntFromJson(String jsonData, String field) {
        try {
            int start = jsonData.indexOf("\"" + field + "\":") + field.length() + 3; // Find the start of the field
            int end = jsonData.indexOf(",", start); // Find the comma after the value
            if (end == -1) end = jsonData.indexOf("}", start); // Handle last field case

            if (start != -1 && end != -1) {
                return Integer.parseInt(jsonData.substring(start, end).trim()); // Return the field value as int
            }
        } catch (Exception e) {
            System.err.println("Error extracting field '" + field + "' from JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 if the value is not found or is invalid
    }

}
