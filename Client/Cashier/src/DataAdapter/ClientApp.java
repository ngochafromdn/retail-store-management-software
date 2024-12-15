//package DataAdapter;
//
//import Model.Inventory;
//import java.io.IOException;
//
//public class ClientApp {
//
//    public static void main(String[] args) {
//        ClientApp test = new ClientApp();
//        try {
//            test.testAddInventory();  // Test thêm kho
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Hàm test thêm kho (inventory)
//    public void testAddInventory() throws IOException {
//        System.out.println("Running testAddInventory...");
//
//        // Tạo đối tượng Inventory với thông tin kho cần thêm
//        Inventory newInventory = new Inventory();
//        newInventory.setProductID(1);  // ID sản phẩm
//        newInventory.setQuantityReceived(100);  // Số lượng kho nhập vào
//        newInventory.setDate("2024-12-15");  // Ngày nhập kho
//        newInventory.setSupplierID(2001);  // ID nhà cung cấp
//
//        // Gọi phương thức để thêm kho
//        boolean result = product_method.addNewInventory(newInventory);
//
//        // Kiểm tra kết quả và in ra thông báo
//        if (result) {
//            System.out.println("Thêm kho thành công.");
//        } else {
//            System.out.println("Thêm kho thất bại.");
//        }
//    }
//}