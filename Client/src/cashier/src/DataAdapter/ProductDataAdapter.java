package cashier.src.DataAdapter;

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

    private static final String BASE_URL = "http://localhost:8015/products";
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

    public static double getProductQuantity(int productID) {
        // Fetch product details by ID
        Map<String, Object> product = getProductByID(productID);
        if (product != null) {
            // Extract the price field from the product map
            if (product.containsKey("quantity")) {
                return ((Number) product.get("quantity")).doubleValue();
            } else {
                System.err.println("Price field not found for product ID: " + productID);
            }
        } else {
            System.err.println("Product not found with ID: " + productID);
        }
        return 0.0; // Return 0.0 if product not found or price is missing
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

    }
}

