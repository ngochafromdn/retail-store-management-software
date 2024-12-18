CREATE TABLE "Account" (
    "AccountID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "AccountName" TEXT,
    "Password" TEXT,
    "AccountType" TEXT
);

CREATE TABLE "Customer" (
    "CustomerID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "Name" TEXT NOT NULL,
    "Number" TEXT NOT NULL,
    "Address" TEXT,
    "AccountID" INTEGER,
    "BankInformation" TEXT,
    FOREIGN KEY ("AccountID") REFERENCES "Account"("AccountID")
);

CREATE TABLE "Employee" (
    "EmployeeID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "Name" TEXT NOT NULL,
    "Number" TEXT NOT NULL,
    "Field4" INTEGER
);

CREATE TABLE "Inventory" (
    "InventoryID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "ProductID" INTEGER,
    "QuantityReceived" INTEGER NOT NULL,
    "Date" DATE NOT NULL,
    "SupplierID" INTEGER,
    FOREIGN KEY ("ProductID") REFERENCES "Product"("product_id"),
    FOREIGN KEY ("SupplierID") REFERENCES "Supplier"("SupplierID")
);

CREATE TABLE "Manager" (
    "ManagerID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "Name" TEXT NOT NULL,
    "Address" TEXT NOT NULL,
    "Field4" INTEGER
);

CREATE TABLE "Product" (
    "product_id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "Name" TEXT NOT NULL,
    "UnitPrice" DECIMAL(10, 2) NOT NULL,
    "Quantity" INTEGER NOT NULL
);

CREATE TABLE "Shipper" (
    "ShipperID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "Name" TEXT NOT NULL,
    "Team" TEXT,
    "Description" TEXT
);

CREATE TABLE "Supplier" (
    "SupplierID" INTEGER PRIMARY KEY AUTOINCREMENT,
    "Name" TEXT NOT NULL
);

-- Inferred relationships:
-- 1. Customers are linked to Accounts via AccountID.
-- 2. Inventory references both Product and Supplier.
-- 3. Managers, Employees, and Shippers are independent for now, as no direct link is defined.
-- 4. Products are managed in Inventory.

-- Diagram Outline (for tools like dbdiagram.io):

/*
Table Account {
    AccountID int [pk, increment]
    AccountName varchar
    Password varchar
    AccountType varchar
}

Table Customer {
    CustomerID int [pk, increment]
    Name varchar
    Number varchar
    Address varchar
    AccountID int [ref: > Account.AccountID]
    BankInformation varchar
}

Table Employee {
    EmployeeID int [pk, increment]
    Name varchar
    Number varchar
    Field4 int
}

Table Inventory {
    InventoryID int [pk, increment]
    ProductID int [ref: > Product.product_id]
    QuantityReceived int
    Date date
    SupplierID int [ref: > Supplier.SupplierID]
}

Table Manager {
    ManagerID int [pk, increment]
    Name varchar
    Address varchar
    Field4 int
}

Table Product {
    product_id int [pk, increment]
    Name varchar
    UnitPrice decimal
    Quantity int
}

Table Shipper {
    ShipperID int [pk, increment]
    Name varchar
    Team varchar
    Description varchar
}

Table Supplier {
    SupplierID int [pk, increment]
    Name varchar
}
*/
