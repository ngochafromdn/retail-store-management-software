package cashier.src.Model;

public class Manager {
    private int managerID;
    private String name;
    private String address;
    private int field4;

    public Manager() {
        this.managerID = 0;
        this.name = "";
        this.address = "";
        this.field4 = 0;
    }

    public Manager(int managerID, String name, String address, int field4) {
        this.managerID = managerID;
        this.name = name;
        this.address = address;
        this.field4 = field4;
    }

    public int getManagerID() {
        return managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getField4() {
        return field4;
    }

    public void setField4(int field4) {
        this.field4 = field4;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "managerID=" + managerID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", field4=" + field4 +
                '}';
    }
}
