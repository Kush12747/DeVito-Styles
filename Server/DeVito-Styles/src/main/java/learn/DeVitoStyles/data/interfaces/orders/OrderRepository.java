package learn.DeVitoStyles.data.interfaces.orders;

import learn.DeVitoStyles.models.Checkout.Order;
import learn.DeVitoStyles.models.Checkout.OrderStatus;

import java.util.List;

public interface OrderRepository {
    Order add(Order order);

    Order findById(int orderId);

    Order findByOrderNumber(String orderNumber);

    boolean updateStatus(int orderId, OrderStatus status);

    List<Order> findByUserId(int userId);
}
