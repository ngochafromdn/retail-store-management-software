// Source code is decompiled from a .class file using FernFlower decompiler.
package entities;

import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private LocalDateTime time;
    private double totalAmount;

    public Order(int var1, LocalDateTime var2, double var3) {
        this.orderId = var1;
        this.time = var2;
        this.totalAmount = var3;
    }

    public int getOrderId() {
        return this.orderId;
    }

    public double getTotalAmount() {
        return this.totalAmount;
    }

    public LocalDateTime getTime() {
        return this.time;
    }
}
