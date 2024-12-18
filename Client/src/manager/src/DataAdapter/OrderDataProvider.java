package manager.src.DataAdapter;

import manager.src.Model.Order;

import java.util.List;

public interface OrderDataProvider {
    List<Order> getAllOrders();
}
