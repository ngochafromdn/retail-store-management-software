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

public class SupplierHandler implements HttpHandler {

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
        String jsonResponse = getSuppliersAsJSON();

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
        if (saveSupplierToDatabase(requestBody.toString())) {
            responseMessage = "{\"message\": \"Supplier added successfully!\"}";
            statusCode = 201;
        } else {
            responseMessage = "{\"message\": \"Failed to add supplier.\"}";
            statusCode = 500;
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseMessage.getBytes());
        }
    }

    private String getSuppliersAsJSON() {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection();
        try (Connection conn = sqlConnection.getConnection()) {
            String query = "SELECT * FROM Supplier";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                jsonBuilder.append("{")
                        .append("\"supplier_id\":").append(rs.getInt("SupplierID")).append(",")
                        .append("\"name\":\"").append(rs.getString("Name")).append("\"")
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

    private boolean saveSupplierToDatabase(String jsonData) {
        SQLConnection sqlConnection = new SQLConnection();
        try (Connection conn = sqlConnection.getConnection()) {
            // Define the SQL query
            String query = "INSERT INTO Supplier (Name) VALUES (?)";

            // Prepare the statement with the query
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Manually parse the "name" field from the JSON string
                String name = extractNameFromJson(jsonData);

                // If name extraction was successful, set the parameter
                if (name != null) {
                    pstmt.setString(1, name);

                    // Execute the update
                    int rowsAffected = pstmt.executeUpdate();
                    return rowsAffected > 0; // Return true if rows were affected
                }
            }
        } catch (SQLException e) {
            // Log SQL exception with a meaningful message
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Catch all other exceptions
            System.err.println("Error processing JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to extract the "name" field from the JSON string
    private String extractNameFromJson(String jsonData) {
        try {
            int start = jsonData.indexOf("\"name\":\"") + 8; // Find the start of the name field
            int end = jsonData.indexOf("\"", start); // Find the end of the name field
            if (start != -1 && end != -1) {
                return jsonData.substring(start, end); // Extract the name
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if there's an error or if the name is not found
    }
}
