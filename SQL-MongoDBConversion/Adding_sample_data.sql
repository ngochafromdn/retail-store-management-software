-- Inserting sample data into Account table
INSERT INTO Account (AccountID, AccountName, Password, AccountType)
VALUES
(1, 'jdoe', 'password123', 'Customer'),
(2, 'asmith', 'passw0rd!', 'Cashier'),
(3, 'mbrown', 'manager321', 'Manager');

-- Inserting sample data into Customer table
INSERT INTO Customer (CustomerID, Name, Number, Address, AccountID)
VALUES
(101, 'John Doe', '555-1234', '123 Main St, Cityville', 1),
(102, 'Alice Smith', '555-5678', '456 Oak Rd, Townville', 1);

-- Inserting sample data into Cashier table
INSERT INTO Cashier (EmployeeID, Name, Number, AccountID)
VALUES
(201, 'Jane Roe', '555-8765', 2);

-- Inserting sample data into Manager table
INSERT INTO Manager (ManagerID, Name, Address, AccountID)
VALUES
(301, 'Bob Brown', '789 Pine Ln, Citytown', 3);

-- Inserting sample data into Product table
INSERT INTO Product (product_id, Name, UnitPrice, Quantity)
VALUES
(401, 'Widget A', 10.99, 100),
(402, 'Widget B', 15.49, 50),
(403, 'Widget C', 20.00, 200);

-- Inserting sample data into Supplier table
INSERT INTO Supplier (SupplierID, Name)
VALUES
(601, 'XYZ Supplies Inc.'),
(602, 'ABC Products Ltd.');

-- Inserting sample data into Inventory table
INSERT INTO Inventory (InventoryID, ProductID, QuantityReceived, Date, SupplierID)
VALUES
(501, 401, 50, '2024-12-01', 601),
(502, 402, 30, '2024-12-05', 602),
(503, 403, 100, '2024-12-10', 601);

-- Inserting sample data into Shipper table
INSERT INTO Shipper (ShipperID, Name, Team, Description)
VALUES
(701, 'FastShippers', 'A Team', 'Handles urgent deliveries'),
(702, 'Reliable Log', 'B Team', 'General logistics and shipping');
