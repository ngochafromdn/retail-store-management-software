package Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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
        String responseMessage;
        int statusCode;

        // Extract query from URI
        String query = exchange.getRequestURI().getQuery();
        String accountNameParam = extractQueryParam(query, "accountName");

        if (accountNameParam != null) {
            try {
                // Use the parameter directly as a String
                String accountName = accountNameParam;

                // Fetch account information
                Map<String, String> accountInfo = getAccountInfoByName(accountName);
                System.out.println(accountInfo.toString());

                if (accountInfo != null) {
                    responseMessage = String.format(
                            "{\"accountID\": \"%s\", \"password\": \"%s\", \"accountType\": \"%s\"}",
                            accountInfo.get("accountID"), // Fixed formatting assumption
                            accountInfo.get("password"),
                            accountInfo.get("accountType")
                    );
                    statusCode = 200;
                } else {
                    responseMessage = "{\"message\": \"Account not found.\"}";
                    statusCode = 404;
                }
            } catch (Exception e) { // Broader exception handling for robustness
                responseMessage = "{\"message\": \"An error occurred while processing the request.\"}";
                statusCode = 500;
            }
        } else {
            responseMessage = "{\"message\": \"Missing 'accountName' parameter.\"}";
            statusCode = 400;
        }

        // Set response headers and body
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseMessage.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseMessage.getBytes());
        }
    }

    // Example helper function to extract a query parameter
    private String extractQueryParam(String query, String key) {
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(key)) {
                return keyValue[1];
            }
        }
        return null;
    }


    /**
     * Retrieves the password for a given account_ID from the database.
     *
     * @param accountID The ID of the account.
     * @return The password if found, or null if not.
     */
    private String getPasswordByAccountID(int accountID) {
        String query = "SELECT Password FROM Account WHERE AccountID = ?";
        try (Connection conn = new SQLConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("Password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves the account information for a given account_ID from the database.
     * The account information includes the password and account type.
     *
     * @param accountID
     * @return
     */
    private Map<String, String> getAccountInfoByName(String accountName) {
        String query = "SELECT AccountID, Password, AccountType FROM Account WHERE AccountName = ?";
        try (Connection conn = new SQLConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Set the accountName parameter in the query
            pstmt.setString(1, accountName);

            // Execute query
            ResultSet rs = pstmt.executeQuery();

            // Process the result set
            if (rs.next()) {
                Map<String, String> accountInfo = new HashMap<>();
                accountInfo.put("accountID", rs.getInt("AccountID") + "");
                accountInfo.put("password", rs.getString("Password"));
                accountInfo.put("accountType", rs.getString("AccountType"));

                return accountInfo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no result or an exception occurs
    }

    /**
     * Extracts the value of a query parameter from the URL query string.
     *
     * @param query The full query string (e.g., "account_ID=123").
     * @param key   The parameter key to look for.
     * @return The value of the parameter, or null if not found.
     */
//    private String extractQueryParam(String query, String key) {
//        if (query == null) return null;
//        String[] pairs = query.split("&");
//        for (String pair : pairs) {
//            String[] keyValue = pair.split("=");
//            if (keyValue.length == 2 && keyValue[0].equals(key)) {
//                return keyValue[1];
//            }
//        }
//        return null;
//    }
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
