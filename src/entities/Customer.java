// Source code is decompiled from a .class file using FernFlower decompiler.
package entities;

public class Customer {
    private int customerId;
    private String name;
    private String number;
    private String status;

    public Customer(String var1, String var2) {
        this.name = var1;
        this.number = var2;
    }

    public int getCustomerId() {
        return this.customerId;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }

    public String getStatus() {
        return this.status;
    }
}
