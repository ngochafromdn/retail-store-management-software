package adapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class LoginDataAdapter {
    private static final String BASE_URL = "http://localhost:8015";

    public Map<String, Object> verifyLogin(String accountName, String password) {
        try {
            URL url = new URL(BASE_URL + "/accounts?accountName=" + accountName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            conn.disconnect();

            // Parse the JSON response
            Gson gson = new Gson();
            JsonElement jsonElement = JsonParser.parseString(response.toString());
            Map<String, Object> accountData = gson.fromJson(jsonElement, Map.class);

            // Verify password and return account data if valid
            if (accountData != null && accountData.containsKey("password")) {
                String storedPassword = (String) accountData.get("password");
                if (password.equals(storedPassword)) {
                    return accountData;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}