package Server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseWebService {

    public static void main(String[] args) throws IOException {
        // Create an HttpServer instance and bind it to port 8010
        HttpServer server = HttpServer.create(new InetSocketAddress(8010), 0);

        // Define endpoints for both SQLite and MongoDB
        server.createContext("/sqlite-products", new SQLiteHandler());

        // Start the server
        server.setExecutor(null); // Use default executor
        server.start();

        System.out.println("Server is running on http://localhost:8010/");
    }

    // SQLite Handler
    static class SQLiteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get query parameters
            String response = getSQLiteProducts();

            // Send response
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private String getSQLiteProducts() {
            StringBuilder jsonResponse = new StringBuilder("[");
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:retail-store-1.db")) {
                String sql = "SELECT * FROM Product";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    jsonResponse.append("{")
                            .append("\"product_id\":").append(rs.getInt("product_id")).append(",")
                            .append("\"name\":\"").append(rs.getString("name")).append("\",")
                            .append("\"price\":").append(rs.getDouble("unitprice")).append(",")
                            .append("\"quantity\":").append(rs.getInt("quantity"))
                            .append("},");
                }
                if (jsonResponse.length() > 1) {
                    jsonResponse.deleteCharAt(jsonResponse.length() - 1); // Remove trailing comma
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            jsonResponse.append("]");
            return jsonResponse.toString();
        }
    }


    }
