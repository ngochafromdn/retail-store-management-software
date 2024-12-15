package Model;

public class Customer {

    private int customerID; // Corresponds to "CustomerID"
    private String name; // Corresponds to "Name"
    private String number; // Corresponds to "Number"
    private String address; // Corresponds to "Address"
    private int accountID; // Corresponds to "AccountID"

    // Default constructor
    public Customer() {
        this.customerID = 0;
        this.name = "";
        this.number = "";
        this.address = "";
        this.accountID = 0;
    }

    // Parameterized constructor
    public Customer(int customerID, String name, String number, String address, int accountID) {
        this.customerID = customerID;
        this.name = name;
        this.number = number;
        this.address = address;
        this.accountID = accountID;
    }

    // Getters and setters
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    // toString method
    @Override
    public String toString() {
        return "Customer{" +
                "customerID=" + customerID +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", address='" + address + '\'' +
                ", accountID=" + accountID +
                '}';
    }
}
