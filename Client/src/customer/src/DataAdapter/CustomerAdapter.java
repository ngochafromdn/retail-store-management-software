package customer.src.DataAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CustomerAdapter {


    public static void main(String[] args) {
        boolean result = updateCustomerInformation(
                1,
                "Alice Achahaa",
                "987654321",
                "789 Elm Street, Othertown, USA",
                3,
                "DOJI 323223"
        );
        if (result) {
            System.out.println("Customer information updated successfully.");
        } else {
            System.out.println("Failed to update customer information.");
        }
    }

    public static boolean updateCustomerInformation(int customerID, String customerName, String number, String address, int accountID, String bankInformation) {
        HttpURLConnection connection = null;
        try {
            // Define the target URL
            URL url = new URL("http://localhost:8015/customers");
            connection = (HttpURLConnection) url.openConnection();

            // Set up the connection properties
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            // Create the JSON payload
            String jsonInputString = String.format(
                    "{\"CustomerID\": %d, \"Name\": \"%s\", \"Number\": \"%s\", \"Address\": \"%s\", \"AccountID\": %d, \"BankInformation\": \"%s\"}",
                    customerID, customerName, number, address, accountID, bankInformation
            );

            // Write the JSON data to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public List<Map<String, Object>> getCustomerInformationByAccountID(int accountID) {
        String urlString = "http://localhost:8015/customers?AccountID=" + accountID;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();

            String jsonResponse = sb.toString();
            Gson gson = new Gson();
            JsonElement jsonElement = JsonParser.parseString(jsonResponse);

            if (jsonElement.isJsonArray()) {
                // If the response is a JSON array
                return gson.fromJson(jsonResponse, new TypeToken<List<Map<String, Object>>>() {
                }.getType());
            } else if (jsonElement.isJsonObject()) {
                // If the response is a single JSON object, wrap it in a list
                Map<String, Object> singleObject = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>() {
                }.getType());
                return List.of(singleObject);
            } else {
                throw new RuntimeException("Unexpected JSON structure");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
