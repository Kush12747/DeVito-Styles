package learn.DeVitoStyles.data.interfaces.PaymentInterfaces;

import learn.DeVitoStyles.models.Checkout.Order;

public interface OrderRepository {
    Order add(Order order);

    Order findById(int orderId);

    Order findByOrderNumber(String orderNumber);

    boolean updateStatus(int orderId, String status);
}
