package Server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InventoryHandler implements HttpHandler {

    private final Gson gson = new Gson();
    private final Server.SQLConnection sqlConnection = new Server.SQLConnection();

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
                sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try (Connection connection = sqlConnection.getConnection()) {
            String query = "SELECT * FROM Inventory";
            PreparedStatement statement = connection.prepareStatement(query);
            var resultSet = statement.executeQuery();

            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("[");

            boolean first = true;
            while (resultSet.next()) {
                if (!first) {
                    jsonResponse.append(",");
                }
                JsonObject item = new JsonObject();
                item.addProperty("InventoryID", resultSet.getInt("InventoryID"));
                item.addProperty("ProductID", resultSet.getInt("ProductID"));
                item.addProperty("QuantityReceived", resultSet.getInt("QuantityReceived"));
                item.addProperty("Date", resultSet.getString("Date"));
                item.addProperty("SupplierID", resultSet.getInt("SupplierID"));
                jsonResponse.append(item);
                first = false;
            }

            jsonResponse.append("]");

            sendResponse(exchange, 200, jsonResponse.toString());
        } catch (SQLException e) {
            sendResponse(exchange, 500, "Database error: " + e.getMessage());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try (Connection connection = sqlConnection.getConnection()) {
            JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

            // Validate fields
            Integer productID = getInteger(jsonObject, "ProductID");
            Integer quantityReceived = getInteger(jsonObject, "QuantityReceived");
            String date = getString(jsonObject, "Date");
            Integer supplierID = getInteger(jsonObject, "SupplierID");

            if (productID == null || quantityReceived == null || date == null || supplierID == null) {
                throw new IllegalArgumentException("Missing required fields in JSON object.");
            }

            String query = "INSERT INTO Inventory (ProductID, QuantityReceived, Date, SupplierID) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(1, productID);
            statement.setInt(2, quantityReceived);
            statement.setString(3, date);
            statement.setInt(4, supplierID);

            int rowsInserted = statement.executeUpdate();

            if (rowsInserted > 0) {
                sendResponse(exchange, 201, "Inventory item added successfully.");
            } else {
                sendResponse(exchange, 500, "Failed to add inventory item.");
            }
        } catch (SQLException e) {
            sendResponse(exchange, 500, "Database error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "Invalid request body: " + e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 400, "Invalid request body: " + e.getMessage());
        }
    }

    private Integer getInteger(JsonObject jsonObject, String key) {
        try {
            return jsonObject.has(key) && !jsonObject.get(key).isJsonNull() ? jsonObject.get(key).getAsInt() : null;
        } catch (Exception e) {
            System.err.println("Error parsing integer field '" + key + "': " + e.getMessage());
            return null;
        }
    }

    private String getString(JsonObject jsonObject, String key) {
        try {
            return jsonObject.has(key) && !jsonObject.get(key).isJsonNull() ? jsonObject.get(key).getAsString() : null;
        } catch (Exception e) {
            System.err.println("Error parsing string field '" + key + "': " + e.getMessage());
            return null;
        }
    }


    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);

        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
