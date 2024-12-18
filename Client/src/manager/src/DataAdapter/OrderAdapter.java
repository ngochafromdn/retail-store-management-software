// OrderAdapter.java
package manager.src.DataAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import manager.src.Model.Order;
import manager.src.Model.OrderItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OrderAdapter implements OrderDataProvider {

    private static final String BASE_URL = "http://localhost:8015/orders";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Order.class, (JsonDeserializer<Order>) (json, typeOfT, context) -> {
                JsonObject jsonObject = json.getAsJsonObject();
                Order order = new Order();

                // Map fields to the Order object
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

    @Override
    public List<Order> getAllOrders() {
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

                    // Parse JSON response into a list of Order objects
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
}
