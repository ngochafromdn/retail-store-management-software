//package controllers;
//
//import views.InventoryAndProductView;
//import DataAdapter.Inventory;
//import DataAdapter.Order;
//import DataAdapter.Product;
//
//import javax.swing.SwingUtilities;
//import java.sql.Date;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//
//public class InventoryController {
//    private InventoryView view;
//    private DataAccess dataAccess;
//    private String type;
//
//    public InventoryAndProductController(String type) {
//        this.dataAdapter = new DataAdapter();
//        this.type = type; // type
//    }
//
//
//    public void initialize() {
//        SwingUtilities.invokeLater(() -> {
//            view = new InventoryAndProductView(this);
//            view.createAndShowGUI();
//        });
//    }
//
