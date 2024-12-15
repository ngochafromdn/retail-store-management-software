// Source code is decompiled from a .class file using FernFlower decompiler.
package entities;

public class Product {
    private int productId;
    private String name;
    private double unitPrice;
    private int quantity;

    public Product(int var1, String var2, double var3, int var5) {
        this.productId = var1;
        this.name = var2;
        this.unitPrice = var3;
        this.quantity = var5;
    }

    public int getProductId() {
        return this.productId;
    }

    public String getName() {
        return this.name;
    }

    public double getUnitPrice() {
        return this.unitPrice;
    }

    public int getQuantity() {
        return this.quantity;
    }
}
