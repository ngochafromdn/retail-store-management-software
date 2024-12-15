INSERT INTO Account (AccountID, AccountName, Password, EmployeeID) VALUES (1, 'admin', 'password123', 1);
INSERT INTO Account (AccountID, AccountName, Password, EmployeeID) VALUES (2, 'user1', 'mypassword', 2);

-- Sample data for Customer table
INSERT INTO Customer (CustomerID, Name, Number) VALUES (1, 'Alice Smith', '123456789');
INSERT INTO Customer (CustomerID, Name, Number) VALUES (2, 'Bob Johnson', '987654321');

-- Sample data for Employee table
INSERT INTO Employee (EmployeeID, Name, Number) VALUES (1, 'John Doe', '111222333');
INSERT INTO Employee (EmployeeID, Name, Number) VALUES (2, 'Jane Roe', '444555666');

-- Sample data for Inventory table
INSERT INTO Inventory (InventoryID, ProductID, QuantityReceived, Date, SupplierID) VALUES (1, 1, 100, '2024-01-01', 1);
INSERT INTO Inventory (InventoryID, ProductID, QuantityReceived, Date, SupplierID) VALUES (2, 2, 200, '2024-02-01', 2);

-- Sample data for Order table
INSERT INTO "Order" (OrderID, Time, TotalAmount, CustomerID) VALUES (1, '2024-11-07 10:00:00', 150.00, 1);
INSERT INTO "Order" (OrderID, Time, TotalAmount, CustomerID) VALUES (2, '2024-11-07 11:00:00', 250.00, 2);

-- Sample data for OrderItem table
INSERT INTO OrderItem (OrderItemID, OrderID, ProductID, Quantity, Price) VALUES (1, 1, 1, 2, 50.00);
INSERT INTO OrderItem (OrderItemID, OrderID, ProductID, Quantity, Price) VALUES (2, 2, 2, 3, 75.00);

-- Sample data for OrderTable table
INSERT INTO OrderTable (OrderID, Time, DateTime, CustomerID, Status) VALUES (1, '10:00 AM', '2024-11-07', 1, 'debt');
INSERT INTO OrderTable (OrderID, Time, DateTime, CustomerID, Status) VALUES (2, '11:00 AM', '2024-11-07', 2, 'paid');

-- Sample data for Product table
INSERT INTO Product (ProductID, Name, UnitPrice, Quantity) VALUES (1, 'Laptop', 750.00, 50);
INSERT INTO Product (ProductID, Name, UnitPrice, Quantity) VALUES (2, 'Smartphone', 500.00, 100);

-- Sample data for Supplier table
INSERT INTO Supplier (SupplierID, Name) VALUES (1, 'Tech Supplier Co.');
INSERT INTO Supplier (SupplierID, Name) VALUES (2, 'Gadget Distributors Inc.');
