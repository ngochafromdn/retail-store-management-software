package entities;

public class Account {
    private int accountID;      // Employee ID associated with the account
    private String accountName;   // The name of the account
    private String password;      // The password for the account
    private String AccountType;   // The type of account (e.g., Employee, Manager, Admin)

    // Constructor to initialize all fields
    public Account(int AccountID, String accountName, String password, String AccountType) {
        this.accountID = AccountID;
        this.accountName = accountName;
        this.password = password;
        this.AccountType = AccountType; // Initialize the account type
    }



    // Getter for accountName
    public String getAccountName() {
        return this.accountName;
    }

    // Getter for password
    public String getPassword() {
        return this.password;
    }

    // Getter for accountType
    public String getAccountType() {
        return this.AccountType; // Return the account type
    }

    public int getAccountID() {
        return this.accountID; // Return the account type
    }
}