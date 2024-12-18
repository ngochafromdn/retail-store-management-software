# Retail Store Management Software (Project 2)

## Overview

This software redesigns the retail store management system from Project 1, applying three architectural patterns: 3-layer, MVC, and client/server. The system includes web services and desktop-based clients for customers, cashiers, and managers, integrated with both MySQL and MongoDB databases.

## Structure

### Client
- **Main.java**: Entry point to run all clients (customer, cashier, manager).
- **cashier**: Cashier-related features (order processing, inventory management).
- **login**: Login functionalities for different users (customer, cashier, manager).
- **utils**: Helper classes for UI components and utilities.
- **adapter**: Used to interface between data and application.
- **customer**: Customer functionalities (order history, self-ordering, profile updates).
- **manager**: Manager functionalities (inventory refills, sales reporting).

### Server
- **AccountHandler.java**: Manages user authentication.
- **ManagerHandler.java**: Handles manager-specific requests.
- **Registration.java**: Entry point for starting the server.
- **CustomerHandler.java**: Handles customer-specific requests.
- **MongoDBConnection.java**: Connects to MongoDB for customer data.
- **SQLConnection.java**: Connects to MySQL for product and user data.
- **EmployeeHandler.java**: Handles employee-specific data.
- **MongoOrderHandler.java**: Manages order data in MongoDB.
- **ShipperHandler.java**: Manages shipping-related tasks.
- **InventoryHandler.java**: Manages inventory operations.
- **ProductHandler.java**: Manages product-related tasks.
- **SupplierHandler.java**: Handles supplier-related data.

### SQL-MongoDBConversion
- **Adding_sample_data.sql**: SQL script to add sample data into MySQL.
- **Create_database.sql**: SQL script for creating the required database.
- **SQLToMongoTransfer.java**: Transfers data from MySQL to MongoDB.

## How to Run

1. **Server**:
   - Run `Registration.java` to start the server.
   - Ensure that MySQL and MongoDB are configured and running.
   
2. **Client**:
   - Run `Main.java` to start the desktop clients.
   - The web browser is the default client for customers.

## Database

- **MySQL**: Used for storing product and user information.
- **MongoDB**: Stores customer and order information.

## Design Patterns Implemented

- **Adapter**: Used to adapt between different data sources and formats.
- **General Hierarchy (Document Model)**: Used for storing and structuring documents in MongoDB.
- **Builder/Generator**: Applied in order generation.
- **Facade**: Simplifies complex systems for the client (e.g., inventory and order management).
- **Proxy**: Used for controlling access to sensitive data.
- **Abstraction-Occurrence**: Applied for user roles and actions.
- **Player-Role**: Applied to users with different roles (Customer, Cashier, Manager).
- **Singleton**: Ensures a single instance of shared resources.
- **Observer**: Used for updating the manager’s dashboard in real time when an order or inventory is updated.

