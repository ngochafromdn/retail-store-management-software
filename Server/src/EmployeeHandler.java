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

public class EmployeeHandler implements HttpHandler {

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
        String jsonResponse = getEmployees();
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
        String name = extractJsonValue(jsonData, "Name");
        String number = extractJsonValue(jsonData, "Number");
        String field4 = extractJsonValue(jsonData, "Field4");

        if (name != null && number != null && field4 != null) {
            if (saveEmployee(name, number, Integer.parseInt(field4))) {
                responseMessage = "{\"message\": \"Employee added successfully!\"}";
                statusCode = 201;
            } else {
                responseMessage = "{\"message\": \"Failed to add employee.\"}";
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
        // Handle updating an employee's information (similar to POST)
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        // Handle deleting an employee
    }

    private String getEmployees() {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection(); // Create an instance of SQLConnection
        try (Connection conn = sqlConnection.getConnection()) { // Now, use the instance to get a connection
            String query = "SELECT * FROM Employee";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                jsonBuilder.append("{")
                        .append("\"EmployeeID\":").append(rs.getInt("EmployeeID")).append(",")
                        .append("\"Name\":\"").append(rs.getString("Name")).append("\",")
                        .append("\"Number\":\"").append(rs.getString("Number")).append("\",")
                        .append("\"Field4\":").append(rs.getInt("Field4"))
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

    private boolean saveEmployee(String name, String number, int field4) {
        try (Connection conn = new SQLConnection().getConnection()) { // Create an instance of SQLConnection
            String query = "INSERT INTO Employee (Name, Number, Field4) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, number);
                pstmt.setInt(3, field4);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Utility method to extract values from JSON-like string
    private String extractJsonValue(String jsonData, String key) {
        int startIndex = jsonData.indexOf("\"" + key + "\":");
        if (startIndex == -1) return null;

        startIndex += key.length() + 3; // Move past the key and `":`
        int endIndex = jsonData.indexOf("\"", startIndex);

        return (endIndex != -1) ? jsonData.substring(startIndex, endIndex) : null;
    }
}
