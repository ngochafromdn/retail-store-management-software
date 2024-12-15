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

public class AccountHandler implements HttpHandler {

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
        String jsonResponse = getAccounts();
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
        String accountName = extractJsonValue(jsonData, "AccountName");
        String password = extractJsonValue(jsonData, "Password");
        String accountType = extractJsonValue(jsonData, "AccountType");

        if (accountName != null && password != null && accountType != null) {
            if (saveAccount(accountName, password, accountType)) {
                responseMessage = "{\"message\": \"Account added successfully!\"}";
                statusCode = 201;
            } else {
                responseMessage = "{\"message\": \"Failed to add account.\"}";
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
        // Handle updating an existing account (similar to POST)
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        // Handle deleting an account
    }

    private String getAccounts() {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection(); // Create an instance of SQLConnection
        try (Connection conn = sqlConnection.getConnection()) { // Now, use the instance to get a connection
            String query = "SELECT * FROM Account";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                jsonBuilder.append("{")
                        .append("\"AccountID\":").append(rs.getInt("AccountID")).append(",")
                        .append("\"AccountName\":\"").append(rs.getString("AccountName")).append("\",")
                        .append("\"Password\":\"").append(rs.getString("Password")).append("\",")
                        .append("\"AccountType\":\"").append(rs.getString("AccountType")).append("\"")
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

    private boolean saveAccount(String accountName, String password, String accountType) {
        try (Connection conn = new SQLConnection().getConnection()) { // Create an instance of SQLConnection
            String query = "INSERT INTO Account (AccountName, Password, AccountType) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, accountName);
                pstmt.setString(2, password);
                pstmt.setString(3, accountType);

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
