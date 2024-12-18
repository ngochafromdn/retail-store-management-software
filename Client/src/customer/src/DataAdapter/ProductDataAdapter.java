package customer.src.DataAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ProductDataAdapter {

    private static final String BASE_URL = "http://localhost:8015/products"; // Replace with your product endpoint
    private static final Gson gson = new Gson();

    public static Map<String, Object> getProductByID(int productID) {
        try {
            // Construct the URL
            URL url = new URL(BASE_URL); // No need to append productID as we fetch all products
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

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

                    // Parse the JSON response into a list of maps (each map represents a product)
                    List<Map<String, Object>> products = gson.fromJson(response.toString(), new TypeToken<List<Map<String, Object>>>() {
                    }.getType());

                    // Search for the product with the specified productID
                    for (Map<String, Object> product : products) {
                        int id = ((Double) product.get("product_id")).intValue();
                        if (id == productID) {
                            return product; // Return the matching product as a Map
                        }
                    }

                    System.err.println("Product with ID " + productID + " not found.");
                }
            } else {
                System.err.println("Failed to fetch products. Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching product by ID: " + e.getMessage());
        }
        return null; // Return null if the product is not found or fetch fails
    }


    public static double getProductPrice(int productID) {
        // Fetch product details by ID
        Map<String, Object> product = getProductByID(productID);
        if (product != null) {
            // Extract the price field from the product map
            if (product.containsKey("price")) {
                return ((Number) product.get("price")).doubleValue();
            } else {
                System.err.println("Price field not found for product ID: " + productID);
            }
        } else {
            System.err.println("Product not found with ID: " + productID);
        }
        return 0.0; // Return 0.0 if product not found or price is missing
    }

    public static String getProductName(int productID) {
        // Fetch product details by ID
        Map<String, Object> product = getProductByID(productID);
        if (product != null) {
            // Extract the product name field from the product map
            if (product.containsKey("name")) {
                return product.get("name").toString();
            } else {
                System.err.println("Product name field not found for product ID: " + productID);
            }
        } else {
            System.err.println("Product not found with ID: " + productID);
        }
        return "Unknown Product"; // Return a default value if product not found or name is missing
    }


    // Method to update the number of products
    public static boolean updateProductQuantity(int productId, int newQuantity) {
        try {
            URL url = new URL(BASE_URL + "/" + productId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create JSON payload
            String jsonPayload = String.format("{\"product_id\": %d, \"quantity\": %d}", productId, newQuantity);
            System.out.println(jsonPayload);

            // Write payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            System.err.println("Error updating product quantity: " + e.getMessage());
        }
        return false;
    }


    public static void main(String[] args) {
        System.out.println("===== Testing ProductDataAdapter =====");

        int testProductID = 1; // Replace with a valid product ID from your server
        int invalidProductID = 9999; // Non-existent product ID for testing
        int newQuantity = 50; // Quantity to update

        // 1. Test getProductByID
        System.out.println("\n--- Test: getProductByID ---");
        Map<String, Object> product = getProductByID(testProductID);
        if (product != null) {
            System.out.println("Product details: " + product);
        } else {
            System.out.println("Product with ID " + testProductID + " not found.");
        }

        // 2. Test getProductByID with invalid ID
        System.out.println("\n--- Test: getProductByID (Invalid ID) ---");
        Map<String, Object> invalidProduct = getProductByID(invalidProductID);
        if (invalidProduct == null) {
            System.out.println("Product with ID " + invalidProductID + " correctly not found.");
        }

        // 3. Test getProductPrice
        System.out.println("\n--- Test: getProductPrice ---");
        double price = getProductPrice(testProductID);
        System.out.println("Product price for ID " + testProductID + ": " + price);

        // 4. Test getProductPrice with invalid ID
        System.out.println("\n--- Test: getProductPrice (Invalid ID) ---");
        double invalidPrice = getProductPrice(invalidProductID);
        if (invalidPrice == 0.0) {
            System.out.println("Price correctly returned 0.0 for invalid product ID.");
        }

        // 5. Test getProductName
        System.out.println("\n--- Test: getProductName ---");
        String productName = getProductName(testProductID);
        System.out.println("Product name for ID " + testProductID + ": " + productName);

        // 6. Test getProductName with invalid ID
        System.out.println("\n--- Test: getProductName (Invalid ID) ---");
        String invalidName = getProductName(invalidProductID);
        System.out.println("Product name for invalid ID: " + invalidName);

        // 7. Test updateProductQuantity
        System.out.println("\n--- Test: updateProductQuantity ---");
        boolean updateSuccess = updateProductQuantity(testProductID, newQuantity);
        if (updateSuccess) {
            System.out.println("Successfully updated product quantity for ID " + testProductID);
        } else {
            System.out.println("Failed to update product quantity for ID " + testProductID);
        }

        // 8. Test updateProductQuantity with invalid ID
        System.out.println("\n--- Test: updateProductQuantity (Invalid ID) ---");
        boolean invalidUpdate = updateProductQuantity(invalidProductID, newQuantity);
        if (!invalidUpdate) {
            System.out.println("Correctly failed to update product with invalid ID " + invalidProductID);
        }

        System.out.println("\n===== Tests Completed =====");
    }

}

