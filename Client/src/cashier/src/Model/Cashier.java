package cashier.src.Model;

public class Cashier {
    private int cashierID;
    private String name;
    private String number;
    private int field4;

    public Cashier() {
        this.cashierID = 0;
        this.name = "";
        this.number = "";
        this.field4 = 0;
    }

    public Cashier(int cashierID, String name, String number, int field4) {
        this.cashierID = cashierID;
        this.name = name;
        this.number = number;
        this.field4 = field4;
    }

    public int getCashierID() {
        return cashierID;
    }

    public void setCashierID(int cashierID) {
        this.cashierID = cashierID;
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

    public int getField4() {
        return field4;
    }

    public void setField4(int field4) {
        this.field4 = field4;
    }

    @Override
    public String toString() {
        return "Cashier{" +
                "cashierID=" + cashierID +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", field4=" + field4 +
                '}';
    }
}
