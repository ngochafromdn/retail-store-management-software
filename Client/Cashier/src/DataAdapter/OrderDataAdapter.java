package DataAdapter;

import Model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class OrderDataAdapter {

    private static final String BASE_URL = "http://localhost:8017/orders"; // Replace with your orders endpoint
    private static final Gson gson = new Gson();

    // Create a new order (POST)
    public static int createNewOrder(Order order) {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = gson.toJson(order);
            System.out.println(jsonPayload);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String response = reader.readLine();
                    return Integer.parseInt(response); // Assuming server returns new OrderID
                }
            } else {
                System.err.println("Failed to create order. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
        }
        return -1; // Return -1 if order creation fails
    }

    public static void main(String[] args) {
        // Create a sample Order
        Map<String, Object> item1 = new HashMap<>();
        item1.put("ProductID", 101);
        item1.put("Quantity", 2);
        item1.put("Price", 500.0);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("ProductID", 102);
        item2.put("Quantity", 1);
        item2.put("Price", 300.0);

        List<Map<String, Object>> orderItems = Arrays.asList(item1, item2);

        Order order = new Order("2024-12-15 12:00", 1300.0, 1, "paid", 101, orderItems);
        int orderID = createNewOrder(order);
        if (orderID != -1) {
            System.out.println("Created new order with OrderID: " + orderID);
        }
    }
}
