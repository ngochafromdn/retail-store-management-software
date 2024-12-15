CREATE TABLE Account (
    AccountID INTEGER PRIMARY KEY,
    AccountName TEXT NOT NULL,
    Password TEXT NOT NULL,
    EmployeeID INTEGER,
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);

CREATE TABLE Customer (
    CustomerID INTEGER PRIMARY KEY,
    Name TEXT NOT NULL,
    Number TEXT NOT NULL UNIQUE
);

CREATE TABLE Employee (
    EmployeeID INTEGER PRIMARY KEY,
    Name TEXT NOT NULL,
    Number TEXT NOT NULL UNIQUE
);

CREATE TABLE Inventory (
    InventoryID INTEGER PRIMARY KEY,
    ProductID INTEGER,
    QuantityReceived INTEGER NOT NULL,
    Date DATE NOT NULL,
    SupplierID INTEGER,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID)
);

CREATE TABLE [Order] (
    OrderID INTEGER PRIMARY KEY,
    Time DATETIME NOT NULL,
    TotalAmount DECIMAL(10, 2) NOT NULL,
    CustomerID INTEGER,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID)
);

CREATE TABLE OrderItem (
    OrderItemID INTEGER PRIMARY KEY,
    OrderID INTEGER,
    ProductID INTEGER,
    Quantity INTEGER NOT NULL,
    Price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES [Order](OrderID),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

CREATE TABLE Product (
    ProductID INTEGER PRIMARY KEY,
    Name TEXT NOT NULL,
    UnitPrice DECIMAL(10, 2) NOT NULL,
    Quantity INTEGER NOT NULL
);

CREATE TABLE Supplier (
    SupplierID INTEGER PRIMARY KEY,
    Name TEXT NOT NULL
);
