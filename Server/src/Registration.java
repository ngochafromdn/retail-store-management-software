package Server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Registration {

    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        // Create a server listening on port 8015
        server = HttpServer.create(new InetSocketAddress(8015), 0);

        // Register the existing and new endpoints
        server.createContext("/inventory", new InventoryHandler());
        server.createContext("/suppliers", new SupplierHandler());
        server.createContext("/products", new ProductHandler());
        server.createContext("/employees", new EmployeeHandler());  // New endpoint for Employee
        server.createContext("/managers", new ManagerHandler());    // New endpoint for Manager
        server.createContext("/shipper", new ShipperHandler());
        server.createContext("/orders", new MongoOrderHandler());
        server.createContext("/customers", new CustomerHandler());
        server.createContext("/accounts", new Server.AccountHandler());

        // Add shutdown hook for disconnecting the server gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the server...");
            if (server != null) {
                server.stop(0);
                System.out.println("Server stopped.");
            }
        }));

        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running on http://localhost:8015/");
        System.out.println("Endpoints: /inventory, /suppliers, /products, /employees, /managers, /shipper, /orders");
    }
}
