# Order and Inventory Management System

## Overview
The **Order and Inventory Management System** is designed to assist employees in efficiently managing customer orders and inventory. This system includes functionalities for:
- **Order Management**: Viewing orders, adding new orders, paying old debts, and recording payment status.
- **Inventory Management**: Viewing inventory details from suppliers, updating inventory after orders, and checking current product status.

## Features
### Order Management
- **View Orders**: Employees can search for existing orders by customer phone number or order ID.
- **Add New Orders**: Employees can create new orders by selecting products, specifying quantities, and generating order IDs.
- **Manage Payment**: Employees can mark orders as paid, or set them as debts to be paid later. Additionally, the system can handle old debt payments by entering the relevant order ID.
- **Order Completion**: Finalized orders include information on total amounts, payment status, and order timestamps.
- **Pay old debt** : Finalized old debt.

### Inventory Management
- **View Inventory**: Allows employees to view inventory from multiple suppliers, including product quantities and supplier details.
- **Update Inventory**: When an order is placed, the system updates inventory automatically to reflect the current stock levels.
- **Check Product Status**: Employees can see the real-time status of current products, including quantities and supplier information.

## System Structure
The project follows an **MVC (Model-View-Controller)** and **3-layer architecture**:
- **Model**: Represents data structures like `Customer`, `Order`, `Product`, `Inventory`, and `Supplier`.
- **View**: GUI components that provide interactive forms for customer and order management.
- **Controller**: Handles business logic and user actions, including managing customer information, handling orders, and interacting with the database.

## Installation
To set up and run the system:
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/order-inventory-management.git

# structure

src
├── controllers                  
│   ├── CustomerController.java     
│   ├── OrderController.java        
│   ├── InventoryController.java      
│   └── Other Controllers as needed      
├── dataaccess (Repository Layer)   
│   ├── DatabaseConnection.java     
│   ├── CustomerRepository.java     
│   ├── OrderRepository.java        
│   ├── ProductRepository.java      
│   └── Other Repositories as needed
├── entities                     
│   ├── Customer.java               
│   ├── Order.java                  
│   ├── Product.java                
│   ├── Supplier.java               
│   └── Other Entities as needed
├── services                     
│   ├── CustomerService.java           
│   ├── OrderService.java        
│   ├── InventoryService.java         
│   └── Other Services as needed
└── views                         
    ├── CustomerView.java           
    ├── OrderView.java              
    └── InitialOrderView.java            


## Requirements

### Technical Requirements
1. **Java Development Kit (JDK)**: Version 8 or above is recommended to compile and run the application.
2. **Database**: SQLite is used as the database management system.
   - Ensure you have the `sqlite-jdbc` driver included in the project dependencies.
   - Database file: `retail-store-1.db` (or your designated database name) located in the project's root directory.
3. **IDE**: Java-compatible IDE (e.g., IntelliJ IDEA, Eclipse, NetBeans) for easy navigation, compilation, and testing of code.
