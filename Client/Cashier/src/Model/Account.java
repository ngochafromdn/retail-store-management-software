package Model;
public class Account {

    private int accountID; // Corresponds to "AccountID"
    private String accountName; // Corresponds to "AccountName"
    private String password; // Corresponds to "Password"
    private String accountType; // Corresponds to "AccountType"

    // Default constructor
    public Account() {
        this.accountID = 0;
        this.accountName = "";
        this.password = "";
        this.accountType = "";
    }

    // Parameterized constructor
    public Account(int accountID, String accountName, String password, String accountType) {
        this.accountID = accountID;
        this.accountName = accountName;
        this.password = password;
        this.accountType = accountType;
    }

    // Getters and setters
    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    // toString method
    @Override
    public String toString() {
        return "Account{" +
                "accountID=" + accountID +
                ", accountName='" + accountName + '\'' +
                ", accountType='" + accountType + '\'' +
                '}';
    }
}
