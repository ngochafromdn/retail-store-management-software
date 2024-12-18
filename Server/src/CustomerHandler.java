package Server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


public class CustomerHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":

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
        String query = exchange.getRequestURI().getQuery(); // Extract query string
        String jsonResponse;

        if (query != null) {
            Map<String, String> queryParams = parseQueryParams(query);
            if (queryParams.containsKey("CustomerID")) {
                int customerID = Integer.parseInt(queryParams.get("CustomerID"));
                jsonResponse = getCustomerByID(customerID); // Fetch specific customer by ID
            } else if (queryParams.containsKey("AccountID")) {
                int accountID = Integer.parseInt(queryParams.get("AccountID"));
                jsonResponse = getCustomerByAccountID(accountID); // Fetch customers by AccountID
            } else {
                jsonResponse = getCustomers(); // Fetch all customers if no specific parameter provided
            }
        } else {
            jsonResponse = getCustomers(); // Fetch all customers if no query parameters
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParams;
    }


    private String getCustomerByAccountID(int accountID) {
        StringBuilder jsonBuilder = new StringBuilder("[");
        SQLConnection sqlConnection = new SQLConnection(); // Create an instance of SQLConnection
        try (Connection conn = sqlConnection.getConnection()) { // Use the instance to get a connection
            String query = "SELECT * FROM Customer WHERE AccountID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, accountID);
                try (ResultSet rs = pstmt.executeQuery()) {
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (jsonBuilder.length() > 1) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1); // Remove trailing comma
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }


    private Integer getIntegerFromObject(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                System.out.println("Failed to parse integer from string: " + obj);
            }
        }
        return null;
    }

    private String getStringFromObject(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        // Read the request body
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String jsonData = requestBody.toString();
        // Create a Gson instance
        Gson gson = new Gson();

        // Define the type for the Map
        Type mapType = new TypeToken<Map<String, Object>>() {
        }.getType();

        // Parse the JSON string into a Map
        // Parse the JSON string into a Map
        Map<String, Object> customerData = gson.fromJson(jsonData, mapType);

        // Access and validate the data from the Map
        Integer customerID = getIntegerFromObject(customerData.get("CustomerID"));
        String name = getStringFromObject(customerData.get("Name"));
        String number = getStringFromObject(customerData.get("Number"));
        String address = getStringFromObject(customerData.get("Address"));
        Integer accountID = getIntegerFromObject(customerData.get("AccountID"));
        String bankInformation = getStringFromObject(customerData.get("BankInformation"));


        System.out.println("Extracted values:");
        System.out.println("CustomerID: " + customerID);
        System.out.println("Name: " + name);
        System.out.println("Number: " + number);
        System.out.println("Address: " + address);
        System.out.println("AccountID: " + accountID);
        System.out.println("BankInformation: " + bankInformation);

        String responseMessage;
        int statusCode;

        // Validate extracted values
        if (customerID != null && name != null && number != null && address != null && accountID != null && bankInformation != null) {
            // Attempt to update the customer in the database
            boolean updateResult = updateCustomer(customerID, name, number, address, accountID, bankInformation);
            System.out.println("Update result: " + updateResult);

            if (updateResult) {
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

        // Send the response
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseMessage.getBytes());
        }
        System.out.println("Response sent with status code: " + statusCode);
        System.out.println("Response message: " + responseMessage);
    }

    // Method to extract values from JSON data
    private String extractJsonValue(String jsonData, String key) {
        // Simple extraction logic; consider using a JSON parsing library for robustness
        String searchKey = "\"" + key + "\":\"";
        int startIndex = jsonData.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        startIndex += searchKey.length();
        int endIndex = jsonData.indexOf("\"", startIndex);
        if (endIndex == -1) {
            return null;
        }
        return jsonData.substring(startIndex, endIndex);
    }


    private boolean updateCustomer(int customerID, String name, String number, String address, int accountID, String bankInformation) {
        String query = "UPDATE Customer SET Name = ?, Number = ?, Address = ?, AccountID = ?, BankInformation = ? WHERE CustomerID = ?";
        try (Connection conn = new SQLConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, number);
            pstmt.setString(3, address);
            pstmt.setInt(4, accountID);
            pstmt.setString(5, bankInformation);
            pstmt.setInt(6, customerID);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

    private String getCustomerByID(int customerID) {
        StringBuilder jsonBuilder = new StringBuilder();
        try (Connection conn = new SQLConnection().getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Database connection failed.");
                return "{}"; // Return empty JSON object if connection fails
            }
            String query = "SELECT * FROM Customer WHERE CustomerID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, customerID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        jsonBuilder.append("{")
                                .append("\"CustomerID\":").append(rs.getInt("CustomerID")).append(",")
                                .append("\"Name\":\"").append(rs.getString("Name")).append("\",")
                                .append("\"Number\":\"").append(rs.getString("Number")).append("\",")
                                .append("\"Address\":\"").append(rs.getString("Address")).append("\",")
                                .append("\"AccountID\":").append(rs.getInt("AccountID")).append(",")
                                .append("\"BankInformation\":\"").append(rs.getString("BankInformation")).append("\"")
                                .append("}");
                    } else {
                        System.err.println("No customer found with ID: " + customerID);
                        return "{}"; // Return empty JSON object if no data found
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{}"; // Return empty JSON object in case of an exception
        }
        return jsonBuilder.toString();
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


}


