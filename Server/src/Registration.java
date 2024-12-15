package Server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Registration {

    public static void main(String[] args) throws IOException {
        // Create a server listening on port 8017
        HttpServer server = HttpServer.create(new InetSocketAddress(8017), 0);

        // Register the existing and new endpoints
        server.createContext("/inventory", new InventoryHandler());
        server.createContext("/suppliers", new SupplierHandler());
        server.createContext("/products", new ProductHandler());
        server.createContext("/employees", new EmployeeHandler());  // New endpoint for Employee
        server.createContext("/managers", new ManagerHandler());    // New endpoint for Manager
        server.createContext("/shipper", new ShipperHandler());
        server.createContext("/orders", new MongoOrderHandler());


        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();

        System.out.println("Server is running on http://localhost:8017/");
        System.out.println("Endpoints: /inventory, /suppliers, /products, /employees, /managers, /shipper");
    }
}
