package cashier.src.DataAdapter;

import cashier.src.Model.Order;
import cashier.src.Model.OrderItem;
import cashier.src.Model.Order_;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDataAdapter {

    private static final String BASE_URL = "http://localhost:8015/orders";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Order.class, (JsonDeserializer<Order>) (json, typeOfT, context) -> {
                JsonObject jsonObject = json.getAsJsonObject();
                Order order = new Order();

                // Map _id.$oid to id
                if (jsonObject.has("_id") && jsonObject.get("_id").getAsJsonObject().has("$oid")) {
                    order.setId(jsonObject.get("_id").getAsJsonObject().get("$oid").getAsString());
                }

                // Map other fields
                if (jsonObject.has("OrderID")) {
                    order.setOrderID(jsonObject.get("OrderID").getAsInt());
                }
                if (jsonObject.has("Time")) {
                    order.setTime(jsonObject.get("Time").getAsString());
                }
                if (jsonObject.has("TotalAmount")) {
                    order.setTotalAmount(jsonObject.get("TotalAmount").getAsDouble());
                }
                if (jsonObject.has("CustomerID")) {
                    order.setCustomerID(jsonObject.get("CustomerID").getAsInt());
                }
                if (jsonObject.has("Status")) {
                    order.setStatus(jsonObject.get("Status").getAsString());
                }
                if (jsonObject.has("ShipperID")) {
                    order.setShipperID(jsonObject.get("ShipperID").getAsInt());
                }

                // Map OrderItems
                if (jsonObject.has("OrderItems")) {
                    List<OrderItem> orderItems = context.deserialize(jsonObject.get("OrderItems"), new TypeToken<List<OrderItem>>() {
                    }.getType());
                    order.setOrderItems(orderItems);
                }

                return order;
            }).create();

    public static OrderItem createOrderItem(int productID, int quantity) {
        Map<String, Object> productDetails = ProductDataAdapter.getProductByID(productID);
        if (productDetails != null) {
            System.out.println(productDetails);
            double price = (Double) productDetails.get("price");
            return new OrderItem(productID, quantity, price);
        } else {
            System.err.println("Failed to create OrderItem. ProductID not found.");
            return null;
        }
    }


    public static int createNewOrder(Order_ newOrder) {
        try {
            // Establish connection
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true); // Enable writing to the connection

            // Convert order object to JSON and send in the request body
            String jsonPayload = gson.toJson(newOrder);
            System.out.println(jsonPayload);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Handle the server's response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) { // 201 Created
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the response to get the order ID
                    JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
                    if (jsonResponse.has("orderID")) {
                        return jsonResponse.get("orderID").getAsInt();
                    } else {
                        System.err.println("Order created but no orderID returned in response.");
                    }
                }
            } else {
                System.err.println("Failed to create new order. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error creating new order: " + e.getMessage());
        }
        return 0; // Return 0 if order creation failed
    }

    public static void updatePaymentMethod(int orderID, String status) {
        // Update the order payment status/method
        try {
            URL url = new URL(BASE_URL + "/" + orderID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Assuming the API accepts a JSON with a status field to update payment status
            String jsonPayload = String.format("{\"Status\": \"%s\"}", status);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Order payment status updated successfully.");
            } else {
                System.err.println("Failed to update payment status. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error updating payment status: " + e.getMessage());
        }
    }

    public static List<Order> getAllOrders() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse JSON using a custom deserialization process
                    return gson.fromJson(response.toString(), new TypeToken<List<Order>>() {
                    }.getType());
                }
            } else {
                System.err.println("Failed to fetch orders. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
        }
        return new java.util.ArrayList<>();
    }

    public static boolean updateShipperID(int orderID, int shipperID) {
        try {
            // Construct the URL with the orderID
            URL url = new URL(BASE_URL + "/" + orderID + "/updateShipperID");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create the JSON payload that includes both orderID and shipperID
            String jsonPayload = String.format("{\"OrderID\": %d, \"ShipperID\": %d}", orderID, shipperID);

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            System.err.println("Error updating shipper ID: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateOrderStatus(int orderID, String status) {
        try {
            // Construct the URL with the orderID
            URL url = new URL(BASE_URL + "/orders/" + orderID + "/updateStatus");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create the JSON payload
            String jsonPayload = String.format("{\"OrderID\": %d, \"Status\": \"%s\"}", orderID, status);

            // Write the JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
        return false;
    }


    public static void main(String[] args) {
        // create new
        System.out.println("=== Creating a New Order ===");
        Order_ newOrder = new Order_();
        newOrder.setTime("2024-12-16T10:00:00Z");
        newOrder.setTotalAmount(200.50);
        newOrder.setCustomerID(1);
        newOrder.setStatus("Paid");
        newOrder.setShipperID(3);
        OrderItem item1 = createOrderItem(1, 3);
        OrderItem item2 = createOrderItem(2, 2);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(item1);
        orderItems.add(item2);
        newOrder.setOrderItems(orderItems);

        int newOrderID = createNewOrder(newOrder);
        if (newOrderID > 0) {
            System.out.println("New order created successfully with OrderID: " + newOrderID);
        } else {
            System.out.println("Failed to create new order.");
        }


//        System.out.println("\n=== Updating Payment Status ===");
//        System.out.println(updateOrderStatus(12345, "Paid"));

        // Test: Fetch All Orders
        System.out.println("\n=== Fetching All Orders ===");
        List<Order> orders = getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            System.out.println("Orders retrieved:");
            for (Order order : orders) {
                System.out.println("OrderID: " + order.getOrderID());
                System.out.println("Time: " + order.getTime());
                System.out.println("TotalAmount: " + order.getTotalAmount());
                System.out.println("CustomerID: " + order.getCustomerID());
                System.out.println("Status: " + order.getStatus());
                System.out.println("ShipperID: " + order.getShipperID());
                System.out.println("------------------------------");
            }
        }

        // Test: Update Shipper ID
//        System.out.println("\n=== Updating Shipper ID ===");
//        boolean shipperUpdated = updateShipperID(12345, 10);
//        if (shipperUpdated) {
//            System.out.println("Shipper ID updated successfully for OrderID: " + 12345);
//        } else {
//            System.out.println("Failed to update Shipper ID.");
//        }
        System.out.println("\n=== Testing Completed ===");
    }

    // Fetch all orders of a given customer ID
    public List<Order> getOrdersByCustomerID(int customerID) {
        List<Order> customerOrders = new ArrayList<>();

        // Fetch all orders
        List<Order> allOrders = getAllOrders();

        // Filter orders by customer ID
        for (Order order : allOrders) {
            if (order.getCustomerID() == customerID) {
                customerOrders.add(order);
            }
        }

        return customerOrders;
    }

    public Order getOrderById(int orderID) {
        // Retrieve order by ID
        try {
            URL url = new URL(BASE_URL + "/" + orderID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // Parse the JSON into an Order object
                    Order order = gson.fromJson(response.toString(), new TypeToken<Order>() {
                    }.getType());
                    return order;
                }
            } else {
                System.err.println("Failed to fetch order. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching order by ID: " + e.getMessage());
        }
        return null;
    }

}
