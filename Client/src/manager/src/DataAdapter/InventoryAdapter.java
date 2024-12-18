package manager.src.DataAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.src.Model.Inventory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter {

    private static final String INVENTORY_URL = "http://localhost:8015/inventory"; // URL for your inventory endpoint
    private static final Gson gson = new Gson(); // Gson instance for JSON parsing


    // GET

    public static List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        try {
            // Define the URL for the GET request
            URL url = new URL("http://localhost:8015/inventory"); // Replace with your endpoint
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            connection.setRequestMethod("GET");

            // Check the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the JSON response into a list of raw objects
                    List<Object> rawJsonList = gson.fromJson(response.toString(), new TypeToken<List<Object>>() {
                    }.getType());

                    // Manually construct Inventory objects using key-value pairs
                    for (Object rawObject : rawJsonList) {
                        String rawJson = gson.toJson(rawObject); // Convert raw object to JSON string
                        var jsonObject = gson.fromJson(rawJson, java.util.Map.class); // Convert to a Map

                        int inventoryID = ((Double) jsonObject.get("InventoryID")).intValue();
                        int productID = ((Double) jsonObject.get("ProductID")).intValue();
                        int quantityReceived = ((Double) jsonObject.get("QuantityReceived")).intValue();
                        String date = jsonObject.get("Date").toString().replaceAll("\"", "");
                        int supplierID = ((Double) jsonObject.get("SupplierID")).intValue();

                        Inventory inventory = new Inventory(inventoryID, productID, quantityReceived, date, supplierID);
                        inventoryList.add(inventory);
                    }
                }
            } else {
                System.err.println("Failed to fetch inventory. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching inventory: " + e.getMessage());
        }
        return inventoryList; // Return the list of inventory objects
    }


    // Function to add a new inventory (POST request)

    public static void addInventory(int productID, int quantityReceived, String date, int supplierID) {
        try {
            // Define the URL of the server endpoint
            URL url = new URL("http://localhost:8015/inventory");

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            connection.setRequestMethod("POST");

            // Set headers
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            // Create JSON payload
            String jsonPayload = String.format(
                    "{ \"ProductID\": %d, \"QuantityReceived\": %d, \"Date\": \"%s\", \"SupplierID\": %d }",
                    productID, quantityReceived, date, supplierID
            );

            // Write JSON payload to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Inventory item added successfully.");
            } else {
                System.out.println("Failed to add inventory item. Response code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            System.err.println("Error adding inventory: " + e.getMessage());
        }
    }

}
