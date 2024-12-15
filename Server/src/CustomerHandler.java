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

public class CustomerHandler implements HttpHandler {

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
        String jsonResponse = getCustomers();
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
        String name = extractJsonValue(jsonData, "Name");
        String number = extractJsonValue(jsonData, "Number");
        String address = extractJsonValue(jsonData, "Address");
        String accountID = extractJsonValue(jsonData, "AccountID");
        String bankInformation = extractJsonValue(jsonData, "BankInformation");

        if (name != null && number != null && address != null && accountID != null && bankInformation != null) {
            if (saveCustomer(name, number, address, Integer.parseInt(accountID), bankInformation)) {
                responseMessage = "{\"message\": \"Customer added successfully!\"}";
                statusCode = 201;
            } else {
                responseMessage = "{\"message\": \"Failed to add customer.\"}";
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
        String customerID = extractJsonValue(jsonData, "CustomerID");
        String name = extractJsonValue(jsonData, "Name");
        String number = extractJsonValue(jsonData, "Number");
        String address = extractJsonValue(jsonData, "Address");
        String accountID = extractJsonValue(jsonData, "AccountID");
        String bankInformation = extractJsonValue(jsonData, "BankInformation");

        if (customerID != null && name != null && number != null && address != null && accountID != null && bankInformation != null) {
            if (updateCustomer(Integer.parseInt(customerID), name, number, address, Integer.parseInt(accountID), bankInformation)) {
                responseMessage = "{\"message\": \"Customer updated successfully!\"}";
                statusCode = 200;
            } else {
                responseMessage = "{\"message\": \"Failed to update customer.\"}";
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

    private void handleDelete(HttpExchange exchange) throws IOException {
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
        String customerID = extractJsonValue(jsonData, "CustomerID");

        if (customerID != null) {
            if (deleteCustomer(Integer.parseInt(customerID))) {
                responseMessage = "{\"message\": \"Customer deleted successfully!\"}";
                statusCode = 200;
            } else {
                responseMessage = "{\"message\": \"Failed to delete customer.\"}";
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

    private String getCustomers() {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection(); // Create an instance of SQLConnection
        try (Connection conn = sqlConnection.getConnection()) { // Now, use the instance to get a connection
            String query = "SELECT * FROM Customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                jsonBuilder.append("{")
                        .append("\"CustomerID\":").append(rs.getInt("CustomerID")).append(",")
                        .append("\"Name\":\"").append(rs.getString("Name")).append("\",")
                        .append("\"Number\":\"").append(rs.getString("Number")).append("\",")
                        .append("\"Address\":\"").append(rs.getString("Address")).append("\",")
                        .append("\"AccountID\":").append(rs.getInt("AccountID")).append(",")
                        .append("\"BankInformation\":\"").append(rs.getString("BankInformation")).append("\"")
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

    private boolean saveCustomer(String name, String number, String address, int accountID, String bankInformation) {
        try (Connection conn = new SQLConnection().getConnection()) {
            String query = "INSERT INTO Customer (Name, Number, Address, AccountID, BankInformation) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, number);
                pstmt.setString(3, address);
                pstmt.setInt(4, accountID);
                pstmt.setString(5, bankInformation);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateCustomer(int customerID, String name, String number, String address, int accountID, String bankInformation) {
        try (Connection conn = new SQLConnection().getConnection()) {
            String query = "UPDATE Customer SET Name = ?, Number = ?, Address = ?, AccountID = ?, BankInformation = ? WHERE CustomerID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, name);
                pstmt.setString(2, number);
                pstmt.setString(3, address);
                pstmt.setInt(4, accountID);
                pstmt.setString(5, bankInformation);
                pstmt.setInt(6, customerID);

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteCustomer(int customerID) {
        try (Connection conn = new SQLConnection().getConnection()) {
            String query = "DELETE FROM Customer WHERE CustomerID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, customerID);

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
