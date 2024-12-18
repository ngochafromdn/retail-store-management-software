import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseWebService {

    public static void main(String[] args) throws IOException {
        // Create an HttpServer instance and bind it to port 8010
        HttpServer server = HttpServer.create(new InetSocketAddress(8010), 0);

        // Define endpoints
        server.createContext("/search", new SearchHandler());

        // Start the server
        server.setExecutor(null); // Use default executor
        server.start();

        System.out.println("Server is running on http://localhost:8010/");
    }

    // Handler for the /search endpoint
    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Parse query parameters
                Map<String, String> params = parseQueryParams(exchange.getRequestURI().getRawQuery());

                // Log parsed parameters for debugging
                System.out.println("Parsed Query Parameters: " + params);

                // Generate the HTML response
                String response = getFilteredProductsAsHTML(params);

                // Send response with Content-Type as text/html
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }
            } else {
                // Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }

        // Parse query parameters from URI with URL decoding
        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> params = new HashMap<>();
            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0].trim(), StandardCharsets.UTF_8);
                        String value = URLDecoder.decode(keyValue[1].trim(), StandardCharsets.UTF_8);
                        params.put(key, value);
                    } else {
                        // Handle cases where there's a key without a value
                        String key = URLDecoder.decode(keyValue[0].trim(), StandardCharsets.UTF_8);
                        params.put(key, "");
                    }
                }
            }
            return params;
        }

        // Generate the filtered product list in HTML format
        private String getFilteredProductsAsHTML(Map<String, String> params) {
            StringBuilder htmlResponse = new StringBuilder();

            // HTML Boilerplate
            htmlResponse.append("<!DOCTYPE html>")
                    .append("<html>")
                    .append("<head>")
                    .append("<title>Product Search</title>")
                    .append("<style>")
                    .append("table { width: 80%; margin: 20px auto; border-collapse: collapse; }")
                    .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }")
                    .append("th { background-color: #f4f4f4; }")
                    .append("tr:nth-child(even) { background-color: #f9f9f9; }")
                    .append("tr:hover { background-color: #f1f1f1; }")
                    .append("</style>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h2 style='text-align:center;'>Filtered Product List</h2>")
                    .append("<table>")
                    .append("<thead>")
                    .append("<tr>")
                    .append("<th>Product ID</th>")
                    .append("<th>Name</th>")
                    .append("<th>Price</th>")
                    .append("<th>Quantity</th>")
                    .append("</tr>")
                    .append("</thead>")
                    .append("<tbody>");

            // Build SQL query based on parameters using PreparedStatement
            String baseQuery = "SELECT * FROM Product";
            StringBuilder whereClause = new StringBuilder();
            Map<Integer, Object> parameters = new HashMap<>();
            int paramIndex = 1;

            // Filter by product_id
            if (params.containsKey("product_id")) {
                if (whereClause.length() == 0) {
                    whereClause.append(" WHERE ");
                } else {
                    whereClause.append(" AND ");
                }
                whereClause.append("product_id = ?");
                try {
                    int productId = Integer.parseInt(params.get("product_id"));
                    parameters.put(paramIndex++, productId);
                } catch (NumberFormatException e) {
                    // Invalid product_id, set a value that will match nothing
                    whereClause.append(" AND 1=0"); // Force no results
                    System.err.println("Invalid product_id provided: " + params.get("product_id"));
                }
            }

            // Filter by price
            if (params.containsKey("price")) {
                String priceFilter = params.get("price");
                System.out.print("Price filter: " + priceFilter);
                String operator = "";
                String valueStr = "";
                if (priceFilter.startsWith("<=")) {
                    operator = "<=";
                    valueStr = priceFilter.substring(2).trim();
                } else if (priceFilter.startsWith(">=")) {
                    operator = ">=";
                    valueStr = priceFilter.substring(2).trim();
                } else if (priceFilter.startsWith("<")) {
                    operator = "<";
                    valueStr = priceFilter.substring(1).trim();
                } else if (priceFilter.startsWith(">")) {
                    operator = ">";
                    valueStr = priceFilter.substring(1).trim();
                } else {
                    // Invalid operator, ignore this filter
                    System.err.println("Invalid price operator in filter: " + priceFilter);
                }

                if (!operator.isEmpty() && !valueStr.isEmpty()) {
                    try {
                        double priceValue = Double.parseDouble(valueStr);
                        if (whereClause.length() == 0) {
                            whereClause.append(" WHERE ");
                        } else {
                            whereClause.append(" AND ");
                        }
                        whereClause.append("UnitPrice ").append(operator).append(" ?");
                        parameters.put(paramIndex++, priceValue);
                    } catch (NumberFormatException e) {
                        // Invalid price value, ignore this filter or handle accordingly
                        // For now, ignoring the price filter
                        System.err.println("Invalid price value in filter: " + valueStr);
                    }
                }
            }

            String finalQuery = baseQuery + whereClause;

            // Debug: Print the final query and parameters
            System.out.println("Final SQL Query: " + finalQuery);
            System.out.println("Parameters: " + parameters);

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:retail-store-1.db")) {
                if (conn == null) {
                    throw new SQLException("Failed to establish a database connection.");
                }

                PreparedStatement pstmt = conn.prepareStatement(finalQuery);

                for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
                    if (entry.getValue() instanceof Integer) {
                        pstmt.setInt(entry.getKey(), (Integer) entry.getValue());
                    } else if (entry.getValue() instanceof Double) {
                        pstmt.setDouble(entry.getKey(), (Double) entry.getValue());
                    } else {
                        pstmt.setObject(entry.getKey(), entry.getValue());
                    }
                }

                ResultSet rs = pstmt.executeQuery();

                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    htmlResponse.append("<tr>")
                            .append("<td>").append(rs.getInt("product_id")).append("</td>")
                            .append("<td>").append(rs.getString("Name")).append("</td>")
                            .append("<td>").append(String.format("$%.2f", rs.getDouble("UnitPrice"))).append("</td>")
                            .append("<td>").append(rs.getInt("Quantity")).append("</td>")
                            .append("</tr>");
                }

                if (!hasResults) {
                    htmlResponse.append("<tr><td colspan='4' style='text-align:center;'>No products found</td></tr>");
                }

                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                htmlResponse.append("<tr><td colspan='4' style='text-align:center;'>Error fetching data</td></tr>");
            }

            htmlResponse.append("</tbody>")
                    .append("</table>")
                    .append("</body>")
                    .append("</html>");

            return htmlResponse.toString();
        }
    }
}
